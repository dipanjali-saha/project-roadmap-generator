package com.project.roadmapgenerator.payloads;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneDto {
	
	private String name;
	private Integer priority;
	private String startDate;
	private String endDate;
	private List<TaskDto> tasks;

}
