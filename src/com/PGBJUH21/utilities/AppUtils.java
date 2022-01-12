package com.PGBJUH21.utilities;

import java.util.Scanner;

/**
 *
 * Help class
 *
 */
public class AppUtils {


    /**
     * Help method to handle user input with ints
     * @param minValue min value of user input
     * @param maxValue max value of user input
     * @return user input as int
     */
    public static int userInput(int minValue, int maxValue){
        Scanner console = new Scanner(System.in);
        int menuChoice;
        do{
            try{
                menuChoice = Integer.parseInt(console.nextLine());
            }catch(Exception e){
                menuChoice = -1;
            }
        }while(menuChoice < minValue || menuChoice > maxValue || menuChoice == -1);
        return menuChoice;

    }

    /**
     * Help method to handle user input with strings
     * @param title Please enter ... "your desired information"
     * @return input from user
     */
    public static String userInput(String title){
        if (title.contains("?")){
            System.out.println(title);
        }else{
            System.out.println("Please enter " + title);
        }
        Scanner console = new Scanner(System.in);
        String input;
        input = console.nextLine();

        return input;
    }

    /**
     * Help method for easy building menus with same layout
     * @param menuName name of the menu
     * @param minValue min value of menu choices
     * @param maxValue max value of menu choices
     * @param options all menu options
     * @return int as the option the user has chosen
     */
    public static int menuBuilder(String menuName, int minValue, int maxValue, String ...options){
        System.out.println(menuName);
        int i = 1;
        for(String menuOptions : options){
            System.out.println(i + ". " + menuOptions);
            i++;
        }

        return AppUtils.userInput(minValue,maxValue);

    }

    /**
     * Help method to handle user input with booleans
     *
     * @param question question for the user to answer
     * @return boolean with answer
     */
    public static boolean trueOrFalse(String question){
        boolean bol;
        String input = AppUtils.userInput(question);
        if(input.equalsIgnoreCase( "yes" ) || input.equalsIgnoreCase("y")){
            bol = true;
        }else{
            bol = false;
        }
        return bol;
    }

    /**
     * Help method to clean the console window
     *
     */
    public static void clearScreen(){
        System.out.println("\n".repeat(50));
    }

}
