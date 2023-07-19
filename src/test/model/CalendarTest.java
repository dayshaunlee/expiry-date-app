package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarTest {
    Calendar calendar;
    Food A = new Food("A", "2000/01/01","2005/05/05");
    Food B = new Food("B", "2030/02/03","2025/02/05");
    Food C = new Food("C", "2050/10/15","2050/01/25");

    @BeforeEach
    void runBefore() {
        calendar = new Calendar("Bob's kitchen");
    }

    @Test
    void testConstructor() {
        assertTrue(calendar.getFoodList().isEmpty());
        assertEquals("Bob's kitchen", calendar.getName());
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

    @Test
    void testRemoveFoodOne() {
        List <Food> foods= calendar.getFoodList();
        calendar.addFood(A);
        calendar.removeFood(A);
        assertTrue(foods.isEmpty());
    }

    @Test
    void testRemoveFoodMultiple() {
        List <Food> foods= calendar.getFoodList();
        calendar.addFood(A);
        calendar.removeFood(B);
        calendar.addFood(C);
        calendar.removeFood(B);
        assertEquals(2, foods.size());
        assertFalse(foods.contains(B));
    }

    @Test
    void testGetFoodListExpiresInDaysEmpty() {
        assertTrue(calendar.getFoodListExpiresInDays(10).isEmpty());
    }

    @Test
    void testGetFoodListExpiresInDaysNoneExpiring() {
        calendar.addFood(B);
        calendar.addFood(C);
        assertTrue(calendar.getFoodListExpiresInDays(15).isEmpty());
    }

    @Test
    void testGetFoodListExpiresInDaysOneExpiring() {
        calendar.addFood(A);
        calendar.addFood(B);
        calendar.addFood(C);
        List<Food> result = calendar.getFoodListExpiresInDays(4000);
        assertTrue(result.contains(B));
        assertEquals(1, result.size());
    }

    @Test
    void testGetFoodListExpiresInDaysMultiple() {
        calendar.addFood(A);
        calendar.addFood(B);
        calendar.addFood(B);
        calendar.addFood(C);
        List<Food> result = calendar.getFoodListExpiresInDays(15000);
        assertTrue(result.contains(B));
        assertTrue(result.contains(C));
        assertFalse(result.contains(A));
        assertEquals(3, result.size());
    }

    @Test
    void testSearchCheckStatusEmpty() {
        assertFalse(calendar.searchCheckStatus("C"));
    }

    @Test
    void testSearchCheckStatusFoundExpired() {
        calendar.addFood(A);
        assertTrue(calendar.searchCheckStatus("A"));
    }

    @Test
    void testSearchCheckStatusFoundNotExpired() {
        calendar.addFood(A);
        calendar.addFood(B);
        assertFalse(calendar.searchCheckStatus("B"));
    }

    @Test
    void testSearchCheckStatusNotFoundExpired() {
        calendar.addFood(B);
        assertFalse(calendar.searchCheckStatus("A"));
    }

    @Test
    void testSearchCheckStatusNotFoundNotExpired() {
        calendar.addFood(A);
        calendar.addFood(B);
        assertFalse(calendar.searchCheckStatus("C"));
    }

    @Test
    void testReturnSortedListEmpty() {
        assertEquals("Bob's kitchen\nExpired foods:\nNo food products listed"
                + "\nNon-expired foods:\nNo food products listed", calendar.returnSortedList());
    }

    @Test
    void testReturnSortedListNotEmpty() {
        calendar.addFood(A);
        calendar.addFood(B);
        calendar.addFood(C);
        String msg = "Bob's kitchen\n" +
                "Expired foods:\n" +
                "Bob's kitchen\n\t" +
                A +
                "\n\nNon-expired foods:\n" +
                "Bob's kitchen\n\t" +
                B + "\n\t" + C + "\n";
        assertEquals(msg, calendar.returnSortedList());
    }

    @Test
    void testGetFoodList() {
        List<Food> foods = calendar.getFoodList();
        assertEquals(new ArrayList<Food>(), calendar.getFoodList());
        calendar.addFood(A);
        calendar.addFood(B);
        calendar.addFood(C);
        assertEquals(A, foods.get(0));
        assertEquals(B, foods.get(1));
        assertEquals(C, foods.get(2));
    }

    @Test
    void testGetName() {
        assertEquals("Bob's kitchen", calendar.getName());
    }
}
