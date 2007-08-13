This is a "reprint" of a post to jsword-devel on 12/31/2006. As such it needs to be updated for 3.3.
====================================================================================================
Here are steps you can follow to set up your environment to run the incubator projects. I'm assuming you're developing in a Win32 environment. If not, you'll need to download the appropriate RCP SDK for your OS in step 2.

1) Designate a folder on your hard drive as your RCP "Target Platform" folder, for example, "D:\RCP\Target_3.1.2". This folder will contain the plugins & bundles that you want your RCP project to reference, and that will be bundled with the RCP application when it's built.

2) Download the RCP SDK for Eclipse 3.1.2 (http://archive.eclipse.org/eclipse/downloads/drops/R-3.1.2-200601181600/download.php?dropFile=eclipse-RCP-3.1.2-win32.zip) and extract its contents into your folder. This should create an "eclipse" folder with a few subfolders and files in it.

3) Add the bundles from incubator/Vendor into your RCP folder's "eclipse\plugins" subfolder.

4) Create a new Eclipse workspace for your RCP project.

5) Configure your workspace to look for plugins and bundles in your RCP folder.
    a) Go to Window->Preferences->Plug-in Development->Target Platform.
    b) Browse to or type in the path to your RCP folder's "eclipse" subfolder in the Location field.
    c) Click the Reload button and verify that plugins load up.
    d) Click OK.

6) Import the incubator projects into your workspace

7) Run the test RCP project
    a) Go to Run -> Run... .
    b) Select "Eclipse Application", click New.
    c) Verify that the "Run an application" field contains "org.crosswire.jsword.test.rcp.application".
    d) Click Run.


That's it! You can find more detailed information about setting up an RCP environment here:

http://wiki.eclipse.org/index.php/RCP_FAQ#What_is_the_recommended_target_platform_setup.3F__Or:_How_can_I_build_and_run_my_RCP_app_against_a_different_version_of_the_Eclipse_base.3F
