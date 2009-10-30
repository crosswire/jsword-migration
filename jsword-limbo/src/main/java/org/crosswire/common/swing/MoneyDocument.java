package org.crosswire.common.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A money document simply extends document to refuse all non-financial data
 * entered. We do not currently do any decimal place checking.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 */
public class MoneyDocument extends PlainDocument {
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.text.Document#insertString(int, java.lang.String,
     * javax.swing.text.AttributeSet)
     */
    /* @Override */
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null)
            return;

        String current = getText(0, getLength());
        boolean has_dot = current.indexOf('.') == -1 ? false : true;

        char[] addition = str.toCharArray();
        StringBuffer clear = new StringBuffer();

        for (int i = 0; i < addition.length; i++) {
            if (Character.isDigit(addition[i]))
                clear.append(addition[i]);
            if (addition[i] == '.' && !has_dot)
                clear.append(addition[i]);
        }

        super.insertString(offs, clear.toString(), a);

        /*
         * TODO(joe): Some other time String after = getText(0, getLength());
         * int dot_pos = after.indexOf('.');
         * 
         * if (dot_pos != -1) { // Ensure there are 2 digits after the . after =
         * after + "00"; after = after.substring(0, dot_pos + 2);
         * 
         * // Ensure there is something before it. if (dot_pos == 0) after = "0"
         * + after; }
         */
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256722887951071028L;
}
