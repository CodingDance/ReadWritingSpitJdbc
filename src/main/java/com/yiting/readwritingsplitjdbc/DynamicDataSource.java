package com.yiting.readwritingsplitjdbc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzyiting on 2016/9/29.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {


	private List<String> slaveDataSourceKeyList =new ArrayList<>();


	//保存registerDatasources用于获取数据源的数据
	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		for (Map.Entry<Object, Object> entry : targetDataSources.entrySet()) {
			String key = (String) entry.getKey();
			if (key != null && key.startsWith(DataSourceType.SLAVE)) {
				slaveDataSourceKeyList.add(key);
			}
		}
		super.setTargetDataSources(targetDataSources);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSourceKey = DynamicDataSourceHolder.getDataSouceKey();
		if (DataSourceType.MASTER.equals(dataSourceKey)) {
			return dataSourceKey;
		} else {
			if (slaveDataSourceKeyList != null && !slaveDataSourceKeyList.isEmpty()) {
				int random = (int) (Math.random() * slaveDataSourceKeyList.size());
				return slaveDataSourceKeyList.get(random);
			} else {
				logger.error("no slave datasource set");
				throw new IllegalArgumentException("no slave datasource set");
			}
		}
	}



	//Todo 跑一个定时任务来 check，Connection的死活，从而保证如果master宕机则进行报警，slave宕机则进行salve切换，如果所有salve挂掉，则切换到主上。


}
