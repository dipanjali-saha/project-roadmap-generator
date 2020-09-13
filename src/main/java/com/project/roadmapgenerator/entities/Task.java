package com.project.roadmapgenerator.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "TASK_TBL")
public class Task {

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "FK_MILESTONE_ID")
	private Milestone milestone;

	@Column(name = "ESTIMATE", nullable = false)
	private int estimate;

	@Column(name = "PRIORITY", nullable = false)
	private int priority;

	@Column(name = "DEPENDENCY")
	private Task dependentTask;

	@Column(name = "START_DATE")
	private Date startDate;

	@Column(name = "END_DATE")
	private Date endDate;

}
