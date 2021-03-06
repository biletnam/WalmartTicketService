package com.lab.walmart.TicketService.service;

import java.util.List;

import com.lab.walmart.TicketService.DAO.SeatDAO;
import com.lab.walmart.TicketService.DAO.SeatHoldDAO;
import com.lab.walmart.TicketService.model.SeatHold;
import com.lab.walmart.TicketService.model.SeatStatus;

public class TicketServiceImpl implements TicketService{
	 
	
	/**
	 * In real life, the DAO layer will be injected into service
	 * following singleton design pattern
	 */
	private final SeatDAO seatDAO = new SeatDAO();
	private final SeatHoldDAO seatHoldDAO = new SeatHoldDAO();

	public int numSeatsAvailable() {
		return seatDAO.getAvailableSeatNumber();
	}

	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		
		if (numSeats < 1) {
			throw new IllegalArgumentException("numSeats need to be a positive integer");
		}
		
		//Throw exception if numSeats available is less than numSeats
		if (numSeatsAvailable() < numSeats) {
			throw new SeatServiceException("Not enough seats left to be hold.");
		}
		
		List<Integer> seatIDs = seatDAO.takeAvailableSeatIds(numSeats);
		SeatHold sh = new SeatHold(seatIDs.get(0), seatIDs, SeatStatus.H, customerEmail);
		
		sh = seatHoldDAO.saveSeatHold(sh);
		return sh;
	}

	public String reserveSeats(int seatHoldId, String customerEmail) {
		
		SeatHold sh = seatHoldDAO.getSeatHold(seatHoldId);
		if (null == sh) {
			throw new SeatServiceException("SeatHold does not exist.");
		}
		if (sh.getStatus().equals(SeatStatus.R)) {
			throw new SeatServiceException("SeatHold already reserved.");
		}
		if (!sh.getCustomerEmail().equals(customerEmail)) {
			throw new SeatServiceException("SeatHold is held by another customer.");
		}
		
		sh.setStatus(SeatStatus.R);
		seatHoldDAO.saveSeatHold(sh);
		return sh.getSeatHoldID().toString();
	}

	@Override
	public void initialize() {
		SeatDAO.initialize();
		SeatHoldDAO.initialize();
	}

}
