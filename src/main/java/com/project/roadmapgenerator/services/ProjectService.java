package com.project.roadmapgenerator.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.roadmapgenerator.entities.MilestoneEntity;
import com.project.roadmapgenerator.entities.ProjectEntity;
import com.project.roadmapgenerator.entities.TaskEntity;
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

	public void persistProjectDetails(ProjectDto projectDto) {
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
