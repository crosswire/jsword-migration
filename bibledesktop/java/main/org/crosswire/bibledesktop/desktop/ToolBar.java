package org.crosswire.bibledesktop.desktop;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.crosswire.common.swing.CWAction;

/**
 * This toolbar allows for<ul>
 * <li>showing/hiding labels</li>
 * <li>small/large icons</li>
 * <li>showing/hiding toolbar</li>
 * </ul>
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
 * @see gnu.gpl.Licence
 * @author DM Smith [ dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class ToolBar extends JToolBar
{
    /**
	 * ToolBar constructor.
	 */
	public ToolBar()
	{
	}

	/**
	 * Set the tool tip text for the buttons on the tool bar.
	 * @param show boolean
	 */
	public void showText(boolean show)
	{
		Component c;
		int i = 0;
		while ((c = getComponentAtIndex(i++)) != null)
		{
			if (c instanceof JButton)
			{
				JButton button = (JButton) c;
				if (show)
				{
					Action action = button.getAction();
					button.setText((String)action.getValue(Action.SHORT_DESCRIPTION));
				}
				else
				{
					button.setText(null);
				}
			}
		}
	}

	/**
	 * Sets the size of the tool bar button images.
	 * @param show boolean
	 */
	public void showLargeIcons(boolean show)
	{
		Component c;
		int i = 0;
		while ((c = getComponentAtIndex(i++)) != null)
		{
			if (c instanceof JButton)
			{
				JButton button = (JButton) c;
				Action action = button.getAction();
				if (action instanceof CWAction)
				{
					// Clear the button's computed disabled icon
					// so the button can get it again.
					button.setDisabledIcon(null);
					if (show)
					{
						button.setIcon((Icon) action.getValue(CWAction.LARGE_ICON));
					}
					else
					{
						button.setIcon((Icon) action.getValue(Action.SMALL_ICON));
					}
				}
			}
		}
	}
}
