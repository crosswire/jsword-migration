Building NSIS to run on Linux
sudo yum install texinfo bison flex

change .../binutils*-src/build/config.status to fix makeinfo entry

http://www.libsdl.org/extras/win32/cross/
[dmsmith@localhost x86-mingw32-build.sh-0.0-20061107-1]$ x86-mingw32-build.sh 

 This script will build and install a locally hosted cross-compiler for the
 i386-mingw32 target.  This comprises the following components:--

    binutils    assembler, linker and library management tools; these
                must be built for the i386-mingw32 target, and should be
                installed prior to building GCC itself.

    headers     the header files for the mingw-runtime and the w32api
                libraries; these too, are best installed before GCC
                is built.

    gcc         the GCC compiler itself, complete with all supported
                source language packages.

    w32api      the runtime libraries supporting the Win32 API.

    mingw-runtime
                additional libraries specific to the i386-mingw32 target.

 For a first time installation, you should build all of the above.  On the
 other hand, if you are upgrading an existing installation, you may wish to
 select components individually, to reduce the build time.

 Do you wish to select components individually? (Default: NO)? 

 Before proceeding to build the cross-compiler, the following packages must
 be downloaded to a local package directory of your choice:--

    gcc-core-3.4.5-20060117-1-src.tar.gz
    binutils-2.17.50-20070129-1-src.tar.gz
    mingw-runtime-3.12-src.tar.gz
    w32api-3.9-src.tar.gz

 Each of these packages is obtainable from the MinGW project download page,
 found at https://sourceforge.net/project/showfiles.php?group_id=2435.  I can
 automatically download each of the versions specified above; alternatively
 you may choose to manually download these, or any alternative versions of
 your choice; you are advised to choose the most recent versions published
 under the `Current' package designation.

 Should I select the source packages for building the cross compiler by:--
   1) Using exactly the above versions, downloading if necessary, or...
   2) Selecting from existing versions in your package directory?

 Please enter your preferred option: (Default: 1): 

 The mandatory packages listed above are sufficient to build a `C' language
 compiler only.  You may also require one or more of the following additional
 packages, if you wish to add support for other optional languages:--

    gcc-ada-3.4.5-20060117-1-src.tar.gz      (ada)
    gcc-g++-3.4.5-20060117-1-src.tar.gz      (C++)
    gcc-g77-3.4.5-20060117-1-src.tar.gz      (f77, i.e. FORTRAN-77)
    gcc-java-3.4.5-20060117-1-src.tar.gz     (java)
    gcc-objc-3.4.5-20060117-1-src.tar.gz     (objc, i.e. Objective-C)

 Would you like me to download any of these optional language packages?
 (Default: YES)? 

 Do you require support for language `ada'? (Default: YES) n
 Do you require support for language `c++'? (Default: YES) 
 Do you require support for language `f77'? (Default: YES) n
 Do you require support for language `java'? (Default: YES) n
 Do you require support for language `objc'? (Default: YES) n

 Which directory should I use for the local package repository?
 (If this doesn't exist, it will be created before downloading).
 (Default: /home/dmsmith/packages/mingw-3.4.5)? /home/jsword/packages/mingw-3.4.5

 Please select one of the following mirrors, from which to download any
 required packages, (or select `none' to inhibit downloading):--

  0) none                   1) jaist       (JP)       2) puzzle      (CH)
  3) nchc        (TW)       4) easynews    (US)       5) ovh         (FR)
  6) belnet      (BE)       7) surfnet     (NL)       8) kent        (UK)
  9) heanet      (IE)      10) switch      (CH)      11) mesh        (DE)
 12) optusnet    (AU)      13) ufpr        (BR)      14) umn         (US)
 15) superb-east (US)      16) superb-west (US)

 Enter the index number for your choice: (Default: 0): 15

 The standard binary distribution of MinGW does not offer diagnostic
 messages in any language but US English; if this is not your native
 language, then you may prefer to enable National Language Support.

 Would you like to enable NLS for your cross compiler?
 (Default: NO)? 

 Additionally, the standard binary distribution of the MinGW compiler
 does not take advantage of any shared libraries which may be installed
 on its Win32 host; (this is to avoid problems, should it be installed
 on a host where an expected library is missing).  If you are building
 this cross compiler on the host where it will run, you may prefer to
 take advantage of shared libraries.

 Would you like to use shared libraries available on this host?
 (Default: YES)? NO

 Currently, the standard MinGW compiler uses the setjmp/longjmp model
 for exception handling.  For compatibility, it is recommended that you
 should build your cross compiler to use this same model; however, you
 may, if you wish, use the experimental dwarf-2 model instead.

 Do you wish to retain the standard setjmp/longjmp exception handler?
 (Default: YES)? 

 Where should I install the cross-compiler, and its support tools?
 (This directory will be created, if necessary, to allow the
  installation to be completed).
 (Default: /usr/local/cross-tools)? /home/jsword/cross-tools

 Which directory should I use to create the build tree?
 (This is required during the build process; it should be a directory
  which will be used exclusively for building the cross-compiler, and
  will be created, if necessary; it may be optionally removed after
  successful completion of the build and installation process).
 (Default: /home/dmsmith/tmp/mingw-3.4.5)? /home/jsword/tmp/mingw-3.4.5

 You may choose whether you would like me to leave a clean slate,
 after I have successfully completed the build and installation, or
 if you would like to keep the build files for future reference.

 Would you like me to delete all build files, when I'm done?
 (Default: YES)? N

 Interactive setup for i386-mingw32 cross compiler build completed.

 Selected components: binutils headers gcc w32api mingw-runtime
 Selected languages:  c,c++

 Ok to commence building? (Default: YES)? 
