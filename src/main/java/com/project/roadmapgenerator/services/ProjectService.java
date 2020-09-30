package com.project.roadmapgenerator.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.roadmapgenerator.entities.ProjectEntity;
import com.project.roadmapgenerator.repositories.ProjectRepository;

@Service
public class ProjectService {
	
	@Autowired
	private ProjectRepository projectRepository;

	public List<ProjectEntity> getProjectsByName(String projectName) {
		return projectRepository.findByName(projectName);
	}
	
	public List<ProjectEntity> getAllProjects() {
		return (List<ProjectEntity>) projectRepository.findAll();
	}
	

}
