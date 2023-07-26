package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import model.Calendar;
import model.Food;
import org.json.*;

// This class is modeled after JSONReader in the JsonSerializationDemo provided by
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence/JsonReader.java

// Represents a reader that reads calendar from JSON data stored in file
public class JsonReader {
    private final String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads workroom from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Calendar read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseCalendar(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses calendar from JSON object and returns it
    private model.Calendar parseCalendar(JSONObject jsonObject) {
        model.Calendar c = new model.Calendar();
        addFoods(c, jsonObject);
        return c;
    }

    // MODIFIES: c
    // EFFECTS: parses foods from JSON object and adds them to calendar
    private void addFoods(model.Calendar c, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("foods");
        for (Object json : jsonArray) {
            JSONObject nextFood = (JSONObject) json;
            addFood(c, nextFood);
        }
    }

    // MODIFIES: wr
    // EFFECTS: parses foods from JSON object and adds it to calendar
    private void addFood(model.Calendar c, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        String expiryDate = jsonObject.getString("expiry_date");
        String datePurchased = jsonObject.getString("date_purchased");

        Food food = new Food(name, expiryDate, datePurchased);
        c.addFood(food);
    }
}
