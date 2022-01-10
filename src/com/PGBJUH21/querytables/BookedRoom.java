package com.PGBJUH21.querytables;

public class BookedRoom {
    private int roomId;
    private int bookingId;
    private String checkInDate;
    private String checkOutDate;
    private String fullName;

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

    public int getBookingId() {
        return bookingId;
    }
}
