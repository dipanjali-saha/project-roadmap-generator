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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.project.roadmapgenerator.entities.Project;
import com.project.roadmapgenerator.repositories.ProjectRepository;

@SpringBootTest
class ProjectServiceTest {
	
	@InjectMocks
	private ProjectService projectService;
	@Mock
	private ProjectRepository projectRepository;
	
	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(projectService, "projectRepository", projectRepository);
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	void should_fetch_project_by_project_names() {
		Mockito.when(projectRepository.findByName(Mockito.any())).thenReturn(getMockedProjectList());
		Assertions.assertThat(projectService.getProjectsByName("testProject")).isEmpty();
	}
	
	private List<Project> getMockedProjectList() {
		return new ArrayList<>();
	}


}
