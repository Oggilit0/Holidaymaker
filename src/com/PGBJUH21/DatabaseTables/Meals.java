package com.PGBJUH21.DatabaseTables;

public class Meals {

    private int id;
    private int roomId;
    private String meals;

    public Meals(int id, int roomId, String meals) {

        this.id = id;
        this.roomId = roomId;
        this.meals = meals;

    }
}