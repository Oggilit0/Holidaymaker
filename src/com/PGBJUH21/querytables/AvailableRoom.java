package com.PGBJUH21.querytables;

public class AvailableRoom {
    private String hotelName;
    private int roomId;
    private int beds;
    private int price;

    public AvailableRoom(String hotelName, int roomId, int beds, int price) {
        this.hotelName = hotelName;
        this.roomId = roomId;
        this.beds = beds;
        this.price = price;
    }

    public String toString(){

        return "Hotel Name: " + hotelName + " room id: " +
                roomId + " Beds: " + beds +
                " Price: " + price;
    }

    public int getRoomId() {
        return roomId;
    }
}
