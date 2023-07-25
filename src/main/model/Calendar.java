package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents a calendar with a name and a list of food products,
// preserved in the order added, duplicates allowed
public class Calendar implements Writable {
    private final List<Food> foodList;

    // EFFECTS: creates a calendar object with an empty list of food products
    public Calendar() {
        foodList = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a food product to the list
    public void addFood(Food food) {
        foodList.add(food);
    }

    // MODIFIES: this
    // EFFECTS: removes first instance of a food product from the list with the given name
    //          returns true if successfully removed, false if not
    public boolean removeFood(String foodName) {
        for (Food f: foodList) {
            if (f.getName().equals(foodName)) {
                foodList.remove(f);
                return true;
            }
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: empties list of food
    public void clearList() {
        foodList.clear();
    }

    // EFFECTS: returns a list of all the food products that expires in x days or less
    //          and is not already expired
    //          returns empty list if none found
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
        return "Expired foods:\n" + returnInListFormat(expiredFoods)
                + "\nNon-expired foods:\n" + returnInListFormat(freshFoods);
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    // EFFECTS: takes a list of food products and returns them line by line
    private String returnInListFormat(List<Food> foods) {
        StringBuilder output = new StringBuilder();
        if (foods.isEmpty()) {
            return "No food products listed";
        }
        for (Food f: foods) {
            output.append("\t").append(f).append("\n");
        }
        return output.toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("foods", foodsToJson());
        return json;
    }

    // EFFECTS: returns things in this calendar as a JSON array
    private JSONArray foodsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Food f : foodList) {
            jsonArray.put(f.toJson());
        }

        return jsonArray;
    }

}
