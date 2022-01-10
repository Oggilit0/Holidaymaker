package com.PGBJUH21.app;

import com.PGBJUH21.Databasetables.Customer;
import com.PGBJUH21.querytables.AvailableRoom;
import com.PGBJUH21.querytables.BookedRoom;
import com.PGBJUH21.utilities.AppUtils;

import java.util.ArrayList;


/*

*reseadministratören kan skapa bokningar (genom att alla queries för detta kan köras).
*Grundläggande val (admin kan markera att detta ska finnas i anslutning till boendet)
*Pool
*Kvällsunderhållning
*Barnklubb
*Restaurang

*Beskriva sällskapet
*Antal personer, deras för- efternamn, email, telefonnr och födelsedatum.

*När administratören pratar med en kund (t ex över telefon, chat, mail eller personligen), ska administratören kunna:
*registrera in kunden.
*söka efter lediga rum med specificerade sökkriterier (se lista nedan), mellan specificerade datum,
*och sedan boka dem.
avboka rum.
*Administratörens gränssnitt ska vara en meny i konsollen.
*Efter ett menyval kan administratören behöva svara på flera frågor efter varandra
*för att samla in data, som sedan ska skrivas till databasen, eller ligga till grund för
*sökning Exempel på en sådan konsoll-meny med följdfrågor finns här


*Skapa bokningar med tilläggstjänster (extrasäng, halvpension, helpension).
*Ändra detaljerna i en bokning.
*Söka på boenden baserat på avstånd till strand (hur långt borta får boendet max ligga ifrån en strand)
*Söka på boenden baserat på till centrum (hur långt borta får boendet max ligga ifrån ett centrum)
*Söktraffärar ska kunna ordnas på pris (lågt till högt)
*Söktraffärar ska kunna ordnas på omdöme (högt till lågt)





*Registrera kund
*Söka lediga rum
*    - antal personer
*    - pool
*    - barnklubb
*    - restaurang
*    - pris
*OCH boka dem
Avboka rum
*Skapa bokning

VG
*Skapa bokning med tilläggstjänst
*    - Extrasäng
*    - Halvpension
*    - Helpension
*Ändra detaljer i en bokning
*Söka med avstånd till strand
*Söka med avstånd till centrum
*Ordnas på pris Lågt till högt


 */

public class AppStart {

        private DataService ds;
        private ArrayList<Customer> currentParty;

    AppStart() throws InterruptedException {
        this.currentParty = new ArrayList<>();
        this.ds = new DataService();
        this.ds.connect();
        mainMenu();
    }



    public void mainMenu() throws InterruptedException {
        AppUtils.clearScreen();
        switch(AppUtils.menuBuilder("Main Menu", 1,4,"Create new booking","Edit existing booking", "Delete booking", "Search in database")){
            case 1:
                createCustomerMenu();
                break;
            case 2:
                editBookingMenu();
                break;
            case 3:
                deleteBookingMenu();
                break;
            case 4:
                searchDatabase();
                break;
        }
    }

    public void searchBookedRooms(){
        String orderBy = AppUtils.userInput("how you want to order the result. Room id, booking id, check in date, check out date, name");
        ArrayList<BookedRoom> bookedRooms =ds.bookedRooms(orderBy);
        int counter = 1;
        for(BookedRoom room : bookedRooms){
            System.out.println(counter + ". " + room);
            counter++;
        }
    }

    public void searchCustomers(){
        String orderBy = AppUtils.userInput("how you want to order the result. ,id, Name, email, phone, birthdate");
        ArrayList<Customer> customers = ds.getCustomer(orderBy);
        int counter = 1;
        for(Customer customer : customers){
            System.out.println(counter + ". " + customer);
            counter++;
        }
    }


    public void searchDatabase() throws InterruptedException {
        AppUtils.clearScreen();
        switch(AppUtils.menuBuilder("Search", 1,3,"Booked rooms", "Customer", "Booking", "Back to main menu")){
            case 1:
                searchBookedRooms();
                System.out.println("1. Return to menu");
                if(AppUtils.userInput(1,1) == 1){
                    searchDatabase();
                }
                break;
            case 2:
                searchCustomers();
                System.out.println("1. Return to menu");
                if(AppUtils.userInput(1,1) == 1){
                    searchDatabase();
                }
                break;
            case 3:
                searchBooking();
                System.out.println("1. Return to menu");
                if(AppUtils.userInput(1,1) == 1){
                    searchDatabase();
                }
                break;
            case 4:
                mainMenu();
                break;
        }
    }

    public void addGuest(){
        this.currentParty.add(customerForm());
        boolean cont;
        do{
            cont = AppUtils.trueOrFalse("Add another guest? yes/no\"");
            if(cont){
                this.currentParty.add(customerForm());
            }else{
                cont = false;
            }
        }while(cont);
    }

    /**
     * Show selected party in menu.
     */
    public void showParty (){
        if(this.currentParty.size() > 0){
            System.out.println("Current party");
            for(Customer c : this.currentParty){
                System.out.println(c);
            }
            System.out.println();
        }
    }

