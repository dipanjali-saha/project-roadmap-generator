package com.project.roadmapgenerator.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
	public void calculateProjectRoadmap(String projectName) {
		List<ProjectEntity> projects = projectRepository.findByName(projectName);
		ProjectEntity project = projects.stream().findFirst().orElse(null);
		Date projectEndDate = null;
		if (null != project) {
			Date milestoneStartDate = project.getStartDate();
			projectEndDate = project.getStartDate();
			List<MilestoneEntity> milestones = project.getMilestones();
			if (CollectionUtils.isNotEmpty(milestones)) {
				milestones.sort(Comparator.comparingInt(MilestoneEntity::getPriority));
				for (MilestoneEntity milestone : milestones) {
					milestone.setStartDate(milestoneStartDate);
					milestone.setEndDate(getMilestoneEndDate(milestone, project.getId()));
					milestoneRepository.saveAndFlush(milestone);
					milestoneStartDate = DateUtil.addDaysWithoutWeekends(milestone.getEndDate(), 1);
					projectEndDate = milestone.getEndDate();
				}
			}
			project.setEndDate(projectEndDate);
			projectRepository.saveAndFlush(project);
		}
	}

	private Date getMilestoneEndDate(MilestoneEntity milestone, Long projectId) {
		Date milestoneEndDate = milestone.getStartDate();
		List<TaskEntity> tasks = milestone.getTasks();
		if (CollectionUtils.isNotEmpty(tasks)) {
			taskAssignmentRepository.deleteAll(taskAssignmentRepository
					.findByTaskIdIn(tasks.stream().map(TaskEntity::getId).collect(Collectors.toList())));
			tasks.sort(Comparator.comparingInt(TaskEntity::getPriority));
			for (TaskEntity task : tasks) {
				Date earliestPossibleDateToBeginTask = getEarliesPossibleDateToAssignTask(milestone.getStartDate(), task);
				Map<Date, EmployeeEntity> earliestPossibleTaskAssignment = getEarliestPossibleTaskAssignment(earliestPossibleDateToBeginTask, projectId);
				if (!earliestPossibleTaskAssignment.isEmpty()) {
					task.setStartDate(earliestPossibleTaskAssignment.keySet().stream().findFirst().get());
					task.setEndDate(DateUtil.addDaysWithoutWeekends(task.getStartDate(), task.getEstimate() - 1));
					taskRepository.saveAndFlush(task);
					assignTaskToEmployee(task, earliestPossibleTaskAssignment.values().stream().findFirst().get());
					milestoneEndDate = task.getEndDate();
				}
			}
		}
		return milestoneEndDate;
	}

	private void assignTaskToEmployee(TaskEntity task, EmployeeEntity employee) {
		TaskAssignmentEntity taskAssignment = TaskAssignmentEntity.builder().task(task).employee(employee).startDate(task.getStartDate()).endDate(task.getEndDate()).build();
		taskAssignmentRepository.saveAndFlush(taskAssignment);
	}

	private Date getEarliesPossibleDateToAssignTask(Date mileStoneStartDate, TaskEntity task) {
		if (null != task.getDependentTaskId()) {
			TaskEntity dependentTask = taskRepository.findById(task.getDependentTaskId()).orElse(null);
			if (null != dependentTask) {
				return DateUtil.addDaysWithoutWeekends(dependentTask.getEndDate(), 1);
			}
		}
		return mileStoneStartDate;
	}

	private Map<Date, EmployeeEntity> getEarliestPossibleTaskAssignment(Date startDate, Long projectId) {
		List<EmployeeEntity> allProjectEmployees = employeeRepository.findByProjectId(projectId);
		Date taskStartDate = startDate;
		while (getEmployeeForTaskAssignment(taskStartDate, allProjectEmployees).isEmpty()) {
			taskStartDate = DateUtil.addDaysWithoutWeekends(taskStartDate, 1);
		}
		return getEmployeeForTaskAssignment(taskStartDate, allProjectEmployees);
	}

	private Map<Date, EmployeeEntity> getEmployeeForTaskAssignment(Date date, List<EmployeeEntity> allEmployees) {
		for (EmployeeEntity emp : allEmployees) {
			if (CollectionUtils.isEmpty(taskAssignmentRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(emp.getId(), date, date))) {
				return Collections.singletonMap(date, emp);
			}
		}
		return Collections.emptyMap();
	}

}
