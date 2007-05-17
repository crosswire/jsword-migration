Background:
On the Mac folders that end with app are packages whose content is not shown in the browser.
Apps have a special layout. This is preserved here.

Instructions:

Building the App:
Info.plist is the "app" control file. When jar names change, it needs to change.
Jars go in BibleDesktop.app/Contents/Resources/Java

Building the Distribution:
DMG is a Mac Disk Image that can only be built on the Mac.
It is the preferred way of distributing Mac applications.
The easiest way to build a new DMG is to use an old one, replacing BibleDesktop.app with a new one.

For a nightly build, one can build the app and zip it up.
