package com.PGBJUH21.databasetables;

/**
 * Unused class created before databases was implemented.
 * Left implemented if needed in future upgrades
 */
public class Hotel {
    private final int id;
    private final String name;
    private final boolean pool;
    private final boolean kidsClub;
    private final boolean entertainment;
    private final boolean restaurant;
    private final String disBeach;
    private final String disTown;
    private final int extraBed;
    private final int halfBoard;
    int fullBoard;
    int review;

    public Hotel(int id, String name, boolean pool, boolean kidsClub, boolean entertainment,
                 boolean restaurant, String disBeach, String disTown, int extraBed, int halfBoard,
                 int fullBoard, int review) {

        this.id = id;
        this.name = name;
        this.pool = pool;
        this.kidsClub = kidsClub;
        this.entertainment = entertainment;
        this.restaurant = restaurant;
        this.disBeach = disBeach;
        this.disTown = disTown;
        this.extraBed = extraBed;
        this.halfBoard = halfBoard;
        this.fullBoard = fullBoard;
        this.review = review;

    }

    public String toString(){

        return "Hotel: " + name +
                "\nPool: " + pool +
                "\nKids Club: " + kidsClub +
                "\nEntertainment: " + entertainment +
                "\nRestaurant: " + restaurant +
                "\nDistance to beach: " + disBeach +
                "\nDistance to town: " + disTown +
                "\nExtra bed: " + extraBed +
                "\nHalf board: " + halfBoard +
                "\nFull board: " + fullBoard +
                "\nReview: " + review +
                "\n--------------------------";
    }

}
