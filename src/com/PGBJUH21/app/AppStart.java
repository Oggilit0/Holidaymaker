package com.PGBJUH21.app;

import com.PGBJUH21.DatabaseTables.Customer;
import com.PGBJUH21.DatabaseTables.Hotel;
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
        chooseRoom();
        //ArrayList<Customer> customers = ds.getAllCustomers();
//        for(var customer : customers){
//            System.out.println(customer);
//        }

        mainMenu();
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

        String input = AppUtils.userInput("full name");
        String[] fullName= input.split(" ");
        String email = AppUtils.userInput("email, leave empty if you don't want to use one");
        String phone = AppUtils.userInput("phone, leave empty if you dont' want to use one");
        String birthdate = AppUtils.userInput("birth date");

        ds.createCustomer(fullName[0], fullName[1], email,phone,birthdate);
        return customer;
    }

    public Customer selectExistingCustomer(){
        Customer customer = null;

        System.out.println("Please write the name of the guest");

        return customer;
    }

    public void chooseRoom(){
        // Might want to change the 10 to something else
        System.out.println("How many people are you in your party?");
        int partySize = AppUtils.userInput(1,10);
        System.out.println("How many rooms do you want to book?");
        int rooms = AppUtils.userInput(1,partySize);
        System.out.println("Please answer the following questions with yes/no or y/n");
        boolean pool = trueOrFalse("Are you looking for a hotel with a Pool?");
        boolean entertainment = trueOrFalse("Are you looking for a hotel with Entertainment?");
        boolean kidsClub = trueOrFalse("Are you looking for a hotel with a Childrens club?");
        boolean restaurant = trueOrFalse("Are you looking for a hotel with a Restaurant?");

        ArrayList<Hotel> hotel = ds.getHotel(pool,entertainment,kidsClub,restaurant);

        System.out.println("\nFound " + hotel.size() + " matches\n");
        for(Hotel h : hotel){
            System.out.println(h);
        }
    }

    public boolean trueOrFalse(String question){
        boolean bol;
        String input = AppUtils.userInput(question);
        if(input.equalsIgnoreCase( "yes" ) || input.equalsIgnoreCase("y")){
            bol = true;
        }else{
            bol = false;
        }
        return bol;
    }

    public void createBookingMenu(){
        switch(AppUtils.menuBuilder("Create Booking", 1,3,"Create New Booking","test 2","Back to main menu")){
            case 1:
                Customer responsibleId = null;
                String chkInDate;
                String chkOutDate;
                System.out.println("1. Create new guest\n2. Choose existing guest");
                if(AppUtils.userInput(1,2) == 1){
                    responsibleId = customerForm();
                }else{
                    boolean validCustomer = false;
                    do {
                        String customer = AppUtils.userInput("customer");
                        ArrayList<Customer> customers = ds.getCustomer(customer);
                        int c = 1;
                        for (Customer existingGuests : customers) {
                            System.out.println(c + ". " + existingGuests);
                        }
                        if (customers.size() > 0) {
                            responsibleId = customers.get(AppUtils.userInput(1, customers.size()) - 1);
                            validCustomer = true;
                        } else {
                            System.out.println("No guest with that name");
                        }
                    }while(!validCustomer);
                }
                chkInDate = AppUtils.userInput("Check in date");
                chkOutDate = AppUtils.userInput("Check out date");
                ds.createBooking(responsibleId.getId(),chkInDate,chkOutDate);
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
