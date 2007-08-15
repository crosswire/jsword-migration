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
package org.crosswire.jsword.rcp.prototype.workbench;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{

    private IWorkbenchAction quitAction;
    private IContributionItem viewsItem;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction copyAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
    {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window)
    {
        this.quitAction = ActionFactory.QUIT.create(window);
        register(quitAction);
        
        this.viewsItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        
        this.aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        
        this.copyAction = ActionFactory.COPY.create(window);
        register(copyAction);
        
    }

    protected void fillMenuBar(IMenuManager menuBar)
    {
        MenuManager fileManager = new MenuManager("&File", "file");
        fileManager.add(quitAction);
        
//        MenuManager editManager = new MenuManager("&Edit", "edit");
//        editManager.add(copyAction);
        
        MenuManager windowManager = new MenuManager("&Window", "window");
        MenuManager showViewManager = new MenuManager("Show &View", "show.view");
        showViewManager.add(viewsItem);
        windowManager.add(showViewManager);
        
        MenuManager helpManager = new MenuManager("&Help", "help");
        helpManager.add(aboutAction);
        
        menuBar.add(fileManager);
//        menuBar.add(editManager);
        menuBar.add(windowManager);
        menuBar.add(helpManager);
    }

}
