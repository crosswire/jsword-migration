package org.crosswire.common.aqua;

import org.crosswire.common.swing.Actionable;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class OSXAdapter extends ApplicationAdapter {

    private OSXAdapter(Actionable actionable, String aboutAction, String prefAction, String quitAction) {
        this.actionable = actionable;
        this.aboutAction = aboutAction;
        this.prefAction = prefAction;
        this.quitAction = quitAction;
    }

    // The main entry-point for this functionality. This is the only method
    // that needs to be called at runtime, and it can easily be done using
    // reflection (see MyApp.java)
    /**
     * Register the application so that About and Quit on the Application menu
     * are hooked to the applications About and Quit choices.
     */
    public static void registerMacOSXApplication(Actionable actionable, String aboutAction, String prefAction, String quitAction) {
        if (theApplication == null) {
            theApplication = new Application();
        }

        if (theAdapter == null) {
            theAdapter = new OSXAdapter(actionable, aboutAction, prefAction, quitAction);
        }

        theApplication.addApplicationListener(theAdapter);
    }

    /**
     * Enables the Preferences menu item in the application menu.
     */
    public static void enablePrefs(boolean enabled) {
        if (theApplication == null) {
            theApplication = new Application();
        }
        theApplication.setEnabledPreferencesMenu(enabled);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.apple.eawt.ApplicationAdapter#handleAbout(com.apple.eawt.ApplicationEvent
     * )
     */
    public void handleAbout(ApplicationEvent ae) {
        handle(aboutAction, ae, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.apple.eawt.ApplicationAdapter#handlePreferences(com.apple.eawt.
     * ApplicationEvent)
     */
    public void handlePreferences(ApplicationEvent ae) {
        handle(prefAction, ae, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.apple.eawt.ApplicationAdapter#handleQuit(com.apple.eawt.ApplicationEvent
     * )
     */
    public void handleQuit(ApplicationEvent ae) {
        /*
         * You MUST setHandled(false) if you want to delay or cancel the quit.
         * This is important for cross-platform development -- have a universal
         * quit routine that chooses whether or not to quit, so the
         * functionality is identical on all platforms. This example simply
         * cancels the AppleEvent-based quit and defers to that universal
         * method.
         */
        handle(quitAction, ae, false);
    }

    private void handle(String action, ApplicationEvent ae, boolean handledState) {
        if (actionable != null) {
            ae.setHandled(handledState);
            actionable.actionPerformed(action);
            ae.setHandled(handledState);
        } else {
            throw new IllegalStateException("handleQuit: MyApp instance detached from listener"); //$NON-NLS-1$
        }
    }

    /** This adapter is a singleton */
    private static OSXAdapter theAdapter;

    /** The MacOSX notion of the application */
    private static Application theApplication;

    /** The application providing about, preferences and quit. */
    private Actionable actionable;

    /** The application's About action */
    private String aboutAction;

    /** The application's Preferences action */
    private String prefAction;

    /** The application's Quit action */
    private String quitAction;
}
