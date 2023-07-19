package model;

import java.util.ArrayList;
import java.util.List;

// Represents a calendar with a name and a list of food products,
// preserved in the order added, duplicates allowed
public class Calendar {
    private String name;
    private List<Food> foodList;

    // EFFECTS: creates a calendar object with a designated name and an empty list of food products
    public Calendar(String name) {
        this.name = name;
        foodList = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a food product to the list
    public void addFood(Food food) {
        foodList.add(food);
    }

    // REQUIRES: food to be removed is in the list
    // MODIFIES: this
    // EFFECTS: removes a food product from the list
    public void removeFood(Food food) {
        foodList.remove(food);
    }

    // EFFECTS: returns a list of all the food products that expires in x days or less
    //          and is not already expired
    public List<Food> getFoodListExpiresInDays(int days) {
        List<Food> expiredFoods = new ArrayList<>();
        for (Food f: foodList) {
            if (f.daysUntilExpired() <= days && !f.isExpired()) {
                expiredFoods.add(f);
            }
        }
        return expiredFoods;
    }

    // EFFECTS: searches for the name of a food product in the list
    //          returns true if food is found and is expired, false otherwise
    //          returns first one in the list if foods share names
    public boolean searchCheckStatus(String foodName) {
        for (Food f: foodList) {
            if (f.getName().equals(foodName)) {
                return f.isExpired();
            }
        }
        return false;
    }

    // EFFECTS: returns items in the list to view sorted by whether they are expired or not
    public String returnSortedList() {
        List<Food> expiredFoods = new ArrayList<>();
        List<Food> freshFoods = new ArrayList<>();

        for (Food f: foodList) {
            if (f.isExpired()) {
                expiredFoods.add(f);
            } else {
                freshFoods.add(f);
            }
        }
        return name + "\nExpired foods:\n" + returnInListFormat(expiredFoods)
                + "\nNon-expired foods:\n" + returnInListFormat(freshFoods);
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public String getName() {
        return name;
    }

    // EFFECTS: takes a list of food products and returns them line by line
    private String returnInListFormat(List<Food> foods) {
        StringBuilder output = new StringBuilder();
        output.append(name + "\n");
        if (foods.isEmpty()) {
            return "No food products listed";
        }
        for (Food f: foods) {
            output.append("\t" + f + "\n");
        }
        return output.toString();
    }

}
