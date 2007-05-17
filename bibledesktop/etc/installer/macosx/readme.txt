Background:
On the Mac folders that end with app are packages whose content is not shown in the browser.
Apps have a special layout. This is preserved here.

Instructions:

Building the App:
Info.plist is the "app" control file. When 3rd party jar names change, it needs to change.
Use Ant to build the "app" into the installation target directory.
This does two things:
1) It updates the release version number for the BibleDesktop and JSword jars.
2) It puts the jars in BibleDesktop.app/Contents/Resources/Java

Building the Distribution:
DMG is a Mac Disk Image that can only be built on the Mac.
It is the preferred way of distributing Mac applications.
The easiest way to build a new DMG is to use an old one, replacing BibleDesktop.app with a new one.


