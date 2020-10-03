package com.project.roadmapgenerator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.roadmapgenerator.entities.ProjectEntity;
import com.project.roadmapgenerator.payloads.ProjectDto;
import com.project.roadmapgenerator.repositories.ProjectRepository;

@Service
public class ProjectRoadmapDetailsService {
	
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private ProjectDetailsMapperService projectDetailsMapperService;
	
	public ProjectDto fetchRoadmapForProject(String projectName) {
		ProjectEntity projectEntity = projectRepository.findByName(projectName).stream().findFirst().orElse(null);
		return projectDetailsMapperService.entityToDto(projectEntity);
	}

}
