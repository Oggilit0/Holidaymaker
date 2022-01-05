package com.PGBJUH21.app;

import com.PGBJUH21.DatabaseTables.Customer;
import com.PGBJUH21.utilities.Menu;

import java.util.ArrayList;

public class AppStart {
    private Menu menu = new Menu();

    AppStart(){
        DataService ds = new DataService();
        ds.connect();
        ArrayList<Customer> customers = ds.getCustomer();
        for(var customer : customers){
            System.out.println(customer);
        }
        menu.mainMenu();
    }

    private void editGuest(){
/*
        First name
        last name
        email kan vara null
        phone kan vara null
        birthday
         */
    }

    private void createNewGuest(){
        /*
        First name
        last name
        email kan vara null
        phone kan vara null
        birthday
         */
    }

    private void selectAll(String table){
        String sql = "SELECT * FROM "+ table;
        String url = "jdbc:sqlite:src/appfiles/Holidaymaker.db";
    }

}
