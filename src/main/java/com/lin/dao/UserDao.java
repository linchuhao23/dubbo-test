package com.lin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.lin.entity.User;

@Repository
public class UserDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<User> listAll() {
		RowMapper<User> rowMapper = new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				User user = new User();
				user.setId(id);
				user.setName(name);
				return user;
			}
		};
		return jdbcTemplate.query("select * from t_user", rowMapper);
	}

	public int save(User user) {
		final User u = user;
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("insert into t_user(name) values(?)", Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, u.getName());
				return ps;
			}
		};
		KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(psc, generatedKeyHolder);
		return generatedKeyHolder.getKey().intValue();
	}
	
}
