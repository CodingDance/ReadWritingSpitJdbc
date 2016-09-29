package com.yiting.readwritingsplitjdbc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by hzyiting on 2016/9/29.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		return DynamicDataSourceHolder.getDataSouce();
	}
}
