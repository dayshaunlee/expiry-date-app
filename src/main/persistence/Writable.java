package persistence;

import org.json.JSONObject;

// This class is modeled after JSONReader in the JsonSerializationDemo provided by
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence/Writable.java
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}