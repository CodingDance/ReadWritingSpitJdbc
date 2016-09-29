package com.yiting.test;

import com.yiting.Entity.User;
import com.yiting.dao.UserDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hzyiting on 2016/9/29.
 */
public class UserDaoTest extends BaseTest {

	@Autowired
	private UserDao userDao;

	@Test
	public void insertUser(){
		User user=new User();
		user.setUid("12345");
		user.setName("yiting");
		userDao.add(user);
	}


	@Test
	public void getUser(){
		User user=userDao.get(1L);
		System.out.print(user);
	}

}
