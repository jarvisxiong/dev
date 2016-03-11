package com.hengyun.domain.doctor;

import java.io.Serializable;
import java.util.List;

import com.hengyun.domain.information.GeneralPerson;

/*
 * 医生信息表 
 * */


public class Doctor extends GeneralPerson implements Serializable{

	private List<Integer>			caseHistory;									//医生参与的病历	

	private List<Integer>				patients;										//医生的病人列表
	
	private List<Integer>				followPatient;										//随访列表


	public List<Integer> getFollowPatient() {
		return followPatient;
	}

	public void setFollowPatient(List<Integer> followPatient) {
		this.followPatient = followPatient;
	}

	public List<Integer> getCaseHistory() {
		return caseHistory;
	}

	public void setCaseHistory(List<Integer> caseHistory) {
		this.caseHistory = caseHistory;
	}

	public List<Integer> getPatients() {
		return patients;
	}

	public void setPatients(List<Integer> patients) {
		this.patients = patients;
	}
	

}
