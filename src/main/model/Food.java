package model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;

// Represents a food object with a name, expiry date, and date purchased
// Date format is parsed according to the format of DATE_FORMAT
public class Food {
    private String name;              // name of food
    private LocalDate expiryDate;     // date food expires
    private LocalDate datePurchased;  // date food purchased
    private boolean expired;          // expiry status, true if food expired
                                      // a food is expired on the day of the expiry date

    // LocalDate CONSTANTS
    // formatting for converting string to date
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");  // sets timezone
    public static final LocalDate TODAY = LocalDate.now(TIME_ZONE); // gets current date

    // REQUIRES: datePurchased is before expiryDate
    // EFFECTS: creates a food object with a name, expiry date,
    //          date purchased, and expiry status based on current date
    public Food(String name, String expiryDate, String datePurchased) {
        this.name = name;
        this.expiryDate = LocalDate.parse(expiryDate, DATE_FORMAT);
        this.datePurchased = LocalDate.parse(datePurchased, DATE_FORMAT);
        updateExpiryStatus();
    }

    // MODIFIES: this
    // EFFECTS: sets an expiry date in the format dateFormat and updates expired status
    //          (Note: this method is for usage in tests only)
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = LocalDate.parse(expiryDate, DATE_FORMAT);
        updateExpiryStatus();
    }

    // MODIFIES: this
    // EFFECTS: updates expiry status of food according to current date
    private void updateExpiryStatus() {
        expired = (expiryDate.isBefore(TODAY)) || (expiryDate.isEqual(TODAY));
    }

    // EFFECTS: returns the amount of days until food is expired, negative means food expired x days ago
    public int daysUntilExpired() {
        return (int) DAYS.between(TODAY, expiryDate);
    }

    // EFFECTS: returns the name of a food product with expiry status,
    //          days until expiry (or time since expiry if expired),
    //          and date purchased
    @Override
    public String toString() {
        String formattedExpiryDate = expiryDate.format(DATE_FORMAT);
        String formattedDatePurchased = datePurchased.format(DATE_FORMAT);
        if (expired) {
            return name + ": EXPIRED " + daysUntilExpired() * -1
                    + " days ago (" + formattedExpiryDate
                    + ")\n purchased " + formattedDatePurchased;
        }
        return name + ": SAFE TO EAT for " + daysUntilExpired()
                + " days (until " + formattedExpiryDate
                + ")\n purchased " + formattedDatePurchased;
    }

    // EFFECTS: updates expiry status according to current date and then checks if food is expired
    public boolean isExpired() {
        updateExpiryStatus();
        return expired;
    }

    public String getName() {
        return name;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getDatePurchased() {
        return datePurchased;
    }

}
