
package org.crosswire.jsword.passage;

import java.util.EventListener;

/**
 * A PassageListener gets told when the verses in a Passage
 * have changed (added or removed). 
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
 * @version $Id$
 * @stereotype role
 */
public interface PassageListener extends EventListener
{
    /** 
     * Sent after stuff has been added to the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesAdded(PassageEvent ev);
    
    /**
     * Sent after stuff has been removed from the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesRemoved(PassageEvent ev);

    /** 
     * Sent after verses have been symultaneously added and removed from the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesChanged(PassageEvent ev);
}

