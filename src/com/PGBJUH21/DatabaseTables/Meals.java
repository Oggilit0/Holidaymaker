package com.PGBJUH21.Databasetables;

public class Meals {

    private int id;
    private int roomId;
    private String meals;

    public Meals(int id, int roomId, String meals) {

        this.id = id;
        this.roomId = roomId;
        this.meals = meals;

    }

    public String toString(){

        return "id: " + id + " Room id: " + roomId + " Meals: " + meals;
    }

}
