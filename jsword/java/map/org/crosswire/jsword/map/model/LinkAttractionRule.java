
package org.crosswire.jsword.map.model;

/**
* LinkAttractionRule.
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
* @version D0.I0.T0
*/
public class LinkAttractionRule extends AbstractRule
{
    /**
    * Basic constructor
    */
    public LinkAttractionRule(LinkArray la)
    {
        this.la = la;
    }

    /**
    * Specify where it would like a node to be positioned in space.
    * Rules return an array of positions where the average of them
    * specifies the real desired position. So to specify a single place
    * simply return an array of one position. The positions are added
    * to the results from all Rules so to specify a single position
    * more strongly, return an array conataining that position many
    * times.
    * @param map The Map to select a node from
    * @param ord The ordinal number (1 - 31104) of the verse
    * @return An array of desired positions.
    */
    public Position[] getDesiredPosition(Map map, int ord)
    {
        if (scale == 0)
            return new Position[] { };

        int scale_copy = scale;
        Position[] reply = new Position[scale_copy];
        int reply_index = 0;

        int dim = map.getDimensions();

        // For all the links for this verse
        Link[] links = la.getLinks(ord);
        float factor = BEST_MATCH / links[0].getStrength();
        for (int i=0; i<links.length; i++)
        {
            // How many times do we include this link position
            int reps = (int) (factor * links[i].getStrength());
            int dest_ord = links[i].getDestinationOrdinal();
            Position p = new Position(map.getPosition(dest_ord));

            // This can take some time
            Thread.currentThread().yield();

            // And it however many times;
            for (int j=0; j<reps; j++)
            {
                reply[reply_index++] = p;

                if (reply_index >= scale_copy)
                    return reply;
            }
        }

        // Blank out the rest
        Position me = new Position(map.getPosition(ord));
        for (int i=reply_index; i<scale_copy; i++)
        {
            reply[i] = me;
        }

        return reply;
    }

    /** The number of positions given to the best matching link */
    private static final float BEST_MATCH = 3.0F;

    /** The number of links we remember for a node */
    private LinkArray la;
}
