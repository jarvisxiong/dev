package com.hengyun.service.impl.hospital;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.hengyun.dao.hospital.DocterDao;
import com.hengyun.domain.hospital.Docter;
import com.hengyun.service.hospital.DocterService;
import com.hengyun.service.impl.BaseServiceImpl;

/*
 *  　医生信息管理
 * */

public class DocterServiceImpl extends BaseServiceImpl<Docter,Integer> implements DocterService{

	@Resource 
	private DocterDao docterDao;

	public List<Docter> queryAll() {
		// TODO Auto-generated method stub
		Query query = Query.query(Criteria.where("workNum").exists(true));
		return docterDao.queryList(query);
		
	}
	
	

}
