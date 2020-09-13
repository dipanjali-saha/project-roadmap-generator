package com.project.roadmapgenerator.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import com.project.roadmapgenerator.entities.Project;

@Repository
public interface ProjectRepository extends JpaRepositoryImplementation<Project, Long> {
	List<Project> findByName(String name);
}
