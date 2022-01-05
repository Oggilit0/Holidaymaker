package com.PGBJUH21.DatabaseTables;

public class Rooms {

    private int roomId;
    private int hotelId;
    private int beds;
    private int price;

    public Rooms(int roomId, int hotelId, int beds, int price) {

        this.roomId = roomId;
        this.hotelId = hotelId;
        this.beds = beds;
        this.price = price;

    }
}
