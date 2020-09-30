package com.project.roadmapgenerator.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
public class TaskEntity implements Serializable {

	private static final long serialVersionUID = 920700615814687644L;

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "FK_MILESTONE_ID")
	@JsonBackReference
	private MilestoneEntity milestone;

	@Column(name = "ESTIMATE", nullable = false)
	private int estimate;

	@Column(name = "PRIORITY", nullable = false)
	private int priority;

	@Column(name = "DEPENDENCY")
	private Long dependentTaskId;

	@Column(name = "START_DATE")
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "END_DATE")
	@Temporal(TemporalType.DATE)
	private Date endDate;

}