    /**
     * Menu interaction the let user to search for guest by name
     */
    public Customer chooseExistingGuest(){
        boolean validCustomer = false;
        Customer customer = null;
        do {
            String customerName = AppUtils.userInput("name of guest, or leave empty");
            if(customerName == ""){
                break;
            }
            ArrayList<Customer> listOfCustomers = ds.getCustomer(customerName);
            int c = 1;
            for (Customer existingGuests : listOfCustomers) {
                System.out.println(c + ". " + existingGuests);
            }
            if (listOfCustomers.size() > 0){
                System.out.print("Choose guest: ");
            }
            if (listOfCustomers.size() > 0) {
                int customerNr = AppUtils.userInput(1, listOfCustomers.size());
                //this.currentParty.add(listOfCustomers.get(customerNr-1));
                customer = listOfCustomers.get(customerNr-1);
                validCustomer = true;
            } else {
                System.out.println("No guest with that name");
            }
        }while(!validCustomer);
        return customer;
    }

    /**
     * Menu to let user to create a booking party by adding new guest or choose existing.
     * options to go back to main menu and clear the party, and to go forward to continue the booking
     * @throws InterruptedException
     */
    public void createCustomerMenu() throws InterruptedException {
        AppUtils.clearScreen();
        showParty();
        switch(AppUtils.menuBuilder("Booking", 1,4,"Create new guest","Choose existing guest","Continue with chosen party","Back to main menu and clear party")){
            case 1:
                addGuest();
                createCustomerMenu();
                break;
            case 2:
                Customer customer = chooseExistingGuest();
                if(customer != null){
                    this.currentParty.add(customer);
                }

                createCustomerMenu();
                break;
            case 3:
                if(currentParty.size() == 0){
                    createCustomerMenu();
                }else {
                    createBookingMenu();
                }
                break;
            case 4:
                this.currentParty.clear();
                mainMenu();
                break;
        }

    }

    public void editBooking(){
        int bookingId = Integer.parseInt(AppUtils.userInput("booking id to be updated"));
        String chkInDate = AppUtils.userInput("check in date to be updated");
        String chkOutDate = AppUtils.userInput("check out date to be updated");
        ds.updateBooking(bookingId,chkInDate,chkOutDate);
    }

    public void editGuest(){
        Customer customer = chooseExistingGuest();
        String fullName = AppUtils.userInput("guest full name or leave empty");
        String email = AppUtils.userInput("guest email or leave empty");
        String phone = AppUtils.userInput("guest phone or leave empty");
        String birthdate = AppUtils.userInput("guest birthdate or leave empty");
        ds.updateCustomer(customer.getId(),fullName,email,phone,birthdate);
    }

    public void editBookingMenu() throws InterruptedException {
        AppUtils.clearScreen();
        switch(AppUtils.menuBuilder("Edit Booking & Guests", 1,3,"Edit booking","Search for and edit guest","Back to main menu")){
            case 1:
                editBooking();
                editBookingMenu();
                break;
            case 2:
                editGuest();
                editBookingMenu();
                break;
            case 3:
                mainMenu();
                break;
        }
    }

    public ArrayList<BookedRoom> searchBooking() throws InterruptedException {
        String orderBy = AppUtils.userInput("Order of which you want the results. id, Name, Check in date, Check out date");
        ArrayList<BookedRoom> bookedRooms = ds.bookedRooms(orderBy);
        int counter = 1;
        for(BookedRoom room : bookedRooms){
            System.out.println(counter + ". " + room);
            counter++;
        }
        return bookedRooms;
    }



    public void deleteBookingMenu() throws InterruptedException {
        AppUtils.clearScreen();
        switch(AppUtils.menuBuilder("Delete booking", 1,3,"Delete booking","Back to main menu")){
            case 1:
                ArrayList<BookedRoom> bookedRooms = searchBooking();
                System.out.println(bookedRooms.size()+1 + ". Back to main menu");
                System.out.print("Enter booking: ");
                int bookingId = AppUtils.userInput(1,bookedRooms.size()+1);
                if(bookingId == bookedRooms.size()+1){
                    mainMenu();
                }
                ds.deleteBooking(bookingId);
                deleteBookingMenu();

                break;
            case 2:
                mainMenu();
                break;
        }
    }



    public Customer customerForm(){

        String input = AppUtils.userInput("full name");
        String[] fullName= input.split(" ");
        String email = AppUtils.userInput("email, leave empty if you don't want to use one");
        String phone = AppUtils.userInput("phone, leave empty if you dont' want to use one");
        String birthdate = AppUtils.userInput("birth date");

        return  ds.getCustomerFromCustomerId(ds.createCustomer(fullName[0], fullName[1], email,phone,birthdate));
    }


