/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ui.gui;

import model.Calendar;
import model.Food;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

// Represents a GUI of an expiry date app, referenced from ListDemo and TextInputDemo
// from https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html
/* ListDemo.java requires no other files. */
public class FoodList extends JPanel
        implements ListSelectionListener, ActionListener,
        FocusListener {
    private final JList<Food> list;
    private final DefaultListModel<Food> listModel;

    private static final int GAP = 10;
    private static final String addString = "Add";
    private static final String removeString = "Remove";
    private static final String filterString = "Filter";
    private JButton removeButton;

    JTextField foodNameField;
    JFormattedTextField foodExpiryDateField;
    JFormattedTextField foodDatePurchasedField;

    private final Calendar calendar;
    private final JFrame frame;
    private final CalendarDisplay calDisplay;

    // MODIFIES: this
    // EFFECTS: creates GUI of food list
    public FoodList(Calendar calendar, JFrame frame, CalendarDisplay calDisplay) {
        super(new BorderLayout());
        this.calendar = calendar;
        this.frame = frame;
        this.calDisplay = calDisplay;

        listModel = new DefaultListModel<>();
        addFoodsToList(calendar.getFoodList());

        //Create the list and put it in a scroll pane.
        list = new JList<>(listModel);
        JScrollPane listScrollPane = initList();

        JPanel leftHalf = new JPanel() {
            //Don't allow us to stretch vertically.
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE,
                        pref.height);
            }
        };
        leftHalf.setLayout(new BoxLayout(leftHalf,
                BoxLayout.PAGE_AXIS));
        leftHalf.add(createEntryFields());
        leftHalf.add(createButtons());

        add(listScrollPane, BorderLayout.WEST);
        add(leftHalf);
    }

    // MODIFIES: this
    // EFFECTS: initializes the display list
    private JScrollPane initList() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(10);
        return new JScrollPane(list);
    }

    // MODIFIES: this
    // EFFECTS: adds foods to list
    private void addFoodsToList(List<Food> foods) {
        for (Food f : foods) {
            listModel.addElement(f);
        }
        calDisplay.updateCalendar(calendar);
    }

    // MODIFIES: this
    // EFFECTS: creates buttons and adds them
    protected JComponent createButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        removeButton = new JButton(removeString);
        removeButton.setActionCommand(removeString);
        removeButton.addActionListener(new RemoveListener());

        JButton filterButton = new JButton(filterString);
        filterButton.setActionCommand(filterString);
        filterButton.addActionListener(new FilterListener());

        JButton addButton = new JButton(addString);
        AddListener addListener = new AddListener(addButton);
        addButton.setActionCommand(addString);
        addButton.addActionListener(addListener);

        foodNameField.getDocument().addDocumentListener(addListener);
        foodExpiryDateField.getDocument().addDocumentListener(addListener);
        foodDatePurchasedField.getDocument().addDocumentListener(addListener);

        addButton.setEnabled(false);
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(filterButton);

        //Match the SpringLayout's gap, subtracting 5 to make
        //up for the default gap FlowLayout provides.
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0,
                GAP - 5, GAP - 5));
        return panel;
    }

    // MODIFIES: this
    // EFFECTS: required by ActionListener
    public void actionPerformed(ActionEvent e) {
        // doesn't get called
    }

    //EFFECTS: A convenience method for creating a MaskFormatter.
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    // EFFECTS: Called when one of the fields gets the focus so that we can select the focused field.
    public void focusGained(FocusEvent e) {
        Component c = e.getComponent();
        if (c instanceof JFormattedTextField) {
            selectItLater(c);
        } else if (c instanceof JTextField) {
            ((JTextField) c).selectAll();
        }
    }

    //EFFECTS: Workaround for formatted text field focus side effects.
    protected void selectItLater(Component c) {
        if (c instanceof JFormattedTextField) {
            final JFormattedTextField ftf = (JFormattedTextField) c;
            SwingUtilities.invokeLater(ftf::selectAll);
        }
    }

    // EFFECTS: Needed for FocusListener interface.
    public void focusLost(FocusEvent e) {
    } //ignore

    // EFFECTS: returns new panel with entry fields
    protected JComponent createEntryFields() {
        JPanel panel = new JPanel(new SpringLayout());

        String[] labelStrings = {
                "Food name: ",
                "Expiry date: ",
                "Date purchased: "
        };

        JLabel[] labels = new JLabel[labelStrings.length];
        JComponent[] fields = new JComponent[labelStrings.length];
        int fieldNum = 0;

        createTextFields(fields, fieldNum);

        setupLabelFields(panel, labelStrings, labels, fields);
        SpringUtilities.makeCompactGrid(panel,
                labelStrings.length, 2,
                GAP, GAP, //init x,y
                GAP, GAP / 2);//xpad, ypad
        return panel;
    }

    // EFFECTS: Associate label/field pairs, add everything,
    //          and lay it out.
    private void setupLabelFields(JPanel panel, String[] labelStrings, JLabel[] labels, JComponent[] fields) {
        for (int i = 0; i < labelStrings.length; i++) {
            labels[i] = new JLabel(labelStrings[i],
                    JLabel.TRAILING);
            labels[i].setLabelFor(fields[i]);
            panel.add(labels[i]);
            panel.add(fields[i]);

            addFieldListener(fields, i);
        }
    }

    // EFFECTS: Add listeners to each field.
    private void addFieldListener(JComponent[] fields, int i) {
        JTextField tf;
        tf = (JTextField) fields[i];
        tf.addActionListener(this);
        tf.addFocusListener(this);
    }

    // MODIFIES: this
    // EFFECTS: creates text fields
    private void createTextFields(JComponent[] fields, int fieldNum) {
        //Create the text field and set it up.
        foodNameField = new JTextField();
        foodNameField.setColumns(20);
        fields[fieldNum++] = foodNameField;

        foodExpiryDateField = new JFormattedTextField(
                createFormatter("####/##/##"));
        fields[fieldNum++] = foodExpiryDateField;

        foodDatePurchasedField = new JFormattedTextField(
                createFormatter("####/##/##"));
        fields[fieldNum++] = foodDatePurchasedField;
    }

    // handles when an item is removed
    class RemoveListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: removes item when remove button pressed
        @Override
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            Food selected = list.getSelectedValue();
            listModel.remove(index);
            calendar.removeFood(selected.getName());
            calDisplay.updateCalendar(calendar);

            int size = listModel.getSize();

            if (size == 0) { //Nobody's left, disable firing.
                removeButton.setEnabled(false);

            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }

                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        }
    }

    // handles when items are filtered
    class FilterListener implements ActionListener {
        // MODIFIES: this
        // EFFECTS: filters items when filter button pressed
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter a number to view foods expiring within that amount of days (or leave blank to view all)",
                    "Filter by days",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);

            //If a string was returned, say so.
            if ((s != null) && (s.length() > 0) && s.matches("^[0-9]*$")) {
                listModel.removeAllElements();
                addFoodsToList(calendar.getFoodListExpiresInDays(Integer.parseInt(s)));
                return;
            }

            //If you're here, the return value was null/empty/not a number.
            listModel.removeAllElements();
            addFoodsToList(calendar.getFoodList());
        }
    }

    //This listener is shared by the text field and the add button.
    class AddListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private final JButton button;

        // MODIFIES: this
        // EFFECTS: adds listener
        public AddListener(JButton button) {
            this.button = button;
        }

        //Required by ActionListener.
        // EFFECTS: adds item when add button is pressed
        public void actionPerformed(ActionEvent e) {
            String name = foodNameField.getText();
            String expiryDate = foodExpiryDateField.getText();
            String datePurchased = foodDatePurchasedField.getText();
            Food food = new Food(name, expiryDate, datePurchased);

            //User didn't type in a unique name...
            if (name.equals("") || alreadyInList(name)) {
                Toolkit.getDefaultToolkit().beep();
                foodNameField.requestFocusInWindow();
                foodNameField.selectAll();
                return;
            }

            int index = list.getSelectedIndex(); //get selected index
            if (index == -1) { //no selection, so insert at beginning
                index = 0;
            } else {           //add after the selected item
                index++;
            }


            listModel.addElement(food);
            calendar.addFood(food);
            calDisplay.updateCalendar(calendar);

            //Reset the text field.
            foodNameField.requestFocusInWindow();
            foodNameField.setText("");

            //Select the new item and make it visible.
            list.setSelectedIndex(index);
            list.ensureIndexIsVisible(index);
        }

        //This method tests for string equality. You could certainly
        //get more sophisticated about the algorithm.  For example,
        //you might want to ignore white space and capitalization.

        //EFFECTS: checks if item is already in list
        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }

        //Required by DocumentListener.
        // MODIFIES: this
        // EFFECTS: enables button when text box not empty
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }

        //Required by DocumentListener.
        // MODIFIES: this
        // EFFECTS: disables button when text box empty
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }

        //Required by DocumentListener.
        // MODIFIES: this
        // EFFECTS: enables button when text box changed and isn't empty
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }

        // MODIFIES: this
        // EFFECTS: enables button
        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }

        // MODIFIES: this
        // EFFECTS: disables button when text field is empty
        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }

    //This method is required by ListSelectionListener.
    // MODIFIES: this
    // EFFECTS: deals with disabling and enabling of remove button
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {

            //No selection, disable remove button.
            //Selection, enable the add button.
            removeButton.setEnabled(list.getSelectedIndex() != -1);
        }
    }
}
