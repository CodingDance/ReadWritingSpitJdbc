package com.yiting.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Feng Changjian (hzfengchj@corp.netease.com)
 * @version $Id: BaseTest.java, v 1.0 2015年4月7日 上午11:37:06
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:beans-dao.xml"})
public class BaseTest {

}
