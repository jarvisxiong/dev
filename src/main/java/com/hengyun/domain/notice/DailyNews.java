package com.hengyun.domain.notice;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* @author bob E-mail:panbaoan@thealth.cn
* @version 创建时间：2016年3月30日 下午2:18:49
* 每日推送
*/
public class DailyNews implements Serializable{

	private int id;																		//咨讯id
	private String title;																//标题
	private String imageUrl;													//图片url
	private String content;														//内容快照
	private String url;																//网页源地址
	private List<Integer> collectorId;									//收藏者列表
	private Date publishTime;

	
	
	public List<Integer> getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(List<Integer> collectorId) {
		this.collectorId = collectorId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


}
