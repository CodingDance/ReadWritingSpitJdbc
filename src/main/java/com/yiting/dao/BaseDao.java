package com.yiting.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public abstract class BaseDao {

	private static final Logger log = LoggerFactory.getLogger(BaseDao.class);
	/**
	 * 容器注入JdbcTemplate实例，Spirng Jdbc SQL操作
	 */
	@Resource
	protected JdbcTemplate jdbcTemplate;

	/**
	 * 容器注入NamedParameterJdbcTemplate实例，Spirng Jdbc SQL操作，功能比JdbcTemplate强大
	 */
	@Resource
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * 插入记录
	 * 
	 * @param sql
	 *            sql语句
	 * @param args
	 *            参数
	 * @return 插入记录数
	 */
	public int add(String sql, Object[] args) {
		return jdbcTemplate.update(sql, args);
	}

	/**
	 * 更新记录
	 * 
	 * @param sql
	 *            sql语句
	 * @param objs
	 *            参数
	 * @return 更新记录数
	 */
	public int update(String sql, Object[] objs) {
		return jdbcTemplate.update(sql, objs);
	}

	/**
	 * 删除记录
	 * 
	 * @param sql
	 *            sql语句
	 * @param args
	 *            参数
	 * @return 删除记录数
	 */
	public int delete(String sql, Object[] args) {
		return jdbcTemplate.update(sql, args);
	}

	/**
	 * 查找单个对象
	 * 
	 * @param sql
	 * @param args
	 * @param rowMapper
	 * @return
	 */
	protected <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) {
		try {
			return jdbcTemplate.queryForObject(sql, args, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	protected <T> T queryForObject(String sql, SqlParameterSource ps, RowMapper<T> rowMapper) {
		try {
			return namedParameterJdbcTemplate.queryForObject(sql, ps, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	protected <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) {
		try {
			return namedParameterJdbcTemplate.queryForObject(sql, paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 大小驼峰方式转换成下划线命名
	 * 
	 * @param name
	 * @return
	 */
	public String camel2UnderScode(String name) {
		StringBuilder result = new StringBuilder();
		if (name.length() > 0) {
			result.append(name.substring(0, 1).toLowerCase());

			for (int i = 1; i < name.length(); i++) {
				String str = name.substring(i, i + 1);
				if (str.equals(str.toUpperCase()) && !Character.isDigit(str.charAt(0))) {
					result.append("_");
				}
				result.append(str.toLowerCase());
			}
		}
		return result.toString();
	}

	/**
	 * 获取查询条件
	 * 
	 * @param head
	 *            查询表头如： select * from CronTask where
	 * @param params
	 *            查询条件
	 * @return
	 */
	public String getQueryCondition(String head, Map<String, Object> params) {
		StringBuilder sb = new StringBuilder(head).append(" 1 = 1");
		Iterator<Entry<String, Object>> iter = params.entrySet().iterator();

		while (iter.hasNext()) {
			if (!sb.toString().equals(head)) {
				sb.append(" and ");
			}
			Entry<String, Object> entry = iter.next();
			String newKey = camel2UnderScode(entry.getKey());
            // String newValue = warpValue(entry.getValue());
            sb.append(newKey).append(" = ").append(":").append(entry.getKey());
		}
		String sql = sb.toString();
        log.debug(sql);
		return sql;
	}

	/**
	 * 使用not in并且带查询条件
	 * @param head
	 * 				查询表头如： select * from CronTask where
	 * @param condition
	 * 				conditon not in (notParams)
	 * @param notParams
	 * @param params 查询条件
	 * @return
	 */
	public String getQueryNotInCondition(String head, Map<String, Object> params, String condition, List<String> notParams) {
		StringBuilder sb = new StringBuilder(head).append(" 1 = 1");
		Iterator<Entry<String, Object>> iter = params.entrySet().iterator();

		while (iter.hasNext()) {
			if (!sb.toString().equals(head)) {
				sb.append(" and ");
			}
			Entry<String, Object> entry = iter.next();
			String newKey = camel2UnderScode(entry.getKey());
			// String newValue = warpValue(entry.getValue());
			sb.append(newKey).append(" = ").append(":").append(entry.getKey());
		}
		if (notParams != null && !notParams.isEmpty()) {
			sb.append(" and ");
			String newCondition = camel2UnderScode(condition);
			sb.append(newCondition + " not in ( ");
			for (String notParam : notParams) {
				sb.append("'" + notParam + "',");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(" )");
		}
		String sql = sb.toString();
		return sql;
	}

	public StringBuilder getSbQueryCondition(String head, Map<String, Object> params) {
		StringBuilder sb = new StringBuilder(head).append(" 1 = 1");
		Iterator<Entry<String, Object>> iter = params.entrySet().iterator();

		while (iter.hasNext()) {
			if (!sb.toString().equals(head)) {
				sb.append(" and ");
			}
			Entry<String, Object> entry = iter.next();
			String newKey = camel2UnderScode(entry.getKey());
            // String newValue = warpValue(entry.getValue());
            sb.append(newKey).append(" = ").append(":").append(entry.getKey());
		}
		String sql = sb.toString();
        log.debug(sql);
		return sb;
	}

	protected void appendOrderBy(StringBuilder sb, String order, boolean asc) {
		sb.append(" order by ").append(order);
		if (asc) {
			sb.append(" asc");
		} else {
			sb.append(" desc ");
		}
	}

    /**
     * 慎用，有SQL注入风险，此方法参数不得接收用户输入。
     * 
     * @param sb
     * @param name
     * @param value
     */
    @Deprecated
	protected void appendNot(StringBuilder sb, String name, String value) {
		sb.append(" and ");
		sb.append(name).append("!='").append(value).append("'");
	}

	protected void appendNot(StringBuilder sb, Map<String, Object> notParams) {
		if (notParams.isEmpty())
			return;
		sb.append(" and ( ");
		for (String key : notParams.keySet()) {
            // Object value = notParams.get(key);
            String newKey = camel2UnderScode(key);
            sb.append(newKey).append(" != ").append(":").append(key);
		}
		sb.append(" ) ");
	}

    protected void appendAND(StringBuilder sb){
        sb.append(" AND ");
    }

    protected void appendComma(StringBuilder sb){
        sb.append(",");
    }

    protected void appendOR(StringBuilder sb){
        sb.append(" OR ");
    }


    protected void appendLess(StringBuilder sb, Map<String, Object> lessParams) {
        if (lessParams == null || lessParams.isEmpty())
            return;
        sb.append("( ");
        int size = lessParams.keySet().size();
        int i = 0;
        for (String key : lessParams.keySet()) {
            String newKey = camel2UnderScode(key);
            sb.append(newKey).append(" < ").append(":").append(key);
            i++;
            if(i < size) {
                appendAND(sb);
            }
        }
        sb.append(" ) ");
    }

    protected void appendEqual(StringBuilder sb, Map<String, Object> params) {
        if (params == null || params.isEmpty())
            return;
        sb.append("( ");
        int size = params.keySet().size();
        int i = 0;
        for (String key : params.keySet()) {
            String newKey = camel2UnderScode(key);
            sb.append(newKey).append(" = ").append(":").append(key);
            i++;
            if(i < size) {
                appendAND(sb);
            }
        }
        sb.append(" ) ");
    }

    protected void appendEqualOnComma(StringBuilder sb, Map<String, Object> params) {
        if (params == null || params.isEmpty())
            return;
        sb.append("( ");
        int size = params.keySet().size();
        int i = 0;
        for (String key : params.keySet()) {
            String newKey = camel2UnderScode(key);
            sb.append(newKey).append(" = ").append(":").append(key);
            i++;
            if(i < size) {
                appendComma(sb);
            }
        }
        sb.append(" ) ");
    }

    protected void appendMore(StringBuilder sb, Map<String, Object> moreParams) {
        if (moreParams == null || moreParams.isEmpty())
            return;
        sb.append("( ");
        int size = moreParams.keySet().size();
        int i = 0;
        for (String key : moreParams.keySet()) {
            String newKey = camel2UnderScode(key);
            sb.append(newKey).append(" > ").append(":").append(key);
            if(i < size) {
                appendAND(sb);
            }
        }
        sb.append(" ) ");
    }

	protected void appendLimit(StringBuilder sb, int limit, int offset) {
		sb.append(" limit ").append(limit);
		sb.append(" offset ").append(offset);
	}
	
	/**
	 * @Title: appendIn  
	 * @Description: 在sql语句中使用in关键字
	 * @param builder
	 * @param fieldName void
	 * @throws  
	 */
	public void appendIn(StringBuilder builder, String fieldName) {
		String tableFieldName = camel2UnderScode(fieldName);
		builder.append(" ")
				   .append(tableFieldName) 
				   .append(" in (:")
				   .append(fieldName)
				   .append(") ");
	}
	
	/**
	 * @Title: appendNotIn  
	 * @Description: 在sql语句中使用not in 关键字
	 * @param builder
	 * @param fieldName void
	 * @throws  
	 * 
	 */
	public void appendNotIn(StringBuilder builder, String fieldName) {
		String tableFieldName = camel2UnderScode(fieldName);
		builder.append(" ")
				   .append(tableFieldName) 
				   .append(" not in (:")
				   .append(fieldName)
				   .append(") ");
	}
	
	protected void appendOrderBy(StringBuilder sb, String order) {
		appendOrderBy(sb, order, true);
	}

	public String getQueryCondition(String head, Map<String, Object> params, String order) {
		return getQueryCondition(head, params, order, true);
	}

	public String getQueryCondition(String head, Map<String, Object> params, String order, boolean asc) {
		StringBuilder sb = new StringBuilder(head);
		Iterator<Entry<String, Object>> iter = params.entrySet().iterator();

		while (iter.hasNext()) {
			if (!sb.toString().equals(head)) {
				sb.append(" and ");
			}
			Entry<String, Object> entry = iter.next();
			String newKey = camel2UnderScode(entry.getKey());
            // String newValue = warpValue(entry.getValue());
            sb.append(newKey).append(" = ").append(":").append(entry.getKey());
		}
		sb.append(" order by ").append(order);
		if (asc) {
			sb.append(" asc");
		} else {
			sb.append(" desc ");
		}
		String sql = sb.toString();
        log.debug(sql);
		return sql;
	}

    /**
     * 获取查询条件。 慎用，有SQL注入风险，此方法参数不得接收用户输入。
     * 
     * @param head
     *            查询表头如： select * from CronTask where
     * @param andParams
     *            查询条件，将查询条件and出来
     * @param orParams
     *            查询条件，将查询条件or出来(允许一个字段多个值的or)，和andParams查询条件进行and操作，
     * @return
     */
    @Deprecated
	public String getAndOrQueryCondition(String head, Map<String, Object> andParams, Map<String, List<Object>> orParams) {
		andParams.put("1", 1);
		StringBuilder sb = new StringBuilder(head);
		Iterator<Entry<String, Object>> andIter = andParams.entrySet().iterator();
		Iterator<Entry<String, List<Object>>> orIter = orParams.entrySet().iterator();

		while (andIter.hasNext()) {
			if (!sb.toString().equals(head)) {
				sb.append(" and ");
			}
			Entry<String, Object> entry = andIter.next();
			String newKey = camel2UnderScode(entry.getKey());
            String newValue = warpValue(entry.getValue());
            sb.append(newKey).append(" = ").append(newValue);
		}

		sb.append(" and ( ");

		boolean Flag = false;

		while (orIter.hasNext()) {
			Entry<String, List<Object>> entry = orIter.next();
			String newKey = camel2UnderScode(entry.getKey());
			List<Object> list = entry.getValue();

			if (Flag) {
				sb.append(" or ");
			}

			for (int i = 0; i < list.size(); i++) {
				if (i > 0) {
					sb.append(" or ");
				}
                String newValue = warpValue(list.get(i));
                sb.append(newKey + " = " + newValue);
			}

			Flag = true;
		}

		sb.append(" )");

		String sql = sb.toString();

        log.debug(sql);

		return sql;
	}

    /**
     * 拼接update语句 set value
     * @param head
     * @param setParams
     * @param queryParams
     * @return set a = ?, b = ?, c = ? where x = ? AND y = ?
     */
    public String getUpdateSetValues(String head, Map<String, Object> setParams, Map<String, Object> queryParams) {
        Assert.notEmpty(setParams);
        Assert.notEmpty(queryParams);
        StringBuilder sb = new StringBuilder(head).append(" SET ");
        Iterator<Entry<String, Object>> iter = setParams.entrySet().iterator();
        int size = setParams.size();
        int i = 0;
        while (iter.hasNext()) {

            Entry<String, Object> entry = iter.next();
            String oldKey = entry.getKey();
            String newKey = camel2UnderScode(oldKey);
            if(i < size - 1) {
                sb.append(newKey).append("=:").append(oldKey).append(",");
            }else {
                sb.append(newKey).append("=:").append(oldKey);
            }
            i++;
        }
        sb.append(" WHERE ");

        iter = queryParams.entrySet().iterator();
        i = 0;
        while (iter.hasNext()) {
            Entry<String, Object> entry = iter.next();
            String oldKey = entry.getKey();
            String newKey = camel2UnderScode(oldKey);
            if(i > 0) {
                sb.append(" AND ").append(newKey).append("=:").append(oldKey);
            }else {
                sb.append(newKey).append("=:").append(oldKey);
            }
            i++;
        }

        String sql = sb.toString();
        log.debug(sql);
        return sql;
    }

	/**
	 * 将value改成sql支持的格式
	 * 
	 * @param value
	 * @return
	 */
	private String warpValue(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof String) {
			return "'" + value + "'";
		} else if (value instanceof Boolean) {
			boolean boolValue = (Boolean) value;
			if (boolValue) {
				return "1";
			} else {
				return "0";
			}
		}
		return value.toString();
	}

	/**
	 * 返回int指的SQL通用方法
	 * 
	 * @param headSql
	 * @param params
	 * @return
	 */
	public Integer getInt(String headSql, Map<String, Object> params) {
		String sql = getQueryCondition(headSql, params);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
	}

	public static class IdsRowMapper implements RowMapper<Long> {

		private String id;

		public IdsRowMapper(String id) {
			this.id = id;
		}

		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			long Id = rs.getLong(id);
			return Id;
		}
	}
	
	/**
	 * 添加返回统一的JDBC模板
	 * 
	 * @return
	 */
	public JdbcTemplate getTemplate() {
		return jdbcTemplate;
	}

}
