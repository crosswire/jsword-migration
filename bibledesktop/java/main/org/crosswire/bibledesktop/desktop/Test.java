package org.crosswire.bibledesktop.desktop;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
/**
 * .
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
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Test extends JPanel
{

	private JSplitPane jSplitPane = null;
	private JPanel jPanel = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel jPanel1 = null;
	private JPanel jPanel2 = null;
	private JScrollPane jScrollPane = null;
	private JList jList = null;
	private JPanel jPanel3 = null;
	private JPanel jPanel4 = null;
	private JPanel jPanel5 = null;
	/**
	 * This is the default constructor
	 */
	public Test() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 */
	private  void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(525, 397);
		this.add(getJSplitPane(), java.awt.BorderLayout.CENTER);
		this.add(getJPanel3(), java.awt.BorderLayout.EAST);
		this.add(getJPanel4(), java.awt.BorderLayout.WEST);
		this.add(getJPanel5(), java.awt.BorderLayout.SOUTH);
		this.add(getJPanel(), java.awt.BorderLayout.NORTH);
	}
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */    
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
            jSplitPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
            jSplitPane.setOpaque(false);
			jSplitPane.setLeftComponent(getJTabbedPane());
			jSplitPane.setRightComponent(getJScrollPane());
			jSplitPane.setDividerSize(8);
			jSplitPane.setDividerLocation(300);
			jSplitPane.setContinuousLayout(true);
			jSplitPane.setBackground(new java.awt.Color(204,180,134));
		}
		return jSplitPane;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
		}
		return jPanel;
	}
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */    
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setBorder(null);
			jTabbedPane.addTab("tab1", null, getJPanel1(), null); //$NON-NLS-1$
			jTabbedPane.addTab("tab2", null, getJPanel2(), null); //$NON-NLS-1$
		}
		return jTabbedPane;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
		}
		return jPanel1;
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
		}
		return jPanel2;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
		}
		return jList;
	}
	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
		}
		return jPanel3;
	}
	/**
	 * This method initializes jPanel4	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel();
		}
		return jPanel4;
	}
	/**
	 * This method initializes jPanel5	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel5() {
		if (jPanel5 == null) {
			jPanel5 = new JPanel();
			jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
		}
		return jPanel5;
	}
          }  //  @jve:decl-index=0:visual-constraint="10,10"
