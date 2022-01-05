package com.PGBJUH21.DatabaseTables;

public class Customer {

    private int id;
    private String fName;
    private String lName;
    private String email;
    private String phone;
    private java.sql.Date birthdate;

    public Customer(int id, String fName, String lName, String email, String phone, java.sql.Date birthdate) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;

    }
}
