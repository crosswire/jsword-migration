package org.crosswire.common.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * An upper case document simply extends document to make all the text entered
 * upper case according to Character.toUpperCase.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @see java.lang.Character#toUpperCase(char)
 */
public class UpperCaseDocument extends PlainDocument {
    /**
     * Override insertString to force upper case
     */
    @Override
    public void insertString(int offs, String str, AttributeSet att) throws BadLocationException {
        if (str == null)
            return;
        char[] upper = str.toCharArray();

        for (int i = 0; i < upper.length; i++) {
            upper[i] = Character.toUpperCase(upper[i]);
        }

        super.insertString(offs, new String(upper), att);
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 4051324548165284660L;
}
