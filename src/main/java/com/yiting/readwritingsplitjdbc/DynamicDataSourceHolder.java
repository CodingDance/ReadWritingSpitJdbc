package com.yiting.readwritingsplitjdbc;

/**
 * Created by hzyiting on 2016/9/29.
 */
public class DynamicDataSourceHolder {
	public static final ThreadLocal<String> holder=new ThreadLocal<>();

	public static void putDataSource(String datasourceName){
		holder.set(datasourceName);
	}

	public static String getDataSouce() {
		return holder.get();
	}
}
