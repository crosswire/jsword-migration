
package org.crosswire.jsword.control.search;

import org.crosswire.jsword.passage.Passage;

/**
* Alter the Passage by calling retainAll with a
* Passage grabbed from the next word in the search string.
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
public class RetainCommandWord implements CommandWord
{
    /**
    * Alter the Passage by calling retainAll with a
    * Passage grabbed from the next word in the search string
    * @param engine The controller that can provide access to the search
    *               string or a default Bible.
    * @param ref The Passage to alter (if necessary)
    */
    public void updatePassage(Engine engine, Passage ref) throws SearchException
    {
        if (!engine.elements().hasMoreElements())
            throw new SearchException("search_retain_blank");

        ParamWord param = (ParamWord) engine.elements().nextElement();
        ref.retainAll(param.getPassage(engine));
    }
}
