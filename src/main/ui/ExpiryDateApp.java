package ui;

import model.Food;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

// Some methods (marked with "*Adapted") are adapted from the TellerApp class in the CPSC 210 TellerApp repository
//      from https://github.students.cs.ubc.ca/CPSC210/TellerApp/blob/main/src/main/ca/ubc/cpsc210/bank/ui/TellerApp.java
// Additionally, the methods used to read/write data to/from JSON files are adapted
//      from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/ui/WorkRoomApp.java

// Application to track expiry dates
public class ExpiryDateApp {
    private static final String JSON_STORE = "./data/food.json";
    private model.Calendar calendar;
    private Scanner input;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private boolean isSaved;

    // MODIFIES: this
    // EFFECTS: runs the app
    public ExpiryDateApp() {
        runApp();
    }

    // *Adapted
    // MODIFIES: this
    // EFFECTS: processes user input
    private void runApp() {
        boolean keepGoing = true;
        String command;

        init();

        while (keepGoing) {
            displayMenu();
            command = input.next().toLowerCase();

            if (command.equals("e")) {
                if (!isSaved) {
                    System.out.println("You have unsaved changes. Are you sure you want to exit? "
                            + "[(y)es/(n)o/(s)ave and exit]");
                    String confirmation = input.next();
                    if (confirmation.equals("y")) {
                        keepGoing = false;
                    }
                    if (confirmation.equals("s")) {
                        saveCalendar();
                        keepGoing = false;
                    }
                } else {
                    keepGoing = false;
                }
            } else {
                processCommand(command);
            }
        }
    }

    // *Adapted
    // MODIFIES: this
    // EFFECTS: processes user command
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void processCommand(String command) {
        switch (command) {
            case "a":
                doAddFood();
                break;
            case "r":
                doRemoveFood();
                break;
            case "c":
                doCheckExpiryDate();
                break;
            case "v":
                doViewListing();
                break;
            case "x":
                doClear();
                break;
            case "f":
                doSearchExpired();
                break;
            case "s":
                saveCalendar();
                break;
            case "l":
                loadCalendar();
                break;
            default:
                System.out.println("Invalid selection.");
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: displays information of a food product that user requests
    //          error message if product not found
    private void doSearchExpired() {
        System.out.println("Name of food to look for:");
        String name = input.next();
        Food food = lookupByName(name);
        if (food == null) {
            errorMessage();
        } else {
            System.out.println(food);
        }
    }

    // MODIFIES: this
    // EFFECTS: empties list of food after confirming from user input
    private void doClear() {
        System.out.println("Are you sure you want to empty the list? This cannot be undone (press \"y\" to continue).");
        String confirmation = input.next();
        if (confirmation.equals("y")) {
            System.out.println("Emptied list");
            isSaved = false;
            calendar.clearList();
        } else {
            System.out.println("No changes made");
        }
    }

    // MODIFIES: this
    // EFFECTS: asks the user if they want to view all or view foods expiring soon
    //          and processes the command
    private void doViewListing() {
        System.out.println("\nSelect from:");
        System.out.println("\ta -> view all");
        System.out.println("\ts -> view foods expiring soon");
        String command = input.next().toLowerCase();
        if (command.equals("a")) {
            doViewAll();
        } else if (command.equals("s")) {
            doViewExpiresSoon();
        } else {
            System.out.println("Invalid command.");
        }
    }

    // MODIFIES: this
    // EFFECTS: displays a list of all food products expiring within given number of days
    private void doViewExpiresSoon() {
        System.out.println("Enter a number to show food products that expire in x days or less:");
        int days = input.nextInt();
        System.out.println(calendar.getFoodListExpiresInDays(days));
    }

    // EFFECTS: displays a list of all food products sorted by expiry status
    private void doViewAll() {
        System.out.println(calendar.returnSortedList());
    }

    // MODIFIES: this
    // EFFECTS: gives the expiry date of a named food,
//              gives an error message if food not found
    private void doCheckExpiryDate() {
        System.out.println("Name of food to check expiry date of:");
        String name = input.next();
        Food food = lookupByName(name);
        if (food == null) {
            errorMessage();
        } else {
            System.out.println("Food expiry date: " + food.getExpiryDate().format(Food.DATE_FORMAT));
        }
    }

    // MODIFIES: this
    // EFFECTS: removes named food from list of calendar,
    //          gives an error message named food not found
    private void doRemoveFood() {
        System.out.println("Name of food to remove:");
        String name = input.next();
        if (calendar.removeFood(name)) {
            System.out.println("Successfully removed " + name);
            isSaved = false;
        } else {
            errorMessage();
        }
    }

    // MODIFIES: this
    // EFFECTS: adds named food to list in calendar
    //          as long as expiry date is after purchase date
    private void doAddFood() {
        System.out.println("Name of food to add:");
        String name = input.next();
        System.out.println("Expiry date (YYYY/MM/dd):");
        String expiryDate = input.next();
        System.out.println("Date purchased (YYYY/MM/dd):");
        String datePurchased = input.next();
        Food food = new Food(name, expiryDate, datePurchased);
        if (!food.getDatePurchased().isBefore(food.getExpiryDate())) {
            System.out.println("Expiry date must be after the date purchased");
        } else {
            calendar.addFood(food);
            System.out.println("Successfully added");
            isSaved = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes objects
    private void init() {
        calendar = new model.Calendar();
        input = new Scanner(System.in);
        input.useDelimiter("\n");
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        isSaved = true;
    }

    // EFFECTS: displays menu of selection options
    private void displayMenu() {
        System.out.println("\nSelect from:");
        System.out.println("\ta -> add food");
        System.out.println("\tr -> remove food");
        System.out.println("\tc -> check expiry date of food");
        System.out.println("\tv -> view listing of food products");
        System.out.println("\tf -> search for food and check if expired");
        System.out.println("\tx -> clears all food products in list");
        System.out.println("\ts -> save calendar data to file");
        System.out.println("\tl -> loads calendar data from file");
        System.out.println("\te -> exit");
    }

    // EFFECTS: takes a name and returns a food object with the corresponding name
    //          returns null if given name does match food in list of calendar
    private Food lookupByName(String name) {
        for (Food f: calendar.getFoodList()) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    // EFFECTS: printed error message if named food is not found in the list
    private void errorMessage() {
        System.out.println("Could not find named food");
    }

    // EFFECTS: saves the calendar to file
    private void saveCalendar() {
        try {
            jsonWriter.open();
            jsonWriter.write(calendar);
            jsonWriter.close();
            System.out.println("Saved calendar to " + JSON_STORE);
            isSaved = true;
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads calendar from file
    private void loadCalendar() {
        try {
            calendar = jsonReader.read();
            System.out.println("Loaded calendar from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }
}
