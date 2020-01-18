package com.pluralsight.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.pluralsight.model.Ride;
import com.pluralsight.repository.util.RideRowMapper;

@Repository("rideRepository")
public class RideRepositoryImpl implements RideRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Ride> getRides()
	{
		//This uses an externalised class RideRowMapper.
		//We could also instead use an anonymous inner class that does the same thing as RideRowMapper directly in this method
		List<Ride> rides = jdbcTemplate.query("select * from ride", new RideRowMapper());

		/* Or we could do it the hardcoded way:
		Ride ride = new Ride();
		ride.setName("Corner Canyon");
		ride.setDuration(120);
		List <Ride> rides = new ArrayList<>();
		rides.add(ride);
		*/

		return rides;
	}

	@Override
	public Ride createRide(Ride ride)
	{
		//The below is using the jdbcTemplate way for Create functionality
		//jdbcTemplate update method can be used for inserting, updating and deleting
		//jdbcTemplate.update("insert into ride (name, duration) values (?,?)", ride.getName(), ride.getDuration());

		/*The below is using the jdbcTemplate way as well as using KeyHolder and PreparedStatement to be able to get/return an
		objects key/id
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator()
		{
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException
			{
				PreparedStatement ps = con.prepareStatement("insert into ride (name, duration) values (?,?)", new String[] {"id"});
				ps.setString(1, ride.getName());
				ps.setInt(2, ride.getDuration());

				return ps;
			}
		}, keyHolder);

		Number id = keyHolder.getKey();

		return getRide(id.intValue());
		*/

		//The below is using the SimpleJDBCInsert way for Create functionality as well as able to get/return an objects key/id
		//Compared to the technique above, this way does not need an anonymous inner class or any SQL, it uses an ORM way instead.
		//In terms of efficiency/speed, both techniques are the same, just depends on preference
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);

		List<String> columns = new ArrayList<>();
		columns.add("name");
		columns.add("duration");

		insert.setTableName("ride");
		insert.setColumnNames(columns);

		Map<String, Object> data = new HashMap<>();
		data.put("name", ride.getName());
		data.put("duration", ride.getDuration());

		insert.setGeneratedKeyName("id");

		Number id = insert.executeAndReturnKey(data);

		System.out.println(id);

		return getRide(id.intValue());
	}

	@Override
	public Ride getRide(Integer id)
	{
		Ride ride = jdbcTemplate.queryForObject("select * from ride where id = ?", new RideRowMapper(), id);

		return ride;
	}

	@Override
	public Ride updateRide(Ride ride)
	{
		jdbcTemplate.update("update ride set name = ?, duration = ? where id = ?",
							ride.getName(), ride.getDuration(), ride.getId());

		return ride;
	}

	@Override
	public void updateRides(List<Object[]> pairs)
	{
		jdbcTemplate.batchUpdate("update ride set ride_date = ? where id = ?", pairs); //pairs is an arrayList with two objects, first is date,
		// second is id, that's why only need to pass in 'pairs' once when the sql statement is needing two variables
	}

	@Override
	public void deleteRide(Integer id)
	{
		//Using jdbcTemplate:
		//jdbcTemplate.update("delete from ride where id = ?", id);

		//Using NamedParameterJDBCTemplate: (This way could be better if needing to pass in many different parameters at once)
		NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("id", id);

		namedTemplate.update("delete from ride where id = :id", paramMap);
	}
}

