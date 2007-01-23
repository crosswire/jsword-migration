/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2006
 *
 */
package org.crosswire.jsword.rcp.prototype.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class OsisElementSelectionProvider implements IOsisElementSelectionProvider
{
    List listeners;
    
    /**
     * 
     */
    public OsisElementSelectionProvider()
    {
        listeners = new ArrayList();
    }
    
    public void dispose()
    {
        listeners.clear();
    }
    
    public void addSelectionListener(IOsisElementSelectionListener listener)
    {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.rcp.prototype.editors.IOsisElementSelectionProvider#removeSelectionListener(org.crosswire.jsword.rcp.prototype.editors.IOsisElementSelectionListener)
     */
    public void removeSelectionListener(IOsisElementSelectionListener listener)
    {
        listeners.remove(listener);
    }

    public void fireSelection(Object source, String elementType, String osisId) {
        if (listeners.isEmpty()) {
            return;
        }
        
        for (Iterator it = listeners.iterator(); it.hasNext();)
        {
            IOsisElementSelectionListener listener = (IOsisElementSelectionListener) it.next();
            listener.elementSelected(new OsisElementSelectionEvent(source, elementType, osisId));
        }
    }
    
}
