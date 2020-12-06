package com.project.roadmapgenerator.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.roadmapgenerator.entities.MilestoneEntity;
import com.project.roadmapgenerator.entities.ProjectEntity;
import com.project.roadmapgenerator.entities.TaskAssignmentEntity;
import com.project.roadmapgenerator.entities.TaskEntity;
import com.project.roadmapgenerator.payloads.MilestoneDto;
import com.project.roadmapgenerator.payloads.ProjectDto;
import com.project.roadmapgenerator.payloads.TaskDto;
import com.project.roadmapgenerator.repositories.EmployeeRepository;
import com.project.roadmapgenerator.repositories.TaskAssignmentRepository;
import com.project.roadmapgenerator.utils.DateUtil;

@Service
public class ProjectDetailsMapperService {

	@Autowired
	private TaskAssignmentRepository taskAssignmentRepository;
	@Autowired
	private EmployeeRepository employeeRepository;

	public ProjectDto entityToDto(ProjectEntity projectEntity) {
		ProjectDto project = null;
		if (null != projectEntity && null != projectEntity.getId()) {
			project = ProjectDto.builder().projectId(projectEntity.getId()).name(projectEntity.getName())
					.startDate(DateUtil.toDefaultString(projectEntity.getStartDate()))
					.endDate(DateUtil.toDefaultString(projectEntity.getEndDate()))
					.roadmapGenerated(projectEntity.isRoadmapGenerated()).
					employeeAssigned(CollectionUtils.isNotEmpty(employeeRepository.findByProjectId(projectEntity.getId()))).build();
			List<MilestoneDto> milestones = CollectionUtils.isNotEmpty(projectEntity.getMilestones()) ? projectEntity
					.getMilestones().stream().map(this::convertMilestoneEntityToDto).collect(Collectors.toList())
					: Collections.emptyList();
			project.setMilestones(milestones);
		}
		return project;
	}

	private MilestoneDto convertMilestoneEntityToDto(MilestoneEntity milestoneEntity) {
		MilestoneDto milestonePayload = MilestoneDto.builder().milestoneId(milestoneEntity.getId())
				.name(milestoneEntity.getName()).priority(milestoneEntity.getPriority())
				.startDate(DateUtil.toDefaultString(milestoneEntity.getStartDate()))
				.endDate(DateUtil.toDefaultString(milestoneEntity.getEndDate())).build();
		List<TaskDto> tasks = CollectionUtils.isNotEmpty(milestoneEntity.getTasks())
				? milestoneEntity.getTasks().stream().map(this::convertTaskEntityToDto).collect(Collectors.toList())
				: Collections.emptyList();
		milestonePayload.setTasks(tasks);
		return milestonePayload;
	}

	private TaskDto convertTaskEntityToDto(TaskEntity taskEntity) {
		TaskDto task = TaskDto.builder().taskId(taskEntity.getId()).name(taskEntity.getName())
				.priority(taskEntity.getPriority()).estimate(taskEntity.getEstimate())
				.startDate(DateUtil.toDefaultString(taskEntity.getStartDate()))
				.endDate(DateUtil.toDefaultString(taskEntity.getEndDate())).build();
		TaskAssignmentEntity taskAssignment = taskAssignmentRepository.findByTaskId(taskEntity.getId()).orElse(null);
		if (null != taskAssignment) {
			task.setAssignedTo(taskAssignment.getEmployee().getName());
		}
		return task;
	}

	public ProjectEntity dtoToEntity(ProjectDto projectPayload) {
		ProjectEntity projectEntity = null;
		if (null != projectPayload) {
			projectEntity = ProjectEntity.builder().id(projectPayload.getProjectId()).name(projectPayload.getName())
					.startDate(DateUtil.toDefaultDate(projectPayload.getStartDate()))
					.endDate(DateUtil.toDefaultDate(projectPayload.getEndDate()))
					.roadmapGenerated(projectPayload.isRoadmapGenerated()).build();
		}
		return projectEntity;
	}

	public MilestoneEntity convertMileStoneDtoToEntity(MilestoneDto milestonePayload) {
		MilestoneEntity milestoneEntity = null;
		if (null != milestonePayload) {
			milestoneEntity = MilestoneEntity.builder().id(milestonePayload.getMilestoneId())
					.name(milestonePayload.getName()).startDate(DateUtil.toDefaultDate(milestonePayload.getStartDate()))
					.endDate(DateUtil.toDefaultDate(milestonePayload.getEndDate()))
					.priority(milestonePayload.getPriority()).tasks(milestonePayload.getTasks().stream()
							.map(this::convertTaskDtoToEntity).collect(Collectors.toList()))
					.build();
		}
		return milestoneEntity;
	}

	public TaskEntity convertTaskDtoToEntity(TaskDto taskPayload) {
		TaskEntity taskEntity = null;
		if (null != taskPayload) {
			taskEntity = TaskEntity.builder().id(taskPayload.getTaskId()).name(taskPayload.getName())
					.estimate(taskPayload.getEstimate()).priority(taskPayload.getPriority()).build();
		}
		return taskEntity;
	}

}
