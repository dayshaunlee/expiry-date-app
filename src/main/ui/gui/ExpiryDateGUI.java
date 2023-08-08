package ui.gui;

//import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterContrastIJTheme;
import model.Calendar;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

// Open/close dialog is referenced from DialogDemo
// from https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html

// Main window, also handles opening/closing events
public class ExpiryDateGUI {
    private static final String JSON_STORE = "./data/food.json";
    private static final String CLOSE_MESSAGE = "Do you want to save before exiting?";
    private static final String CLOSE_TITLE = "Save data";
    private static final String OPEN_MESSAGE = "Do you want to load saved data?";
    private static final String OPEN_TITLE = "Load data";

    private static Calendar calendar;
    private static final JsonWriter jsonWriter = new JsonWriter(JSON_STORE);
    private static final JsonReader jsonReader = new JsonReader(JSON_STORE);

    // MODIFIES: this
    // EFFECTS: loads calendar from file
    private static void loadCalendar() {
        try {
            calendar = jsonReader.read();
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // MODIFIES: this
    // EFFECTS: saves the calendar to file
    private static void saveCalendar() {
        try {
            jsonWriter.open();
            jsonWriter.write(calendar);
            jsonWriter.close();
            // isSaved = true;
        } catch (FileNotFoundException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // MODIFIES: this
    // EFFECTS: creates dialog popup boxes when opening/closing the app
    private static void openClose(JFrame frame, Boolean onClose) {
        String message;
        String title;

        if (onClose) {
            message = CLOSE_MESSAGE;
            title = CLOSE_TITLE;
        } else {
            message = OPEN_MESSAGE;
            title = OPEN_TITLE;
        }

        final JOptionPane optionPane = setupDialogBox(frame, message, title);

        int value = (Integer) optionPane.getValue();
        if (value == JOptionPane.YES_OPTION) {
            handleYesOption(onClose);

        } else if (value == JOptionPane.NO_OPTION) {
            handleNoOption(onClose);
        }
    }

    // EFFECTS: handles action when user selects no option
    private static void handleNoOption(Boolean onClose) {
        if (onClose) {
            System.exit(0);
        }
    }

    // EFFECTS: handles action when user selects yes option
    private static void handleYesOption(Boolean onClose) {
        if (onClose) {
            saveCalendar();
            System.exit(0);
        }
        loadCalendar();
    }

    // EFFECTS: creates and displays dialog box, and handles related events
    private static JOptionPane setupDialogBox(JFrame frame, String message, String title) {
        final JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);

        //You can't use pane.createDialog() because that
        //method sets up the JDialog with a property change
        //listener that automatically closes the window
        //when a button is clicked.
        final JDialog dialog = new JDialog(frame,
                title,
                true);
        makeWindowUnclosable(optionPane, dialog);
        handleCloseWindow(optionPane, dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        return optionPane;
    }

    // EFFECTS: closes window after action has been selected
    private static void handleCloseWindow(JOptionPane optionPane, JDialog dialog) {
        optionPane.addPropertyChangeListener(
                e -> {
                    String prop = e.getPropertyName();

                    if (dialog.isVisible()
                            && (e.getSource() == optionPane)
                            && (JOptionPane.VALUE_PROPERTY.equals(prop))) {
                        //If you were going to check something
                        //before closing the window, you'd do
                        //it here.
                        dialog.setVisible(false);
                    }
                });
    }

    // EFFECTS: prevents the user from closing the dialog window with the X button
    private static void makeWindowUnclosable(JOptionPane optionPane, JDialog dialog) {
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(
                JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                Toolkit.getDefaultToolkit().beep();
            }
        });
    }

    // EFFECTS: Create the GUI and show it.  For thread safety,
    //          this method should be invoked from the
    //          event-dispatching thread.
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Expiry Date Tracker");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                openClose(frame, true);
            }
        });

        openClose(frame, false);

        //Create and set up the content pane.
        Container c = frame.getContentPane();
        CalendarDisplay calendarDisplay = new CalendarDisplay(calendar);
        c.setLayout(new BoxLayout(c, BoxLayout.X_AXIS));

        //Add elements
        c.add(calendarDisplay);
        c.add(new FoodList(calendar, frame, calendarDisplay));

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

    // MODIFIES: this
    // EFFECTS: runs the app
    public static void main(String[] args) {
        //setup L&F, for some reason Git doesn't recognize the library
//        FlatMaterialLighterContrastIJTheme.setup();

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            calendar = new Calendar();
            createAndShowGUI();
        });
    }
}
