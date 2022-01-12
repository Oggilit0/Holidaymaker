package com.PGBJUH21.databasetables;

/**
 * Unused class created before databases was implemented.
 * Left implemented if needed in future upgrades
 */
public class Booking {

    private final int id;
    private final int customerId;
    private final java.sql.Date checkInDate;
    private final java.sql.Date checkOutDate;

    public Booking(int id, int customerId, java.sql.Date checkInDate, java.sql.Date checkOutDate) {
        this.id = id;
        this.customerId = customerId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String toString(){

        return "id: " + id + " Customer Id: " + customerId + " Check in date: " + checkInDate + " Check out date: " + checkOutDate;
    }

}
