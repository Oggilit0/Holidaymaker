package com.PGBJUH21.DatabaseTables;

public class Party {

    private int customerId;
    private int bookingId;
    private int roomId;

    public Party(int customerId, int bookingId, int roomId) {

        this.customerId = customerId;
        this.bookingId = bookingId;
        this.roomId = roomId;

    }
}
