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
public class ProjectDetailsWrapperDto {
	private List<ProjectDto> projectDetails;

}
