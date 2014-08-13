package org.ccsds.moims.mo.mal.planning.datamodel;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.sql.rowset.serial.SerialBlob;

@Entity
public class TaskArgumentValue {

	private Long id;
	private Task task;
	private String name;
	private byte[] value;
	private List<SerialBlob> attachments;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne
	@JoinColumn(name = "task_id")
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public List<SerialBlob> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<SerialBlob> attachments) {
		this.attachments = attachments;
	}

}
