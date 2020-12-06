package com.project.roadmapgenerator.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

import com.project.roadmapgenerator.entities.EmployeeEntity;
import com.project.roadmapgenerator.entities.EmployeeLeaveEntity;
import com.project.roadmapgenerator.entities.ProjectEntity;
import com.project.roadmapgenerator.entities.TaskEntity;
import com.project.roadmapgenerator.payloads.EmployeeDto;
import com.project.roadmapgenerator.payloads.EmployeeListWrapperDto;
import com.project.roadmapgenerator.repositories.EmployeeLeaveRepository;
import com.project.roadmapgenerator.repositories.EmployeeRepository;
import com.project.roadmapgenerator.repositories.ProjectRepository;
import com.project.roadmapgenerator.repositories.TaskAssignmentRepository;

@Service
public class EmployeeService {

	public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	static String SHEET = "Leave Planner";

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private EmployeeLeaveRepository employeeLeaveRepository;
	@Autowired
	private TaskAssignmentRepository taskAssignmentRepository;

	public EmployeeListWrapperDto fetchAllEmployees() {
		List<EmployeeDto> employees = employeeRepository.findAll().stream()
				.map(employee -> EmployeeDto.builder().employeeId(employee.getId()).name(employee.getName())
						.assignedProjectId(employee.getProjectId())
						.assignedProjectName(projectRepository.findById(employee.getProjectId()).get().getName())
						.build())
				.collect(Collectors.toList());
		return EmployeeListWrapperDto.builder().employeeList(employees).build();
	}

	public EmployeeListWrapperDto persistEmployeeDetails(EmployeeDto employeeDto) {
		if (null != employeeDto) {
			if (null != employeeDto.getEmployeeId()) {
				updateExistingEmployee(employeeDto);
			} else {
				saveNewEmployeeDetails(employeeDto);
			}
		}
		return fetchAllEmployees();
	}

	public void persistEmployeeLeaves(Long employeeId, MultipartFile leaveApplicationFile) throws IOException {
		if (hasExcelFormat(leaveApplicationFile)) {
			EmployeeEntity employee = employeeRepository.findById(employeeId).orElse(null);
			if (null != employee) {
				employeeLeaveRepository.deleteAll(employeeLeaveRepository.findByEmployeeId(employeeId));
				List<EmployeeLeaveEntity> employeeLeaves = new ArrayList<>();
				Workbook workbook = new XSSFWorkbook(leaveApplicationFile.getInputStream());
				Sheet sheet = workbook.getSheet(SHEET);
				Iterator<Row> rows = sheet.iterator();
				int rowNumber = 0;
				while (rows.hasNext()) {
					Row currentRow = rows.next();
					if (rowNumber == 0) {
						rowNumber++;
						continue;
					}
					EmployeeLeaveEntity employeeLeaveEntity = new EmployeeLeaveEntity();
					employeeLeaveEntity.setEmployee(employee);
					Iterator<Cell> cellsInRow = currentRow.iterator();
					int cellIdx = 0;
					while (cellsInRow.hasNext()) {
						Cell currentCell = cellsInRow.next();
						if (null != currentCell.getDateCellValue()) {
							switch (cellIdx) {
							case 0:
								employeeLeaveEntity.setStartDate((Date) currentCell.getDateCellValue());
								break;
							case 1:
								employeeLeaveEntity.setEndDate((Date) currentCell.getDateCellValue());
								break;
							default:
								break;
							}
							employeeLeaves.add(employeeLeaveEntity);
						}
						cellIdx++;
					}

				}
				employeeLeaveRepository.saveAll(employeeLeaves);
				ProjectEntity assignedProject = projectRepository.findById(employee.getProjectId()).orElse(null);
				if (null != assignedProject) {
					assignedProject.setRoadmapGenerated(false);
					projectRepository.save(assignedProject);
				}
				
			}
		}
	}

	private boolean hasExcelFormat(MultipartFile file) {
		return (TYPE.equals(file.getContentType()));
	}

	private void saveNewEmployeeDetails(EmployeeDto employeeDto) {
		EmployeeEntity employeeEntity;
		employeeEntity = EmployeeEntity.builder().name(employeeDto.getName())
				.projectId(employeeDto.getAssignedProjectId()).build();
		employeeRepository.save(employeeEntity);
	}

	private void updateExistingEmployee(EmployeeDto employeeDto) {
		EmployeeEntity employeeEntity;
		employeeEntity = employeeRepository.findById(employeeDto.getEmployeeId()).orElse(null);
		if (null != employeeEntity) {
			Long existingProjectId = employeeEntity.getProjectId();
			employeeEntity.setName(employeeDto.getName());
			if (projectRepository.findById(employeeDto.getAssignedProjectId()).isPresent()) {
				employeeEntity.setProjectId(employeeDto.getAssignedProjectId());
			}
			employeeRepository.save(employeeEntity);
			List<ProjectEntity> roadmapsToBeGeneratedFor = new ArrayList<>();
			ProjectEntity previousProject = projectRepository.findById(existingProjectId).orElse(null);
			if (null != previousProject) {
				previousProject.setRoadmapGenerated(false);
				roadmapsToBeGeneratedFor.add(previousProject);
				List<TaskEntity> projectTasks = previousProject.getMilestones().stream()
						.flatMap(milestone -> milestone.getTasks().stream()).collect(Collectors.toList());
				taskAssignmentRepository.deleteAll(taskAssignmentRepository
						.findByEmployeeIdAndTaskIdIn(employeeEntity.getId(), projectTasks.stream().map(TaskEntity::getId).collect(Collectors.toList())));
			}
			ProjectEntity assignedProject = projectRepository.findById(employeeDto.getAssignedProjectId()).orElse(null);
			if (null != assignedProject) {
				assignedProject.setRoadmapGenerated(false);
				roadmapsToBeGeneratedFor.add(assignedProject);
			}
			projectRepository.saveAll(roadmapsToBeGeneratedFor);
		}
	}

}
