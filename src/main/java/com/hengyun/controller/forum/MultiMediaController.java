package com.hengyun.controller.forum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.hengyun.controller.BaseController;
import com.hengyun.domain.common.ResponseCode;
import com.hengyun.domain.forum.UploadResponseCode;
import com.hengyun.service.forum.MultiMediaService;
import com.hengyun.util.network.NetworkUtil;
import com.mongodb.gridfs.GridFSDBFile;

/*
 * 
 * 	多媒体信息上传下载
 * 
 * */
@Controller
@RequestMapping("multiMedia")
public class MultiMediaController extends BaseController{

	@Resource
	private MultiMediaService multiMediaService;

	/*
	 * 
	 *  多媒体信息上传
	 * 
	 * */
    @RequestMapping(value="/upload",produces = "text/html;charset=UTF-8")  
    @ResponseBody
    public String upload(@RequestParam MultipartFile media,HttpServletRequest request) throws IOException  
    {  
	    
	    	UploadResponseCode response = new UploadResponseCode();
	    	String ip = NetworkUtil.getPhysicalHostIP();
    	    String originalfilename = media.getOriginalFilename();
    	  
    	    String filename = new Date().getTime()+originalfilename;
    	    
	    	
	      if(media.isEmpty()){
	    	  response.setCode("110");
	    	  response.setMessage("上传文件失败");
	    	  }else{
	    	//	if(filename.endsWith(".mp4")){
	    		 // String videoPath = "/home/bob/multimedia/download/";
	    		String videoPath = "/home/thealth/multimedia/download/";
	    		  FileUtils.writeByteArrayToFile(new File(videoPath,filename), media.getBytes());
	    		  response.setCode("0");
	    		  response.setResponseCode(0);
	    		  response.setDescription("上传成功");
	    		  String fileUrl = "http://"+ip+"/download/"+filename;
	    		  response.setMessage(fileUrl);
	    		  response.setFileUrl(fileUrl);
	    		  /*
	    		} else {
	    			multiMediaService.save(media.getInputStream(), filename);
	    			  response.setCode("0");
		    		  response.setResponseCode(0);
		    		  response.setDescription("上传成功");
		    		  String fileUrl = "http://"+ip+"/healthcloudserver/multiMedia/download?url=";
		    		  response.setMessage(fileUrl);
		    		  response.setFileUrl(fileUrl);
	    		}
	    		  */
	    	  }
    	
	      return JSON.toJSONString(response);
      
    }
          

    /*
     * 
     *  查询多媒体文件
     * 
     * */
    @RequestMapping("/download")
    @ResponseBody
    public String download(HttpServletRequest request ,Model model, HttpServletResponse response) throws IOException{
    	ResponseCode responseCode = new ResponseCode();
   
    	String filename = request.getParameter("url");
    
    	response.addHeader("Content-Disposition", "attachment;filename="+filename);  
    	GridFSDBFile gridFSDBFile = multiMediaService.retrieveFileOne(filename);
    	InputStream in = gridFSDBFile.getInputStream();
    	File file = new File(filename);
    	FileOutputStream fout = new FileOutputStream(file);
    	byte[] a = new byte[1024];
    	int d;
    	while( (d=in.read(a,0,a.length))!= -1){ 
    		fout.write(a,0,d);     
    		}
    	in.close();
    	fout.flush();
		fout.close();
		
    	FileInputStream fin = new FileInputStream(file);
    	OutputStream os = response.getOutputStream();  //创建输出流
    	byte[] b = new byte[1024];
		int z;
		while( (z=fin.read(b,0,b.length))!= -1){ 
		os.write(b,0,z);     
		}
		fin.close(); 
		os.flush();
		os.close();
    	
	
		responseCode.setCode("208");
		responseCode.setMessage("download success");

		return  JSON.toJSONString(responseCode);
    }
    
}
