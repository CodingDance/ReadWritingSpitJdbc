package com.yiting.dao.impl;

import com.yiting.Entity.User;
import com.yiting.dao.BaseDao;
import com.yiting.dao.UserDao;
import com.yiting.readwritingsplitjdbc.DataSourceType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hzyiting on 2016/9/29.
 */
@Component
public class UserDaoImpl extends BaseDao implements UserDao {
	@Override
	public long add(User user) {
		String sql = "insert into User (uid,name) values(:uid,:name)";
		SqlParameterSource ps = new BeanPropertySqlParameterSource(user);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(sql, ps, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public User get(long id) {
		String sql = "select * from User  where id=:id";
		return queryForObject(sql, new MapSqlParameterSource("id", id), new UserMapper());
	}


	final class UserMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("id"));
			user.setName(rs.getString("name"));
			user.setUid(rs.getString("uid"));

			return user;
		}
	}
}


