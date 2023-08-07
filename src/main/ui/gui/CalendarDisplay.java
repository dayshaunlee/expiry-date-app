package ui.gui;

import model.Calendar;
import model.Food;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// A visual representation of calendar data
// Adapted from https://stackoverflow.com/questions/22437966/change-color-of-jpanels-after-their-creation
public class CalendarDisplay extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int ROWS = 5; // amount of weeks in a month
    private static final int COLS = 7; // amount of days in a week

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private static final Color NORMAL_COLOR = new Color(230, 230, 230);

    private static final Color TODAY_COLOR = new Color(92, 231, 255);
    private static final LocalDate NOW = LocalDate.now();

    private final int offset;
    // days in the month that have food that expires on that day AND is after today
    private final List<Integer> expiresSoon = new ArrayList<>();

    // MODIFIES: this
    // EFFECTS: creates a CalendarDisplay with rows and columns matching that of a calendar
    public CalendarDisplay(model.Calendar calendar) {
        super(new GridLayout(ROWS, COLS, 2, 2));

        setBorder(BorderFactory.createTitledBorder(NOW.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)));
        offset = NOW.withDayOfMonth(1).getDayOfWeek().getValue();

        getDatesExpiresSoon(calendar.getFoodList());
        createPanels();
        updateCalendar(calendar);
    }

    // REQUIRES: foods is not empty
    // MODIFIES: this
    // EFFECTS: returns the days in the current month with food expiring on that day
    private void getDatesExpiresSoon(List<Food> foods) {
        expiresSoon.clear();
        for (Food f : foods) {
            LocalDate expiryDate = f.getExpiryDate();
            if (expiryDate.getMonth().equals(NOW.getMonth()) && expiryDate.isAfter(NOW)) {
                expiresSoon.add(f.getExpiryDate().getDayOfMonth());
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: creates/updates the current calendar display, including colours on food items marking expiry status
    public void updateCalendar(Calendar calendar) {
        getDatesExpiresSoon(calendar.getFoodList());
        for (int p = offset; p < NOW.lengthOfMonth() + offset; p++) {
            int currentDay = p - offset + 1;
            int todayDay = NOW.getDayOfMonth();
            float expiresHowSoonScale = (currentDay - todayDay) / 30F * (75F / 360);
            JPanel currentPanel = (JPanel) getComponent(p);
            currentPanel.removeAll();
            if (todayDay == currentDay) {
                currentPanel.setBackground(TODAY_COLOR);
            } else if (expiresSoon.contains(currentDay)) {
                currentPanel.setBackground(new Color(Color.HSBtoRGB(expiresHowSoonScale, 0.75F, 1F)));
            } else {
                currentPanel.setBackground(NORMAL_COLOR);
            }
            currentPanel.add(new JLabel(Integer.toString(currentDay)));
            currentPanel.revalidate();
            currentPanel.repaint();
        }
    }

    // MODIFIES: this
    // EFFECTS: creates panels for calendar
    private void createPanels() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JPanel p2 = new JPanel();
                p2.setBackground(Color.white);
                add(p2);
            }
        }
    }

    // EFFECTS: always returns a Dimension of set size,
    //          effectively sets size of CalendarDisplay
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
}

