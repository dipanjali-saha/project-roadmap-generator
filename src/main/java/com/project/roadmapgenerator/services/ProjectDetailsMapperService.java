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
import com.project.roadmapgenerator.repositories.TaskAssignmentRepository;
import com.project.roadmapgenerator.utils.DateUtil;

@Service
public class ProjectDetailsMapperService {

	@Autowired
	private TaskAssignmentRepository taskAssignmentRepository;

	public ProjectDto entityToDto(ProjectEntity projectEntity) {
		ProjectDto project = null;
		if (null != projectEntity) {
			project = ProjectDto.builder().name(projectEntity.getName())
					.startDate(DateUtil.toDefaultString(projectEntity.getStartDate()))
					.endDate(DateUtil.toDefaultString(projectEntity.getEndDate())).build();
			List<MilestoneDto> milestones = CollectionUtils.isNotEmpty(projectEntity.getMilestones()) ? projectEntity
					.getMilestones().stream().map(this::convertMilestoneEntityToDto).collect(Collectors.toList())
					: Collections.emptyList();
			project.setMilestones(milestones);
		}
		return project;
	}

	public MilestoneDto convertMilestoneEntityToDto(MilestoneEntity milestoneEntity) {
		MilestoneDto milestonePayload = MilestoneDto.builder().name(milestoneEntity.getName())
				.startDate(DateUtil.toDefaultString(milestoneEntity.getStartDate()))
				.endDate(DateUtil.toDefaultString(milestoneEntity.getEndDate())).build();
		List<TaskDto> tasks = CollectionUtils.isNotEmpty(milestoneEntity.getTasks())
				? milestoneEntity.getTasks().stream().map(this::convertTaskEntityToDto).collect(Collectors.toList())
				: Collections.emptyList();
		milestonePayload.setTasks(tasks);
		return milestonePayload;
	}

	public TaskDto convertTaskEntityToDto(TaskEntity taskEntity) {
		TaskDto task = TaskDto.builder().name(taskEntity.getName()).priority(taskEntity.getPriority())
				.estimate(taskEntity.getEstimate()).startDate(DateUtil.toDefaultString(taskEntity.getStartDate()))
				.endDate(DateUtil.toDefaultString(taskEntity.getEndDate())).build();
		TaskAssignmentEntity taskAssignment = taskAssignmentRepository.findByTaskId(taskEntity.getId()).orElse(null);
		if (null != taskAssignment) {
			task.setAssignedTo(taskAssignment.getEmployee().getName());
		}
		return task;
	}

}
