package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FoodTest {
    Food testFood;

    @BeforeEach
    void runBefore() {
        testFood = new Food("pasta", "2050/12/31", "2023/07/18");
    }

    @Test
    void testConstructor() {
        assertEquals("pasta", testFood.getName());
        assertEquals(LocalDate.parse("2050/12/31", Food.DATE_FORMAT), testFood.getExpiryDate());
        assertEquals(LocalDate.parse("2023/07/18", Food.DATE_FORMAT), testFood.getDatePurchased());
        assertFalse(testFood.isExpired());
    }

    @Test
    void testSetExpiryDateNotExpired() {
        assertEquals(LocalDate.parse("2050/12/31", Food.DATE_FORMAT), testFood.getExpiryDate());
        testFood.setExpiryDate("2051/01/01");
        assertEquals(LocalDate.parse("2051/01/01", Food.DATE_FORMAT), testFood.getExpiryDate());
        assertFalse(testFood.isExpired());
    }

    @Test
    void testSetExpiryDateExpired() {
        assertFalse(testFood.isExpired());
        assertEquals(LocalDate.parse("2050/12/31", Food.DATE_FORMAT), testFood.getExpiryDate());
        testFood.setExpiryDate("2004/02/26");
        assertEquals(LocalDate.parse("2004/02/26", Food.DATE_FORMAT), testFood.getExpiryDate());
        assertTrue(testFood.isExpired());
        testFood.setExpiryDate(Food.TODAY.format(Food.DATE_FORMAT));
        assertTrue(testFood.isExpired());
    }

    @Test
    void testDaysUntilExpired() {
        String notExpiredDate = Food.TODAY.plusDays(7).format(Food.DATE_FORMAT);
        testFood.setExpiryDate(notExpiredDate);
        assertEquals(7, testFood.daysUntilExpired());
    }

    @Test
    void testDaysUntilExpiredExpiresTomorrow() {
        String notExpiredDate = Food.TODAY.plusDays(1).format(Food.DATE_FORMAT);
        testFood.setExpiryDate(notExpiredDate);
        assertEquals(1, testFood.daysUntilExpired());
    }

    @Test
    void testIsExpiredNotExpired() {
        assertFalse(testFood.isExpired());
    }

    @Test
    void testIsExpiredAlreadyExpired() {
        testFood.setExpiryDate("2000/01/01");
        assertTrue(testFood.isExpired());
    }

    @Test
    void testGetName() {
        assertEquals("pasta", testFood.getName());
    }

    @Test
    void testGetExpiryDate() {
        assertEquals(LocalDate.parse("2050/12/31", Food.DATE_FORMAT), testFood.getExpiryDate());
    }

    @Test
    void testGetPurchasedDate() {
        assertEquals(LocalDate.parse("2023/07/18", Food.DATE_FORMAT), testFood.getDatePurchased());
    }

    @Test
    void testToStringNotExpired() {
        assertEquals("pasta: SAFE TO EAT for " + testFood.daysUntilExpired()
                + " days (until 2050/12/31)\n purchased 2023/07/18", testFood.toString());
    }

    @Test
    void testToStringExpired() {
        testFood.setExpiryDate("1979/04/02");
        assertEquals("pasta: EXPIRED " + testFood.daysUntilExpired() * -1
                + " days ago (1979/04/02)\n purchased 2023/07/18", testFood.toString());
    }


}