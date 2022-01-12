package com.PGBJUH21.databasetables;

/**
 * Unused class created before databases was implemented.
 * Left implemented if needed in future upgrades
 */
public class Party {

    private final int customerId;
    private final int bookingId;
    private final int roomId;

    public Party(int customerId, int bookingId, int roomId) {

        this.customerId = customerId;
        this.bookingId = bookingId;
        this.roomId = roomId;

    }

    public String toString(){

        return "Customer id: " + customerId + " Booking id: " + bookingId + " Room id: " + roomId;
    }

}
