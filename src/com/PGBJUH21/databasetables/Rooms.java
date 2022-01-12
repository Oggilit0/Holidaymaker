package com.PGBJUH21.databasetables;

/**
 * Unused class created before databases was implemented.
 * Left implemented if needed in future upgrades
 */
public class Rooms {

    private final int roomId;
    private final int hotelId;
    private final int beds;
    private final int price;

    public Rooms(int roomId, int hotelId, int beds, int price) {

        this.roomId = roomId;
        this.hotelId = hotelId;
        this.beds = beds;
        this.price = price;

    }

    public String toString(){

        return "Room id: " + roomId + " Hotel id: " + hotelId + " Beds: " + beds + " Price: " + price;
    }

}
