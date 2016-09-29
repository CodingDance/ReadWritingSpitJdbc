package com.yiting.dao;

import com.yiting.Entity.User;
import com.yiting.readwritingsplitjdbc.DataSourceType;

/**
 * Created by hzyiting on 2016/9/29.
 */
public interface UserDao {

	@DataSourceType("master")
	public long add(User user);

	@DataSourceType("slave")
	public User get(long userId);

}
