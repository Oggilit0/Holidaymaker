package com.PGBJUH21.databasetables;

/**
 * Unused class created before databases was implemented.
 * Left implemented if needed in future upgrades
 */
public class Meals {

    private final int id;
    private final int roomId;
    private final String meals;

    public Meals(int id, int roomId, String meals) {

        this.id = id;
        this.roomId = roomId;
        this.meals = meals;

    }

    public String toString(){

        return "id: " + id + " Room id: " + roomId + " Meals: " + meals;
    }

}
