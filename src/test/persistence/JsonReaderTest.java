package persistence;

import model.Calendar;
import model.Food;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            Calendar c = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyCalendar() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyCalendar.json");
        try {
            Calendar c = reader.read();
            assertEquals(0, c.getFoodList().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralCalendar() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralCalendar.json");
        try {
            Calendar c = reader.read();
            List<Food> foods = c.getFoodList();
            assertEquals(2, foods.size());
            checkFood("bread", "2050/08/02", "2050/08/01", false, foods.get(1));
            checkFood("milk", "2023/07/01", "2023/06/25", true, foods.get(0));
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}