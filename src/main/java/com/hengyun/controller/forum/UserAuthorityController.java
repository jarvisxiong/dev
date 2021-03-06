package com.hengyun.controller.forum;

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
import com.hengyun.domain.forum.ForumAuthority;
import com.hengyun.domain.forum.ForumResponseCode;
import com.hengyun.service.forum.ForumAuthorityService;
import com.hengyun.service.forum.ForumPostService;
import com.hengyun.service.forum.PostCommentService;
import com.hengyun.service.forum.SecondCommentService;
import com.hengyun.service.logininfo.LoginInfoService;

/*
 *  用户权限验证
 * */
@Controller  
@RequestMapping("authority")  
public class UserAuthorityController {   
    @Resource
    private LoginInfoService loginInfoService;
    
	@Resource
	private ForumPostService forumPostService;
	
	@Resource 
	private PostCommentService postCommentService;
	
	@Resource 
	private SecondCommentService secondCommentService;
	
	
	@Resource
	private ForumAuthorityService forumAuthorityService;
	
	@RequestMapping("/add")
	@ResponseBody
	public String addAuthority(@RequestParam String data,HttpServletRequest request){
		ResponseCode response = new ResponseCode();
		JSONObject jsonObject =JSON.parseObject(data);
		ForumAuthority authority = JSON.toJavaObject(jsonObject, ForumAuthority.class);
		forumAuthorityService.save(authority);
		response.setCode("209");
		response.setMessage("post success");
	
		return JSON.toJSONString(response);
	}
	
	@RequestMapping("/set")
	@ResponseBody
	public String setPost(@RequestParam String data,HttpServletRequest request){
		
		return null;
	}
	
	//展示权限列表
	@RequestMapping("/show")
	@ResponseBody
	public String showAuthority(HttpServletRequest request){
		List<ForumAuthority> authority = forumAuthorityService.show();
		   return JSON.toJSONString(authority);
		
	}
	
	
	//是否允许发帖
	@RequestMapping(value="/allow",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String allowPost(HttpServletRequest request){
		String tocken = request.getParameter("tocken");
		int userId = loginInfoService.isOnline(tocken);
		ForumResponseCode response = new ForumResponseCode();
		if(userId>0){
		response.setResponseCode(0);
		response.setDescription("允许发帖");
		} else {
			response.setResponseCode(-1);
			response.setDescription("不允许发帖");
		}
		 return JSON.toJSONString(response);
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public String deleteComment(@RequestParam String data,HttpServletRequest request){
		ResponseCode response = new ResponseCode();
	
		String tocken = request.getParameter("tocken");
		String idStr = request.getParameter("commentId");
		secondCommentService.deleteById(Integer.valueOf(idStr));
		response.setCode("210");
		response.setMessage("delete success");
		return null;
	}
	
	
}