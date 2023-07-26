package persistence;

import model.Food;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    protected void checkFood(String name, String expiryDate, String datePurchased, boolean expired, Food food) {
        assertEquals(name, food.getName());
        assertEquals(expiryDate, food.getExpiryDate().format(Food.DATE_FORMAT));
        assertEquals(datePurchased, food.getDatePurchased().format(Food.DATE_FORMAT));
        assertEquals(expired, food.isExpired());
    }
}