package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalendarTest {
    Calendar calendar;
    Food A = new Food("A", "2000/01/01","2005/05/05");
    Food B = new Food("B", "2030/02/03","2025/02/05");
    Food C = new Food("C", "2050/10/15","2050/01/25");

    @BeforeEach
    void runBefore() {
        calendar = new Calendar();
    }

    @Test
    void testConstructor() {
        assertTrue(calendar.getFoodList().isEmpty());
    }

    @Test
    void testAddFoodAddOne() {
        List<Food> foods = calendar.getFoodList();
        assertTrue(foods.isEmpty());
        calendar.addFood(A);
        assertEquals(A, foods.get(0));
        assertEquals(1, foods.size());
    }

    @Test
    void testAddFoodMultiple() {
        List<Food> foods = calendar.getFoodList();
        calendar.addFood(C);
        calendar.addFood(B);
        calendar.addFood(B);
        assertEquals(C, foods.get(0));
        assertEquals(B, foods.get(1));
        assertEquals(B, foods.get(2));
    }
}
