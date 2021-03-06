package com.hengyun.service.impl.forum;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.hengyun.dao.forum.ReplySubjectDao;
import com.hengyun.dao.information.InformationDao;
import com.hengyun.domain.forum.ReplySubject;
import com.hengyun.service.forum.ReplySubjectService;
import com.hengyun.service.forum.SubjectService;
import com.hengyun.service.impl.BaseServiceImpl;
import com.hengyun.service.logininfo.LoginInfoService;

/*
 *  　个人信息管理
 * */

public class ReplySubjectServiceImpl extends BaseServiceImpl<ReplySubject,Integer> implements ReplySubjectService{

	@Resource
	private ReplySubjectDao replySubjectDao;
	
	@Resource
	private SubjectService subjectService;
	
	@Resource 
	private InformationDao informationDao;
	@Resource
	
	private LoginInfoService loginInfoService;
	
	
	



	//更新资料
	public int update(ReplySubject forumPost,String tocken) {
		// TODO Auto-generated method stub
		int userId = loginInfoService.isOnline(tocken);
		if(userId>0){
			Query query = Query.query(Criteria.where("userId").is(userId));
			Update update = Update.update("title","title").set("title", "hello");
			replySubjectDao.updateFirst(query, update);
			return 0;
		} 
			return -1;
	
	}

	//发帖
	public int  post(ReplySubject forumPost,int userId) {
		// TODO Auto-generated method stub
			forumPost.setReplyTime(String.valueOf(new Date().getTime()));
			
			int postId = replySubjectDao.post(forumPost);
		
			return postId;
	
	}


	public List<ReplySubject> showList( int userId,int subjectId,int replyId ) {
		// TODO Auto-generated method stub
		List<ReplySubject> forumPost=null;
		
			
			Query query = new Query();
		//	query.addCriteria(Criteria.where("userId").is(userId).andOperator(Criteria.where("subjectId").is(subjectId).
			///		andOperator(Criteria.where("replyId").gt(replyId))));
		//	Criteria criteria =Criteria.where("userId").is(userId).andOperator(Criteria.where("replyId").gt(replyId).andOperator(Criteria.where("subjectId").is(subjectId)));
			Criteria criteria =Criteria.where("subjectId").is(subjectId).andOperator(Criteria.where("replyId").gt(replyId));
			  query.addCriteria(criteria).with(new Sort(Direction.ASC, "replyId"));
		//	query.query(Criteria.where("subjectId").is(subjectId).andOperator(Criteria.where("replyId").gt(replyId)));
		//	forumPost = replySubjectDao.getPage(query, 0, 20);
			//  Query query2 = Query.query(Criteria.where("replyId").exists(true));
			  forumPost = replySubjectDao.queryList(query);
			return forumPost;
	
	}

	
	public List<ReplySubject> show(String author ) {
		// TODO Auto-generated method stub
	
			Query query = Query.query(Criteria.where("author").is(author));
			List<ReplySubject> forumPost = replySubjectDao.queryList(query);
			return forumPost;
	
	}

	public List<ReplySubject> showAll() {
		// TODO Auto-generated method stub
		Query query = new Query();
		query.addCriteria(Criteria.where("id").exists(true));
		return replySubjectDao.queryList(query);
		
	}

	//删帖
	@Override
	public void delete(int subjectId) {
		// TODO Auto-generated method stub
		Query query = Query.query(Criteria.where("subjectId").is(subjectId));
		ReplySubject subject = replySubjectDao.queryOne(query);
		replySubjectDao.delete(subject);
	}

	@Override
	public List<ReplySubject> show() {
		// TODO Auto-generated method stub
		return null;
	}

}
