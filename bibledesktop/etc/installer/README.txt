This file describes how to build and maintain a windows installer for BibleDesktop.

Currently we are using NSIS (NullSoft Scriptable Install System) to deploy BibleDesktop to Windows.

Prerequsites:
1) A windows computer to build the installer.
2) NSIS installed your Windows computer. You can obtain it here:
    http://nsis.sourceforge.net/Download
3) InetLoad.dll in the NSIS/plugins directory. You can obtain it from here:
    http://nsis.sourceforge.net/mediawiki/images/b/b4/InetLoad.zip
*) Optionally, install EclipseNSIS plugin. You can obtain it here:
    http://eclipsensis.sourceforge.net
   This plugin allows for wizard creation,
                      for smart editing,
                      for simple compiling
   of NSIS scripts.

In this directory there are two scripts. One for running the application and the other for deploying the application.

The application script, BibleDesktop.nsi, will create BibleDesktop.exe.
The program when run will check to see if Java is installed.
If it is not then it will offer to automatically download and install it.
This script will need to be updated from time to time to get the current version of Java.
Once there is a suitable Java on the computer, it will run BibleDesktop.

The deployment script will create setup.exe.

The typical pattern of events to build the whole shebang is:
1) Build all the jars using ant.
2) Compile the application script into BibleDesktop.exe
   This only needs to be done when BibleDesktop.nsi changes
3) Compile the setup script into setup.exe.
   This needs to be done each time the jars change or Setup.nsi changes.
4) Copy the setup.exe to a suitable place on crosswire.org.

At this point and time, these steps are manual. While it would be good to automate it,
we don't release often enough to make this a big deal. And we may change how we do
things before the next release.