package com.project.roadmapgenerator.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.roadmapgenerator.entities.EmployeeEntity;
import com.project.roadmapgenerator.entities.MilestoneEntity;
import com.project.roadmapgenerator.entities.ProjectEntity;
import com.project.roadmapgenerator.entities.TaskAssignmentEntity;
import com.project.roadmapgenerator.entities.TaskEntity;
import com.project.roadmapgenerator.repositories.EmployeeRepository;
import com.project.roadmapgenerator.repositories.MilestoneRepository;
import com.project.roadmapgenerator.repositories.ProjectRepository;
import com.project.roadmapgenerator.repositories.TaskAssignmentRepository;
import com.project.roadmapgenerator.repositories.TaskRepository;
import com.project.roadmapgenerator.utils.DateUtil;

@Service
public class RoadmapCalculatorService {

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private MilestoneRepository milestoneRepository;
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private TaskAssignmentRepository taskAssignmentRepository;

	@Transactional
	public void calculateProjectRoadmap(Long projectId) {
		ProjectEntity project = projectRepository.findById(projectId).orElse(null);
		Date projectEndDate = null;
		if (null != project) {
			List<TaskEntity> projectTasks = project.getMilestones().stream()
					.flatMap(milestone -> milestone.getTasks().stream()).collect(Collectors.toList());
			taskAssignmentRepository.deleteAll(taskAssignmentRepository
					.findByTaskIdIn(projectTasks.stream().map(TaskEntity::getId).collect(Collectors.toList())));
			projectEndDate = project.getStartDate();
			List<MilestoneEntity> milestones = project.getMilestones();
			if (CollectionUtils.isNotEmpty(milestones)) {
				milestones.sort(Comparator.comparingInt(MilestoneEntity::getPriority));
				for (MilestoneEntity milestone : milestones) {
					milestone = updateMilestoneTimelines(milestone, project.getId(), project.getStartDate());
					milestoneRepository.saveAndFlush(milestone);
					projectEndDate = milestone.getEndDate();
				}
			}
			project.setEndDate(projectEndDate);
			project.setRoadmapGenerated(true);
			projectRepository.saveAndFlush(project);
		}
	}

	private MilestoneEntity updateMilestoneTimelines(MilestoneEntity milestone, Long projectId, Date projectStartDate) {
		Date milestoneEndDate = null;
		milestone.setStartDate(null);
		milestone.setEndDate(null);
		List<TaskEntity> tasks = milestone.getTasks();
		if (CollectionUtils.isNotEmpty(tasks)) {
			tasks.sort(Comparator.comparingInt(TaskEntity::getPriority));
			for (TaskEntity task : tasks) {
				Date earliestPossibleDateToBeginTask = getEarliestPossibleDateToAssignTask(projectStartDate, task);
				Map<Date, EmployeeEntity> earliestPossibleTaskAssignment = getBestPossibleTaskAssignment(
						earliestPossibleDateToBeginTask, projectId, task.getEstimate());
				if (!earliestPossibleTaskAssignment.isEmpty()) {
					if (null == milestone.getStartDate()) {
						milestone.setStartDate(
								earliestPossibleTaskAssignment.keySet().stream().findFirst().orElse(null));
					}
					task.setStartDate(earliestPossibleTaskAssignment.keySet().stream().findFirst().orElse(null));
					task.setEndDate(DateUtil.addDaysWithoutWeekends(task.getStartDate(), task.getEstimate() - 1));
					taskRepository.saveAndFlush(task);
					assignTaskToEmployee(task,
							earliestPossibleTaskAssignment.values().stream().findFirst().orElse(null));
					milestoneEndDate = task.getEndDate();
				}
			}
		}
		milestone.setEndDate(milestoneEndDate);
		return milestone;
	}

	private void assignTaskToEmployee(TaskEntity task, EmployeeEntity employee) {
		TaskAssignmentEntity taskAssignment = TaskAssignmentEntity.builder().task(task).employee(employee)
				.startDate(task.getStartDate()).endDate(task.getEndDate()).build();
		taskAssignmentRepository.saveAndFlush(taskAssignment);
	}

	private Date getEarliestPossibleDateToAssignTask(Date mileStoneStartDate, TaskEntity task) {
		if (null != task.getDependentTaskId()) {
			TaskEntity dependentTask = taskRepository.findById(task.getDependentTaskId()).orElse(null);
			if (null != dependentTask) {
				return DateUtil.addDaysWithoutWeekends(dependentTask.getEndDate(), 1);
			}
		}
		return mileStoneStartDate;
	}

	private Map<Date, EmployeeEntity> getBestPossibleTaskAssignment(Date date, Long projectId, int taskEstimate) {
		Map<Date, EmployeeEntity> bestPossibleTaskAssignment = new HashMap<>();
		Date taskStartDate = date;
		List<EmployeeEntity> allProjectEmployees = employeeRepository.findByProjectId(projectId);
		while (bestPossibleTaskAssignment.isEmpty()) {
			Date evaluationDate = taskStartDate;
			List<EmployeeEntity> availableEmployees = allProjectEmployees.stream()
					.filter(emp -> isEmployeeNotOnLeave(emp, evaluationDate)
							&& isEmpNotAssignedToDifferentTask(evaluationDate, emp))
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(availableEmployees)) {
				Map<EmployeeEntity, Date> employeeTaskCompletionMap = new HashMap<>();
				for (EmployeeEntity emp : availableEmployees) {
					employeeTaskCompletionMap.put(emp, calculateTaskEndForEmployee(emp, taskEstimate, evaluationDate));
				}
				bestPossibleTaskAssignment.put(evaluationDate,
						Collections.min(employeeTaskCompletionMap.entrySet(), Map.Entry.comparingByValue()).getKey());
			} else {
				taskStartDate = DateUtil.addDaysWithoutWeekends(taskStartDate, 1);
			}
		}
		return bestPossibleTaskAssignment;
	}

	private boolean isEmployeeNotOnLeave(EmployeeEntity emp, Date dayInProgress) {
		return CollectionUtils
				.isEmpty(
						emp.getEmployeeLeaves().stream()
								.filter(leave -> leave.getStartDate().compareTo(dayInProgress) <= 0
										&& leave.getEndDate().compareTo(dayInProgress) >= 0)
								.collect(Collectors.toList()));
	}

	private boolean isEmpNotAssignedToDifferentTask(Date evaluationDate, EmployeeEntity emp) {
		return CollectionUtils.isEmpty(
				taskAssignmentRepository.findByEmployeeIdAndEndDateGreaterThanEqual(emp.getId(), evaluationDate));
	}

	private Date calculateTaskEndForEmployee(EmployeeEntity emp, int taskEstimate, Date startDate) {
		Date dayInProgress = startDate;
		int daysConsumed = 0;
		while (isEmployeeNotOnLeave(emp, dayInProgress) && daysConsumed <= taskEstimate) {
			if (isEmpNotAssignedToDifferentTask(dayInProgress, emp)) {
				daysConsumed += 1;
			}
			dayInProgress = DateUtil.addDaysWithoutWeekends(dayInProgress, 1);
		}
		return DateUtil.addDaysWithoutWeekends(dayInProgress, -1);
	}
}
