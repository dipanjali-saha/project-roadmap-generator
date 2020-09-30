package com.project.roadmapgenerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.roadmapgenerator.entities.MilestoneEntity;

@Repository
public interface MilestoneRepository extends JpaRepository<MilestoneEntity, Long> {

}
