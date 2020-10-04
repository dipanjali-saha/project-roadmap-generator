package com.project.roadmapgenerator.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "EMPLOYEE_TBL")
public class EmployeeEntity {
	
	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;
	
	@Column(name= "PROJECT_ID")
	private Long projectId;
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<TaskAssignmentEntity> taskAssignments;

}
