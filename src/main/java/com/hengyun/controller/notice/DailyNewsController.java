package com.hengyun.controller.notice;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hengyun.domain.notice.DailyNews;
import com.hengyun.domain.notice.DailyNewsResponse;
import com.hengyun.service.notice.DailyNewsService;

/*
 * 
 *  每日推送控制器
 *  
 * */

@Controller
@RequestMapping("dnews")
public class DailyNewsController {
	
	@Resource
	private DailyNewsService dailyNewsService;
	
	
	@RequestMapping("/add")
	@ResponseBody
	public String addNotice(@RequestParam String data,HttpServletRequest request){
		
		return null;
	}
	
	/*
	 * 
	 *  加载资讯
	 * 
	 * */
	@RequestMapping("/load")
	@ResponseBody
	public String load(@RequestParam String data,HttpServletRequest request){
		JSONObject jsonObject =JSON.parseObject(data);
		int id = jsonObject.getIntValue("id");
		DailyNews dn = dailyNewsService.query(id);
		List<DailyNews> daily = new ArrayList<DailyNews>();
		daily.add(dn);
		DailyNewsResponse response = new DailyNewsResponse();
		response.setCode("206");
		response.setMessage("加载成功");
		response.setDaily(daily);
		return JSON.toJSONString(response);
		
	}
	

}