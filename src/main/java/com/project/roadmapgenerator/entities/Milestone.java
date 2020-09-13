package com.project.roadmapgenerator.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "MILESTONE_TBL")
public class Milestone {

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "FK_PROJECT_ID")
	private Project project;
	
	@Column(name = "PRIORITY", nullable = false)
	private int priority;

	@Column(name = "START_DATE", nullable = false)
	private Date startDate;

	@Column(name = "END_DATE")
	private Date endDate;

	@OneToMany(mappedBy = "milestone", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Task> tasks;

}
