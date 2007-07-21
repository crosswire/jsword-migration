package org.crosswire.common.util;

/**
 * A generic class of String utils.
 * It would be good if we could put this stuff in java.lang ...
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 */
public final class Validator
{
    /**
     * Ensure that we can not be instantiated
     */
    private Validator()
    {
    }

    /**
     * Check that the given string contains ascii letters (upper and lower) and
     * numbers only. This is useful for checking identifiers for special values
     * @param test The string to check
     * @return true if the string passes the tests specified
     */
    public static boolean isAsciiAlphanumeric(String test)
    {
        for (int i=0; i<test.length(); i++)
        {
            char c = test.charAt(i);

            if (!Character.isLetterOrDigit(c))
                return false;

            if (c >= 127)
                return false;
        }

        return true;
    }

    /**
     * Check that the given string contains letters (upper and lower), space
     * and numbers only. In effect there can be no control characters or
     * punctuation. This ensures that the string can be safely displayed in a
     * web browser without side effects.
     * @param test The string to check
     * @return true if the string passes the tests specified
     */
    public static boolean isSpaceAlphanumeric(String test)
    {
        for (int i=0; i<test.length(); i++)
        {
            char c = test.charAt(i);

            if (!Character.isLetterOrDigit(c) && c != ' ')
                return false;
        }

        return true;
    }

    /**
     * Check that the given string contains letters (upper and lower), and
     * simple punctuation only. In effect there can be no control characters
     * or dangerous punctuation. This ensures that the string can be safely
     * displayed in a web browser without side effects. The allowed
     * punctuation marks are [comma], [apostrophe], [period], [minus],
     * [query], [bang], [quote], [slash], [open paren], [close paren] and
     * [colon]
     * @param test The string to check
     * @return true if the string passes the tests specified
     */
    public static boolean isSimpleAscii(String test)
    {
        for (int i=0; i<test.length(); i++)
        {
            char c = test.charAt(i);

            if (!Character.isLetterOrDigit(c)
                && c != ' ' && c != ','
                && c != '"' && c != '/'
                && c != '?' && c != '!'
                && c != '\'' && c != '.'
                && c != '(' && c != ')'
                && c != '-' && c != ':')
                return false;
        }

        return true;
    }

    /**
     * Check that the given string does not contain control characters (except
     * return, ff or tab) or any triangular brackets or ampersands.
     * i.e. nothing that HTML regards as special.
     * @param test The string to check
     * @return true if the string passes the tests specified
     */
    public static boolean isSafeEmbededHTML(String test)
    {
        for (int i=0; i<test.length(); i++)
        {
            char c = test.charAt(i);

            if (Character.isISOControl(c) &&
                c != '\n' && c != '\r' && c != '\t')
                return false;

            if (c == '&')
                return false;

            if (c == '<')
                return false;

            if (c == '>')
                return false;
        }

        return true;
    }

    /**
     * Check that the given text represents a potentially valid email
     * address. We should reject all of the following: a, a@b, a@b.c.
     * There is probably a lot more we could do to become an rfc822 strict
     * tester, probably mostly in the character set arena. I've seen a regex
     * to do this better, but is is very unmaintainable and it requires a
     * regex library.
     * @param text The string to check for potential validity
     * @return true if the string passes the tests specified
     */
    public static boolean isInternetEmail(String text)
    {
        int lastdot = text.lastIndexOf('.');
        int firstat = text.indexOf('@');

        // There must be something before an @ sign
        if (firstat < 1)
            return false;

        // There must be a dot
        if (lastdot < 0)
            return false;

        // There must be at least 2 chars after the last dot
        if (lastdot >= text.length() - 2)
            return false;

        // There must be at least 2 chars between the at sign
        // and the last dot
        if (firstat >= lastdot - 2)
            return false;

        // Is it simple ascii?
        // We could probably be much more strict here.
        for (int i=0; i<text.length(); i++)
        {
            char c = text.charAt(i);
            if (c < 33 || c > 126)
                return false;
        }

        return true;
    }

    /**
     * Take a string (for example "£2,000.00") and chop out any of the
     * chars that make it fail to parse (giving 2000.00) All we do is
     * chop out any chars that are not one of "+", "-", "." or a number
     */
    public static String trimToNumberic(String orig)
    {
        StringBuffer buffer = new StringBuffer();

        for (int i=0; i<orig.length(); i++)
        {
            char c = orig.charAt(i);

            if (Character.isDigit(c) || c == '+' || c == '-' || c == '.')
                buffer.append(c);
        }

        return buffer.toString();
    }
}

