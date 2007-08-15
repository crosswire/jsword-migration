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
package org.crosswire.jsword.rcp.prototype.factories;

import org.crosswire.jsword.rcp.prototype.editors.BookDataInput;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * 
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class InputElementFactory implements IElementFactory
{

    public static final String FACTORY_ID = "org.crosswire.jsword.rcp.prototype.inputelementfactory";
    
    public IAdaptable createElement(IMemento memento) {
        IMemento child = memento.getChild("input");
        if (child == null){
            return null;
        }
        
        String type = child.getString("type");
        if (type == null) {
            return null;
        }
        
        if (type.equals(BookDataInput.INPUT_TYPE)) {
            return BookDataInput.createFromState(child);
        }
        
        return null;
    }
}


