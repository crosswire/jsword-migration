
package org.crosswire.common.config.choices;

import org.crosswire.common.config.*;

/**
* ObjectChoice.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public abstract class ObjectChoice extends AbstractChoice
{
    /**
    * Construct an Object Choice
    */
    public ObjectChoice()
    {
    }

    /**
    * Fetch an object
    */
    public abstract Object getObject();

    /**
    * Set a new object
    */
    public abstract void setObject(Object value);

    /**
    * Generalized read Object from the Properties file
    * @return Found int or the default value
    */
    public String getString()
    {
        return getObject().getClass().getName();
    }

    /**
    * Generalized set Object to the Properties file
    * @param value The value to enter
    */
    public void setString(String value) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        if (!value.equals(getString()))
        {
            Class clazz = Class.forName(value);
            Object temp = clazz.newInstance();
            setObject(temp);
        }
    }
}
