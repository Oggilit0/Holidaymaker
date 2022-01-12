package com.PGBJUH21.databasetables;

/**
 * Class that stores a customer with imported data
 */
public class Customer {

    private final int id;
    private final String fName;
    private final String lName;
    private final String email;
    private final String phone;
    private final String birthdate;

    public Customer(int id, String fName, String lName, String email, String phone, String birthdate) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;

    }

    public int getId() {
        return id;
    }

    public String toString(){

        return "id: " + id + " name: " + fName + " " + lName + " email: " + email + " phone: " + phone + " birthdate: " + birthdate;
    }

}
