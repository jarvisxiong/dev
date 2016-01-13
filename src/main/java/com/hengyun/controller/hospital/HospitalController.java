package com.hengyun.controller.hospital;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hengyun.domain.common.ResponseCode;
import com.hengyun.domain.hospital.Hospital;
import com.hengyun.domain.loginInfo.UserAccount;
import com.hengyun.service.hospital.HospitalService;

/*
 *  医院管理
 * 
 * */
@Controller
@RequestMapping("hospital")
public class HospitalController {
	
	@Resource 
	private HospitalService hospitalService;
	
	@RequestMapping("/add")
	@ResponseBody
	public String addHospital(@RequestParam String data,HttpServletRequest request){
		JSONObject jsonObject = JSONObject.parseObject(data);
		Hospital hospital = JSON.toJavaObject(jsonObject, Hospital.class);
		hospitalService.save(hospital);
		
		ResponseCode response = new ResponseCode();
		response.setCode("206");
		response.setMessage("edit success");
		 return JSON.toJSONString(response);
	}
	
	@RequestMapping("/set")
	@ResponseBody
	public String setHospital(@RequestParam String data,HttpServletRequest request){
		
		return null;
	}
	
	@RequestMapping("/showAll")
	@ResponseBody
	public String queryHospital(HttpServletRequest request){
		List<Hospital> hospitalList ;
    	hospitalList = hospitalService.queryAll();

    	 String jsonString= JSON.toJSONString(hospitalList);  
        return jsonString;  
	}
	
	@RequestMapping("/edit")
	@ResponseBody
	public String editHospital(@RequestParam String data,HttpServletRequest request){
		return null;
	}
	
	
}
