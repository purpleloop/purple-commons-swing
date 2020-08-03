package purpleloop.commons.swing;

import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

/** Utilities for Swing UI. */
public final class SwingUtils {

    /** Private constructor. */
    private SwingUtils() {
    }

    /**
     * Creates a button.
     * 
     * @param caption button caption
     * @param cmd command associated to the button
     * @param cont container of the button
     * @param list Action listener d'action
     * @param enabled true if the button is active
     * @return the button
     */
    public static JButton createButton(String caption, String cmd, Container cont,
            ActionListener list, boolean enabled) {
        return createButton(caption, cmd, cont, null, list, enabled);
    }

    /**
     * Creates a button.
     * 
     * @param caption button caption
     * @param cmd command associated to the button
     * @param cont container of the button
     * @param list action listener
     * @param enabled true if the button is active
     * @param constraints additional constraints on the container
     * @return the button
     */
    public static JButton createButton(String caption, String cmd, Container cont,
            Object constraints, ActionListener list, boolean enabled) {
        JButton button = new JButton(caption);
        if (list != null) {
            button.addActionListener(list);
        }
        if (cmd != null) {
            button.setActionCommand(cmd);
        }
        button.setEnabled(enabled);
        if (cont != null) {
            cont.add(button, constraints);
        }
        return button;
    }

    /**
     * Creates a button for a given action.
     * 
     * @param action the action linked to the button
     * @param container container
     * @return the button
     */
    public static JButton createButton(Action action, Container container) {
        JButton button = new JButton(action);
        container.add(button);
        return button;
    }

    /**
     * Creates a new label.
     * 
     * @param text text of the label
     * @param cont container of the label
     * @return the label
     */
    public static JLabel addLabel(String text, Container cont) {
        JLabel label = new JLabel(text);
        cont.add(label);
        return label;
    }

    /**
     * Creates a text field with the given constraints.
     * 
     * @param columns columns
     * @param initialText initial text
     * @param cont container of the text field
     * @param constraints additional constraints on the container
     * @return the text field
     */
    public static JTextField addTextField(int columns, String initialText, Container cont,
            Object constraints) {

        JTextField textField = new JTextField(columns);
        textField.setText(initialText);

        if (cont != null) {
            cont.add(textField);
        }
        return textField;
    }

    /**
     * Creates a combo box.
     * 
     * @param items text elements of to list
     * @param cmd command associated to the combo-box
     * @param cont container of the combo-box
     * @param constraints additional constraints on the container
     * @param list action listener
     * @return the resulting combo-box
     */
    public static JComboBox<String> createComboBox(String[] items, String cmd, Container cont,
            ActionListener list, Object constraints) {
        JComboBox<String> combo = new JComboBox<>(items);
        if (cmd != null) {
            combo.setActionCommand(cmd);
        }
        if (list != null) {
            combo.addActionListener(list);
        }
        if (cont != null) {
            cont.add(combo, constraints);
        }
        return combo;
    }

    /**
     * Creates a checkBox.
     * 
     * @param text initial text
     * @param actionName action associated to the checkBbox
     * @param cont container of the checkBox
     * @param aListener action listener
     * @param selected true if the checkbBx is pre-selected, false otherwise
     * @param constraints Constraints on the container
     * @return the checkBox
     */
    public static JCheckBox createCheckBox(String text, String actionName, Container cont,
            ActionListener aListener, boolean selected, Object constraints) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setActionCommand(actionName);
        checkBox.addActionListener(aListener);
        checkBox.setSelected(selected);
        if (cont != null) {
            cont.add(checkBox, constraints);
        }
        return checkBox;
    }

    /**
     * Creates a slider field.
     * 
     * @param owner the container of the slider
     * @param minimumValue the minimal value
     * @param maximumValue the maximal value
     * @param currentValue the current value
     * @param ticksSpacing major tick increment
     * @param paintTicks true to paint ticks
     * @return the resulting slider
     */
    public static JSlider createSlider(Container owner, int minimumValue, int maximumValue,
            int currentValue, int ticksSpacing, boolean paintTicks) {

        JSlider jSlider = new JSlider();
        jSlider.setMinimum(minimumValue);
        jSlider.setMaximum(maximumValue);
        jSlider.setValue(currentValue);
        jSlider.setMajorTickSpacing(ticksSpacing);
        jSlider.setPaintTicks(paintTicks);
        if (owner != null) {
            owner.add(jSlider);
        }
        return jSlider;
    }

    /**
     * Creates a menu item.
     * 
     * @param label text of the menu item
     * @param command Command to execute
     * @param listener action listener
     * @param parentMenu parent menu
     * @return menu item
     */
    public static JMenuItem addMenuItem(String label, String command, ActionListener listener,
            JMenu parentMenu) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.setActionCommand(command);
        parentMenu.add(menuItem);
        menuItem.addActionListener(listener);
        return menuItem;
    }

    /**
     * Creates a checkBox menu item.
     * 
     * @param label text of the menu item
     * @param command Command to execute
     * @param listener action listener
     * @param parentMenu parent menu
     * @param selected true if the menu item is selected, false otherwise
     * @return menu item
     */
    public static JCheckBoxMenuItem addCheckBoxMenuItem(String label, String command,
            ActionListener listener, JMenu parentMenu, boolean selected) {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(label);
        menuItem.setActionCommand(command);
        menuItem.addActionListener(listener);
        menuItem.setSelected(selected);
        parentMenu.add(menuItem);
        return menuItem;
    }

    /**
     * Creates a radio button.
     * 
     * @param label text of the radio-button
     * @param command Command to execute
     * @param listener action listener
     * @param group the radio button group
     * @param owner the container of the button
     * @param selected true if the button is selected
     * @return the radio button
     */
    public static JRadioButton createRadioButton(String label, String command, ButtonGroup group,
            ActionListener listener, Container owner, Boolean selected) {
        JRadioButton rbt = new JRadioButton(label, selected);
        rbt.setActionCommand(command);
        rbt.addActionListener(listener);
        owner.add(rbt);
        group.add(rbt);
        return rbt;
    }

}
