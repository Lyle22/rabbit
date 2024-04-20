package org.rabbit.user.base;

import java.util.List;

import org.rabbit.entity.base.BaseEntity;
import org.springframework.stereotype.Service;

@Service
public class BaseService<E extends BaseDao<T>, T extends BaseEntity> {

	private BaseDao<T> baseDao;

	public T insert(T entity) {
		return baseDao.insert(entity);
	}

	public T update(T entity) {
		return baseDao.update(entity);
	}

	boolean delete(Integer id) {
		int result = baseDao.delete(id);
		return result > 0 ? true : false;
	}

	boolean deleteByLogical(Integer id) {
		int result = baseDao.deleteByLogical(id);
		return result > 0 ? true : false;
	}

	T get(Integer id) {
		return this.baseDao.get(id);
	}

	List<T> find(T entity) {
		return this.baseDao.find(entity);
	}
}
