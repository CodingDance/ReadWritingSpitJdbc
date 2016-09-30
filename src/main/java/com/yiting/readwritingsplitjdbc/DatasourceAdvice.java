package com.yiting.readwritingsplitjdbc;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by hzyiting on 2016/9/29.
 */
public class DatasourceAdvice {
	Logger logger = Logger.getLogger(DatasourceAdvice.class);

	/**
	 * 对数据库查询方法进行切面编程，注入不容的数据源，通过ThreadLocal方式
	 * @param point
	 */
	public void before(JoinPoint point) {
		Object target = point.getTarget();
		String method = point.getSignature().getName();

		Class<?>[] classz = target.getClass().getInterfaces();
		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();

		try {
			Method m = classz[0].getMethod(method, parameterTypes);
			if(m!=null) {
				if (m.isAnnotationPresent(DataSourceType.class)) {
					DataSourceType dataSourceType = m.getAnnotation(DataSourceType.class);
					DynamicDataSourceHolder.putDataSourceKey(dataSourceType.value());
					System.out.println("dataSourceType:" + dataSourceType.value());
				}else{
					DynamicDataSourceHolder.putDataSourceKey(DataSourceType.MASTER);
				}
			}
		} catch (Exception e) {
			logger.error("Advice Error:",e);
		}


	}

}
