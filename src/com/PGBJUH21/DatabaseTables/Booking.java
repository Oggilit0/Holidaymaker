package com.PGBJUH21.DatabaseTables;
public class Booking {

    private int id;
    private int customerId;
    private java.sql.Date checkInDate;
    private java.sql.Date checkOutDate;

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
