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
	
	private String name;
	private int priority;
	private int estimate;
	private String assignedTo;
	private String startDate;
	private String endDate;

}
