package com.PGBJUH21.querytables;

/**
 * Class that stores a booked rooms with imported data
 */
public class BookedRoom {
    private final int roomId;
    private final int bookingId;
    private final String checkInDate;
    private final String checkOutDate;
    private final String fullName;

    public BookedRoom(int roomId, int bookingId, String checkInDate, String checkOutDay, String fullName) {

        this.roomId = roomId;
        this.bookingId = bookingId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDay;
        this.fullName = fullName;
    }

    public String toString(){

        return " Booking id: " + bookingId +
                " Room id: " + roomId +
                " Check in date: " + checkInDate +
                " Check out date: " + checkOutDate +
                " Full name: " + fullName;
    }
}
