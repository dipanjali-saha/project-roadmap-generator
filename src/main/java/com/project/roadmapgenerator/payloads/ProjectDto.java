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
public class ProjectDto {
	
	private String name;
	private String startDate;
	private String endDate;
	private List<MilestoneDto> milestones;

}
