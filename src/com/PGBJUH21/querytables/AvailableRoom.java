package com.PGBJUH21.querytables;

public class AvailableRoom {
    private String hotelName;
    private int roomId;
    private int beds;
    private int price;
    private int distBeach;
    private int distDowntown;

    public AvailableRoom(String hotelName, int roomId, int beds, int price, int distBeach, int distDowntown) {
        this.hotelName = hotelName;
        this.roomId = roomId;
        this.beds = beds;
        this.price = price;
        this.distBeach = distBeach;
        this.distDowntown = distDowntown;
    }

    public String toString(){

        return "Hotel Name: " + hotelName + ". room id: " +
                roomId + ". Beds: " + beds +
                ". Price: $" + price +
                ". Distance to beach: " + distBeach + "m" +
                ". Distance to downtown: " + distDowntown +"m";
    }

    public int getRoomId() {
        return roomId;
    }
}
