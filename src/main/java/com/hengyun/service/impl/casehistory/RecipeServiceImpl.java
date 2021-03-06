package com.hengyun.service.impl.casehistory;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.hengyun.dao.casehistory.RecipeDao;
import com.hengyun.dao.logininfo.IndexCollectionDao;
import com.hengyun.domain.casehistory.DoctorAdvice;
import com.hengyun.domain.casehistory.Recipe;
import com.hengyun.service.casehistory.RecipeService;
import com.hengyun.service.impl.BaseServiceImpl;

/**
* @author bob E-mail:panbaoan@thealth.cn
* @version 创建时间：2016年3月10日 下午2:20:41
* 处方业务类
*/
public class RecipeServiceImpl extends BaseServiceImpl<Recipe,Integer> implements RecipeService{

	@Resource 
	private RecipeDao recipeDao;
	@Resource
	private IndexCollectionDao indexCollectionDao;
	/*
	 *  添加处方
	 * */
	@Override
	public boolean addRecipe(Recipe recipe) {
		// TODO Auto-generated method stub
		int recipeId = indexCollectionDao.updateIndex("recipeId");
		recipe.setRecipeId(recipeId);
		recipe.setRecipeTime(new Date());
		recipeDao.save(recipe);
		return true;
	}

	/*
	 *  查询处方
	 * 
	 * */
	@Override
	public Recipe queryRecipe(int recipeId) {
		// TODO Auto-generated method stub
		Query query  = Query.query(Criteria.where("recipeId").is(recipeId));
		Recipe recipe = recipeDao.queryOne(query);
		if(recipe!=null){
			return recipe;
		} else {
			return null;
		}
	}

	/*
	 *  查询病人的处方列表
	 * */
	@Override
	public List<Recipe> getPatientRecipe(int patientId,int doctorId) {
		// TODO Auto-generated method stub
		Query query  = Query.query(Criteria.where("patientId").
				is(patientId).andOperator(Criteria.where("doctorId").is(doctorId))).with(new Sort(Direction.DESC, "recipeId"));
		List<Recipe> recipes = recipeDao.queryList(query);
		if(recipes != null){
			return recipes;
		} else {
			return null;
		}
	}

	/*
	 * 	删除一条处方
	 * */
	@Override
	public boolean deleteRecipe(int recipeId) {
		// TODO Auto-generated method stub
		//Query query  = Query.query(Criteria.where("recipeId").is(recipeId));
		recipeDao.deleteById(recipeId);
		return true;
	}

	/*
	 *  更新一条处方
	 * 
	 * */
	@Override
	public boolean updateRecipe(Recipe recipe) {
		// TODO Auto-generated method stub
		
		return true;
	}

	/*
	 *  处方是否到期
	 * */
	@Override
	public boolean deadline(Recipe recipe, int day) {
		// TODO Auto-generated method stub
		
			return true;
	}

	/*
	 *  医嘱中是否有处方过期
	 * */
	@Override
	public boolean adviceDeadline(DoctorAdvice advice, Date date) {
		// TODO Auto-generated method stub
		Date startTime = advice.getExecuteTime();
		int interval = date.getDay()-startTime.getDay();
		List<Recipe> recipes = advice.getRecipes();
		for(Recipe recipe :recipes){
			if(deadline(recipe,interval)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public List<Recipe> patientSelf(int patientId) {
		// TODO Auto-generated method stub
		Query query  = Query.query(Criteria.where("patientId").
				is(patientId)).with(new Sort(Direction.DESC, "recipeId"));
		List<Recipe> recipes = recipeDao.queryList(query);
		if(recipes != null){
			return recipes;
		} else {
			return null;
		}
	}

}
