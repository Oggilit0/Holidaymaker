package com.PGBJUH21.utilities;

/*
Skapa bokningar med tilläggstjänster (extrasäng, halvpension, helpension).
Ändra detaljerna i en bokning.
Söka på boenden baserat på avstånd till strand (hur långt borta får boendet max ligga ifrån en strand)
Söka på boenden baserat på till centrum (hur långt borta får boendet max ligga ifrån ett centrum)
Söktraffärar ska kunna ordnas på pris (lågt till högt)
Söktraffärar ska kunna ordnas på omdöme (högt till lågt)
 */
public class Menu {

    public static void mainMenu(){
        switch(AppUtils.menuBuilder("Main Menu", 1,3,"Create new booking","Edit existing booking")){
            case 1:
                createBookingMenu();
                break;
            case 2:
                editBookingMenu();
                break;
            case 3:
                break;
        }
    }

    public static void createBookingMenu(){
        switch(AppUtils.menuBuilder("Create Booking", 1,3,"Test1","Test2","Back to main menu")){
            case 1:
                break;
            case 2:
                break;
            case 3:
                mainMenu();
                break;
        }
    }

    public static void editBookingMenu(){
        switch(AppUtils.menuBuilder("Edit Booking", 1,3,"Test1","Test2","Back to main menu")){
            case 1:
                break;
            case 2:
                break;
            case 3:
                mainMenu();
                break;
        }
    }

}
