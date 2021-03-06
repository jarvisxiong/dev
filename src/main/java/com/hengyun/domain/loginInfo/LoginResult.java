package com.hengyun.domain.loginInfo;

import java.io.Serializable;

import com.hengyun.domain.common.BaseResponseCode;
import com.hengyun.domain.information.DoctorInfo;
import com.hengyun.domain.information.Information;

/*
 *  注册返回码
 * */
public class LoginResult extends BaseResponseCode implements Serializable{

	private int userCode;					//用户代号	2,3

	private int userId;							//用户userId;
	
	private String username;				//用户名
	
	private Information info;						//病人基本资料
	private DoctorInfo doctorInfo;			//医生账号信息
	
	private boolean questionaire;			//是否评估过
	
	private String tocken;							//tocken
	
	
	public String getTocken() {
		return tocken;
	}

	public void setTocken(String tocken) {
		this.tocken = tocken;
	}

	public boolean isQuestionaire() {
		return questionaire;
	}

	public void setQuestionaire(boolean questionaire) {
		this.questionaire = questionaire;
	}

	public DoctorInfo getDoctorInfo() {
		return doctorInfo;
	}

	public void setDoctorInfo(DoctorInfo doctorInfo) {
		this.doctorInfo = doctorInfo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserCode() {
		return userCode;
	}

	public void setUserCode(int userCode) {
		this.userCode = userCode;
	}

	public Information getInfo() {
		return info;
	}

	public void setInfo(Information info) {
		this.info = info;
	}

	
	
}
