package com.project.roadmapgenerator.services;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.project.roadmapgenerator.entities.ProjectEntity;
import com.project.roadmapgenerator.repositories.ProjectRepository;

class ProjectServiceTest {
	
	@InjectMocks
	private ProjectService projectService;
	
	@Mock
	private ProjectRepository projectRepository;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	void should_fetch_project_by_project_names() {
		Mockito.when(projectRepository.findByName(Mockito.any())).thenReturn(getMockedProjectList());
		Assertions.assertThat(projectService.getProjectsByName("testProject")).isEmpty();
	}
	
	private List<ProjectEntity> getMockedProjectList() {
		return new ArrayList<>();
	}


}
