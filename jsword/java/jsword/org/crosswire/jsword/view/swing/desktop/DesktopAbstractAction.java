
package org.crosswire.jsword.view.swing.desktop;

import javax.swing.KeyStroke;

import org.crosswire.common.swing.EirAbstractAction;

/**
 * An EirAbstractAction that knows about a Desktop that it is attached to.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class DesktopAbstractAction extends EirAbstractAction
{
    /**
     * Constructor for DesktopAbstractAction.
     */
    public DesktopAbstractAction(Desktop tools, String name, String small_icon, String large_icon, String short_desc, String long_desc, int mnemonic, KeyStroke accel)
    {
        super(name, small_icon, large_icon, short_desc, long_desc, mnemonic, accel);
        this.tools = tools;
    }

    /**
     * Accessor for the Desktop this Action is tied to.
     */
    public Desktop getDesktop()
    {
        return tools;
    }
    
    private Desktop tools;
}
