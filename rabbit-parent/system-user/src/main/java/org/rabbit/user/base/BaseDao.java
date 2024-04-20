package org.rabbit.user.base;

import java.util.List;

import org.rabbit.entity.base.BaseEntity;

public interface BaseDao<T extends BaseEntity>{

	T insert(T entity);
	
	T update(T entity);
	
	int delete(Integer id);
	
	int deleteByLogical(Integer id);
	
	T get(Integer id);
	
	List<T> find(T entity);
	
}
