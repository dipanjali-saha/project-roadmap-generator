package com.project.roadmapgenerator.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.roadmapgenerator.entities.MilestoneEntity;
import com.project.roadmapgenerator.entities.ProjectEntity;
import com.project.roadmapgenerator.entities.TaskEntity;
import com.project.roadmapgenerator.payloads.MilestoneDto;
import com.project.roadmapgenerator.payloads.ProjectDetailsWrapperDto;
import com.project.roadmapgenerator.payloads.ProjectDto;
import com.project.roadmapgenerator.payloads.TaskDto;
import com.project.roadmapgenerator.repositories.MilestoneRepository;
import com.project.roadmapgenerator.repositories.ProjectRepository;
import com.project.roadmapgenerator.repositories.TaskRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private ProjectDetailsMapperService projectDetailsMapperService;
	@Autowired
	private MilestoneRepository milestoneRepository;
	@Autowired
	private TaskRepository taskRepository;

	static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	static final String PROJECT_OVERVIEW_SHEET = "Project Overview";
	static final String PROJECT_NAME_LABEL = "Project Name";
	static final String START_DATE_LABEL = "Start Date";
	static final String END_DATE_LABEL = "End Date";

	public ProjectDetailsWrapperDto fetchAllProjects() {
		List<ProjectDto> projectDetails = projectRepository.findAll().stream()
				.map(projectEntity -> projectDetailsMapperService.entityToDto(projectEntity))
				.collect(Collectors.toList());
		return ProjectDetailsWrapperDto.builder().projectDetails(projectDetails).build();

	}

	private boolean hasExcelFormat(MultipartFile file) {
		return (TYPE.equals(file.getContentType()));
	}

	public ProjectDetailsWrapperDto persistProjectDetails(MultipartFile projectDetailsFile) throws IOException {
		if (hasExcelFormat(projectDetailsFile)) {
			Workbook workbook = new XSSFWorkbook(projectDetailsFile.getInputStream());
			ProjectDto projectDto = constructProjectObject(workbook);
			List<MilestoneDto> projectMilestones = new ArrayList<>();
			Iterator<Sheet> sheetIterator = workbook.iterator();
			int sheetIndex = 0;
			int milestonePriority = 1;
			while (sheetIterator.hasNext()) {
				Sheet milestoneSheet = sheetIterator.next();
				if (sheetIndex == 0) {
					sheetIndex++;
					continue;
				}
				MilestoneDto milestoneObj = MilestoneDto.builder().name(milestoneSheet.getSheetName()).priority(milestonePriority).build();
				List<TaskDto> milestoneTasks = new ArrayList<>();
				Iterator<Row> milestoneTasksRows = milestoneSheet.iterator();
				int rowNumber = 0;
				while (milestoneTasksRows.hasNext()) {
					Row currentRow = milestoneTasksRows.next();
					if (rowNumber == 0) {
						rowNumber++;
						continue;
					}
					if (0 != currentRow.getCell(0).getNumericCellValue()) {
						TaskDto taskObj = new TaskDto();
						Iterator<Cell> cellsInRow = currentRow.iterator();
						int cellIdx = 0;
						while (cellsInRow.hasNext()) {
							if (cellIdx > 4) {
								break;
							}
							Cell currentCell = cellsInRow.next();
							switch (cellIdx) {
							case 1:
								taskObj.setName(currentCell.getStringCellValue());
								break;
							case 2:
								taskObj.setPriority((int) currentCell.getNumericCellValue());
								break;
							case 3:
								taskObj.setEstimate((int) currentCell.getNumericCellValue());
								break;
							case 4:
								taskObj.setDependentTask((0 != currentCell.getNumericCellValue())
										? (int) currentCell.getNumericCellValue()
										: null);
								break;
							default:
								break;
							}
							cellIdx++;
						}
						milestoneTasks.add(taskObj);
					}
					milestoneObj.setTasks(milestoneTasks);
				}
				projectMilestones.add(milestoneObj);
				milestonePriority++;
			}
			projectDto.setMilestones(projectMilestones);
			saveProjectDetails(projectDto);
		}
		return fetchAllProjects();
	}

	private ProjectDto constructProjectObject(Workbook workbook) {
		Sheet projectOverviewSheet = workbook.getSheet(PROJECT_OVERVIEW_SHEET);
		Iterator<Row> projectOverviewRows = projectOverviewSheet.iterator();
		int rowNumber = 0;
		ProjectDto projectObj = new ProjectDto();
		projectObj.setRoadmapGenerated(false);
		while (projectOverviewRows.hasNext()) {
			if (rowNumber == 0) {
				rowNumber++;
				continue;
			}
			Row currentRow = projectOverviewRows.next();
			if (currentRow.getRowNum() > 4) {
				break;
			}
			Cell labelCell = currentRow.getCell(0);
			if (null != labelCell.getStringCellValue()) {
				switch (labelCell.getStringCellValue()) {
				case PROJECT_NAME_LABEL:
					projectObj.setName(currentRow.getCell(1).getStringCellValue());
					break;
				case START_DATE_LABEL:
					projectObj.setStartDate(currentRow.getCell(1).getStringCellValue());
					break;
				case END_DATE_LABEL:
					projectObj.setEndDate((null != currentRow.getCell(1).getStringCellValue()
							&& "".equals(currentRow.getCell(1).getStringCellValue()))
									? currentRow.getCell(1).getStringCellValue()
									: null);
					break;
				default:
					break;
				}
			} else {
				break;
			}
		}
		return projectObj;
	}

	public void saveProjectDetails(ProjectDto projectDto) {
		ProjectEntity projectEntity = projectRepository.save(projectDetailsMapperService.dtoToEntity(projectDto));
		List<MilestoneEntity> projectMilestones = projectDto.getMilestones().stream()
				.map(mileStonePayload -> projectDetailsMapperService.convertMileStoneDtoToEntity(mileStonePayload))
				.collect(Collectors.toList());
		projectMilestones.forEach(milestone -> {
			milestone.setProject(projectEntity);
		});
		milestoneRepository.saveAll(projectMilestones);
		projectDto.getMilestones().forEach(milestonePayload -> {
			MilestoneEntity milestoneEntity = milestoneRepository
					.findByNameAndProjectId(milestonePayload.getName(), projectEntity.getId()).orElse(null);
			if (null != milestoneEntity) {
				milestonePayload.getTasks().sort(Comparator.comparing(TaskDto::getPriority));
				milestonePayload.getTasks().forEach(taskPayload -> {
					TaskEntity task = projectDetailsMapperService.convertTaskDtoToEntity(taskPayload);
					task.setMilestone(milestoneEntity);
					if (null != taskPayload.getDependentTask()) {
						TaskEntity dependentTask = taskRepository
								.findByPriorityAndMilestoneId(taskPayload.getDependentTask(), milestoneEntity.getId())
								.orElse(null);
						task.setDependentTaskId(null != dependentTask ? dependentTask.getId() : null);
					}
					taskRepository.save(task);
				});
			}
		});
	}

}
