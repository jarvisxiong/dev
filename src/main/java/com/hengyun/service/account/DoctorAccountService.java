package com.hengyun.service.account;

import java.util.List;

import com.hengyun.domain.account.DoctorInfo;
import com.hengyun.domain.loginInfo.LoginResult;
import com.hengyun.service.BaseService;

/**
* @author bob E-mail:panbaoan@thealth.cn
* @version 创建时间：2016年4月5日 下午2:52:51
* 医生信息业务访问层
*/
public interface DoctorAccountService extends BaseService<DoctorInfo,Integer> {

	public DoctorInfo getDoctorInfoById(int id);

	//用户是否存在
	public int existDoctor(String sign,String type);
	
	public List<DoctorInfo> getUserAccountALL();
	
	//更改密码
	public void updatePassword(String password,int userId) ;

	//注册账号
	public int registerDoctor(String username,String type,String password) ;
	
	//改变用户找好绑定信息
	public int change(String type,String username,int userId);
	

	//验证用户是否有效,返回用户userId
	public LoginResult validateUserBySign(String sign, String type,String password) ;
	
	
	
}
