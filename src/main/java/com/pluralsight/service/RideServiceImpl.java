package com.pluralsight.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pluralsight.model.Ride;
import com.pluralsight.repository.RideRepository;

@Service("rideService")
public class RideServiceImpl implements RideService
{
	@Autowired
	private RideRepository rideRepository;
	
	@Override
	public List<Ride> getRides() {
		return rideRepository.getRides();
	}

	@Override
	public Ride createRide(Ride ride)
	{
		return rideRepository.createRide(ride);
	}

	@Override
	public Ride getRide(Integer id)
	{
		return rideRepository.getRide(id);
	}

	@Override
	public Ride updateRide(Ride ride)
	{
		return rideRepository.updateRide(ride);
	}

	@Override
	@Transactional //Allows for the rolling back of changes if an exception occurs during this method
	public void batch()
	{
		List<Ride> rides = rideRepository.getRides();

		List<Object[]> pairs = new ArrayList<>();

		for (Ride ride : rides)
		{
			Object [] tmp = {new Date(), ride.getId()};
			pairs.add(tmp);
		}

		rideRepository.updateRides(pairs);

		/*
		throw new DataAccessException("Testing Exception Handling")
		{
			//This simulates as if going partially through our code and then an error happens so we want to rollback (i.e. our TransactionalManager).
			//The test scenario is if we create a new Ride in our RestControllerTest (by calling testCreateRide), it won't have a Date yet. So, we do
			//testBatchUpdate to add a Date to it, however, when it is called in this method here, we have this exception, signalling that we should
			//not go through with this transaction and indeed, it does NOT add a Date and instead rolls back the data.
		};
		*/
	}

	@Override
	public void deleteRide(Integer id)
	{
		rideRepository.deleteRide(id);
	}
}
