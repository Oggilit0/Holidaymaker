package com.PGBJUH21.querytables;

/**
 * Class that stores an available rooms with imported data
 */
public class AvailableRoom {
    private final String hotelName;
    private final int roomId;
    private final int beds;
    private final int price;
    private final int distBeach;
    private final int distDowntown;
    private final int review;

    public AvailableRoom(String hotelName, int roomId, int beds, int price, int distBeach, int distDowntown, int review) {
        this.hotelName = hotelName;
        this.roomId = roomId;
        this.beds = beds;
        this.price = price;
        this.distBeach = distBeach;
        this.distDowntown = distDowntown;
        this.review = review;
    }

    public String toString(){

        return "Hotel Name: " + hotelName + ". room id: " +
                roomId + ". Beds: " + beds +
                ". Price: $" + price +
                ". Distance to beach: " + distBeach + "m" +
                ". Distance to downtown: " + distDowntown +"m" +
                ". Review: " + review;
    }

    public int getRoomId() {
        return roomId;
    }

}
