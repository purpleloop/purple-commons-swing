package io.github.purpleloop.commons.swing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextField;

/**
 * A date simple field with ISO Local date format.
 * 
 * Warning draft version, only supporting ISO_LOCAL_DATE.
 * 
 * See {@link DateTimeFormatter#ISO_LOCAL_DATE}.
 */
public class JDateField extends JTextField {

    /** Serialization tag. */
    private static final long serialVersionUID = 1130478724134085824L;

    /**
     * Sets the date
     * 
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        if (date == null) {
            setText("");
        } else {
            setText(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        String text = getText();
        if ("".equals(text)) {
            return null;
        } else {
            return (LocalDate) DateTimeFormatter.ISO_LOCAL_DATE.parse(text);
        }

    }

}
