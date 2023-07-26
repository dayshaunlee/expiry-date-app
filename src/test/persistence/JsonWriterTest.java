package persistence;

import model.Calendar;
import model.Food;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        try {
            Calendar c = new Calendar();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyCalendar() {
        try {
            Calendar c = new Calendar();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyCalendar.json");
            writer.open();
            writer.write(c);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyCalendar.json");
            c = reader.read();
            assertEquals(0, c.getFoodList().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralCalendar() {
        try {
            Calendar c = new Calendar();
            c.addFood(new Food("milk", "2023/07/01", "2023/06/25"));
            c.addFood(new Food("bread", "2050/08/02", "2050/08/01"));
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralCalendar.json");
            writer.open();
            writer.write(c);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralCalendar.json");
            c = reader.read();
            List<Food> foods = c.getFoodList();
            assertEquals(2, foods.size());
            checkFood("milk", "2023/07/01", "2023/06/25", true, foods.get(0));
            checkFood("bread", "2050/08/02", "2050/08/01", false, foods.get(1));

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}