    /**
     * Method to handle the booking.
     * I apologize for the size. Its a mess
     * @throws InterruptedException
     */
    public void createBookingMenu() throws InterruptedException {
        boolean enoughSpace;
        boolean extraBed;
        boolean fullBoard;
        boolean halfBoard;
        int rooms;
        int totalPrice = 0;
        int partySize = currentParty.size();
        int counter = 1;
//        int chkInDateAsInt;
//        int chkOutDateAsInt;
        int sumOfDays = 0;


        do{
            System.out.println("How many rooms do you want to book?");
            rooms = AppUtils.userInput(1,partySize);
            System.out.println(rooms);
            System.out.println(currentParty.size());
            if(rooms*5 >= currentParty.size()){
                enoughSpace = true;
            }else{
                System.out.println("Sorry but you need more rooms for that many guests");
                enoughSpace = false;
            }
        }while(!enoughSpace);

        System.out.println("Please answer the following questions with yes/no or y/n or leave empty");
        boolean pool = AppUtils.trueOrFalse("Is it important that your hotel has a Pool?");
        boolean entertainment = AppUtils.trueOrFalse("Is it important that your hotel has Entertainment?");
        boolean kidsClub = AppUtils.trueOrFalse("Is it important that your hotel has a Children club?");
        boolean restaurant = AppUtils.trueOrFalse("Is it important that your hotel has a Restaurant?");
        String chkInDate = AppUtils.userInput("which day you want to check in");
        String chkOutDate = AppUtils.userInput("which day you want to check out");
        String orderBy = AppUtils.userInput("in which way you want to order the result (name, beds, price, distance to beach, distance to down town, review)");


        calculateDays(chkInDate, chkOutDate);
//        if((chkInDate.charAt(6) == '6' && chkOutDate.charAt(6) == '6' || chkInDate.charAt(6) == '7' && chkOutDate.charAt(6) == '7' )){
//            chkInDateAsInt = Integer.parseInt(chkInDate.replaceAll("-",""));
//            chkOutDateAsInt = Integer.parseInt(chkOutDate.replaceAll("-",""));
//            sumOfDays = (chkOutDateAsInt - chkInDateAsInt) + 1;
//        }else if(chkInDate.charAt(6) == '6' && chkOutDate.charAt(6) == '7'){
//            sumOfDays = 30- Integer.parseInt(chkInDate.substring(8,10)) + Integer.parseInt(chkOutDate.substring(8,10));
//        }

        int bookingId = ds.createBooking(currentParty.get(0).getId(),chkInDate,chkOutDate);

        ArrayList<AvailableRoom> availableRooms = ds.availableRooms(chkInDate,chkOutDate,orderBy,pool,entertainment,kidsClub,restaurant);
        int count = 1;
        for (AvailableRoom room : availableRooms){
            System.out.println(count + " " + room);
            count++;
        }
        int[] roomNumber = new int[rooms];


        for(int i = 0 ; i < rooms; i++){
            System.out.println("Please pick which room you are interested in");
            roomNumber[i] = AppUtils.userInput(1,availableRooms.size())-1;
            System.out.println("You picked " + availableRooms.get( roomNumber[i] ) );
            extraBed = AppUtils.trueOrFalse("Do you want an extra bed in your room?");
            fullBoard = AppUtils.trueOrFalse("Do you want full board?");
            if(!fullBoard){
                halfBoard = AppUtils.trueOrFalse("Do you want half board?");
            }else {
                halfBoard = false;
            }
            System.out.println("Please pick the persons staying in the room");


            for (Customer existingGuests : currentParty) {
                System.out.println(counter + ". " + existingGuests);
                counter++;
            }

            int customerId = AppUtils.userInput(1, currentParty.size())-1;
            ds.createParty(currentParty.get(customerId).getId(),bookingId,availableRooms.get( roomNumber[i]).getRoomId());
            totalPrice += ds.getPrice(extraBed,fullBoard,halfBoard,availableRooms.get( roomNumber[i]).getRoomId());
        }




        System.out.println("Your booking is now completed");
        System.out.println("You have chosen to stay between "+ chkInDate +" - " + chkOutDate + " for " + sumOfDays + " days.");
        Thread.sleep(1000);
        System.out.println("You have chosen room(s):" );
        for(int i = 0; i < rooms; i++){
            System.out.println(availableRooms.get( roomNumber[i]));
        }
        Thread.sleep(1000);
        System.out.println("Total cost for all rooms, meals and extra beds is : $" + totalPrice*sumOfDays);
        System.out.println("Returning to main menu");
        for(int i = 0; i < 8 ; i ++){
            System.out.print(".");
            Thread.sleep(1000);
        }

        mainMenu();


    }

    private int calculateDays(String chkInDate, String chkOutDate) {
        int chkInDateAsInt;
        int chkOutDateAsInt;
        int sumOfDays = 0;
        if((chkInDate.charAt(6) == '6' && chkOutDate.charAt(6) == '6' || chkInDate.charAt(6) == '7' && chkOutDate.charAt(6) == '7' )){
            chkInDateAsInt = Integer.parseInt(chkInDate.replaceAll("-",""));
            chkOutDateAsInt = Integer.parseInt(chkOutDate.replaceAll("-",""));
            sumOfDays = (chkOutDateAsInt - chkInDateAsInt) + 1;
        }else if(chkInDate.charAt(6) == '6' && chkOutDate.charAt(6) == '7'){
            sumOfDays = 30- Integer.parseInt(chkInDate.substring(8,10)) + Integer.parseInt(chkOutDate.substring(8,10));
        }
        return sumOfDays;
    }


}
