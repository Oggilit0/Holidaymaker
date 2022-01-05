package com.PGBJUH21.DatabaseTables;

public class Hotel {
    private int id;
    private String name;
    private boolean pool;
    private boolean kidsClub;
    private boolean entertainment;
    private boolean restaurant;
    private int disBeach;
    private int disTown;
    private int extraBed;
    private int halfBoard;
    int fullBoard;
    int review;

    public Hotel(int id, String name, boolean pool, boolean kidsClub, boolean entertainment,
                 boolean restaurant, int disBeach, int disTown, int extraBed, int halfBoard,
                 int fullBoard, int review) {

        this.id = id;
        this.name = name;
        this.pool = pool;
        this.kidsClub = kidsClub;
        this.entertainment = entertainment;
        this. restaurant = restaurant;
        this.disBeach = disBeach;
        this.disTown = disTown;
        this.extraBed = extraBed;
        this.halfBoard = halfBoard;
        this.fullBoard = fullBoard;
        this.review = review;

    }
}
