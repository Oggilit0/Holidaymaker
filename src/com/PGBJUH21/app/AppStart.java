package com.PGBJUH21.app;

import com.PGBJUH21.DatabaseTables.Customer;
import com.PGBJUH21.utilities.AppUtils;

import java.util.ArrayList;

/*

reseadministratören kan skapa bokningar (genom att alla queries för detta kan köras).
Grundläggande val (admin kan markera att detta ska finnas i anslutning till boendet)
Pool
Kvällsunderhållning
Barnklubb
Restaurang

Beskriva sällskapet
Antal personer, deras för- efternamn, email, telefonnr och födelsedatum.

När administratören pratar med en kund (t ex över telefon, chat, mail eller personligen), ska administratören kunna:
registrera in kunden.
söka efter lediga rum med specificerade sökkriterier (se lista nedan), mellan specificerade datum,
och sedan boka dem.
avboka rum.
Administratörens gränssnitt ska vara en meny i konsollen.
Efter ett menyval kan administratören behöva svara på flera frågor efter varandra
för att samla in data, som sedan ska skrivas till databasen, eller ligga till grund för
sökning Exempel på en sådan konsoll-meny med följdfrågor finns här


Skapa bokningar med tilläggstjänster (extrasäng, halvpension, helpension).
Ändra detaljerna i en bokning.
Söka på boenden baserat på avstånd till strand (hur långt borta får boendet max ligga ifrån en strand)
Söka på boenden baserat på till centrum (hur långt borta får boendet max ligga ifrån ett centrum)
Söktraffärar ska kunna ordnas på pris (lågt till högt)
Söktraffärar ska kunna ordnas på omdöme (högt till lågt)


 */

// Skapa fler konstruktorer för inmatning av värden som inte behövs
public class AppStart {

        private DataService ds;

    AppStart(){
        ds = new DataService();
        ds.connect();
        ArrayList<Customer> customers = ds.getAllCustomers();
//        for(var customer : customers){
//            System.out.println(customer);
//        }
//        ds.createCustomer("Oskar","Jonsson","Oskar.jon@outlook.com","07011111111","1905-05-23");
//        ds.createCustomer("Henrik","Adolfsson","Henrik.a@gmail.com","","1905-05-23");
//        ds.createCustomer("Jan-Erik","Burman","","05044289","1905-05-23");
//        ds.createCustomer("Svante","Einarsson","","","1905-05-23");

        mainMenu();
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

    public void mainMenu(){
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

    public Customer customerForm(){
        Customer customer = null;

        System.out.println("Please write your full name");
        String input = AppUtils.userInput("^[\\p{L} .'-]+$");
        String[] fullName= input.split(" ");
        System.out.println("Please write your E-mail, Leave empty if you don't want to use one");
        String email = AppUtils.userInput("null");
        System.out.println("Please write your phone, Leave empty if you don't want to use one");
        String phone = AppUtils.userInput("null");
        System.out.println("Please write your birth date");
        String birthdate = AppUtils.userInput("\\d{4}-\\d{2}-\\d{2}");

        ds.createCustomer(fullName[0], fullName[1], email,phone,birthdate);
        return customer;
    }

    public Customer selectExistingCustomer(){
        Customer customer = null;

        System.out.println("Please write the name of the guest");

        return customer;
    }

    public void createBookingMenu(){
        switch(AppUtils.menuBuilder("Create Booking", 1,3,"Add guest to party","Create New Party","Back to main menu")){
            case 1:
                System.out.println("1. Create new guest\n2. Choose existing guest");
                if(AppUtils.userInput(1,2) == 1){
                    customerForm();
                }else{
                    System.out.println("Write name of customer");
                    String customer = AppUtils.userInput("^[\\p{L} .'-]+$");
                    ArrayList<Customer> customers = ds.getCustomer(customer);
                    int c = 1;
                    for(Customer existingGuests : customers){
                        System.out.println(c + ". " + existingGuests);
                    }
                    if(customers.size() > 0){
                        AppUtils.userInput(1,customers.size());
                    }else{
                        System.out.println("No guest with that name");
                    }


                }
                break;
            case 2:
                break;
            case 3:
                mainMenu();
                break;
        }
    }

    public void editBookingMenu(){
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
