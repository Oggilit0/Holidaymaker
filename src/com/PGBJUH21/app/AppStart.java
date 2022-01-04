package com.PGBJUH21.app;

import com.PGBJUH21.utilities.Menu;

public class AppStart {
    private Menu menu = new Menu();

    AppStart(){
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
    }

}
