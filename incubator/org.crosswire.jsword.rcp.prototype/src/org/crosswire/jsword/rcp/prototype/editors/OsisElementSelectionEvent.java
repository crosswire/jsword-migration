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

import java.util.EventObject;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class OsisElementSelectionEvent extends EventObject
{
    private String elementType;
    private String osisId;

    public OsisElementSelectionEvent(Object source, String elementType, String osisId)
    {
        //TODO type and value are place-holders until we need something more dynamic.
        super(source);
        this.elementType = elementType;
        this.osisId = osisId;
    }
    
    /**
     * @return Returns the elementType.
     */
    public String getElementType()
    {
        return elementType;
    }

    /**
     * @return Returns the elementValue.
     */
    public String getElementValue()
    {
        return osisId;
    }
    
    
    private static final long serialVersionUID = 4224117105047427255L;

    
}
