package com.project.roadmapgenerator.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

	private Long taskId;
	private String name;
	private Integer priority;
	private Integer estimate;
	private Integer dependentTask;
	private String assignedTo;
	private String startDate;
	private String endDate;

}
