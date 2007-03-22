#!/bin/sh

# Parts of this file were derived from the ant startup script for unix.
# Copyright (c) 2001-2003 The Apache Software Foundation.

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home   
           fi
           ;;
esac

if [ -z "$JSWORD_HOME" ] ; then
  ## resolve links - $0 may be a link to jsword's home
  PRG="$0"
  progname=`basename "$0"`
  saveddir=`pwd`

  # need this for relative symlinks
  dirname_prg=`dirname "$PRG"`
  cd "$dirname_prg"
  
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '.*/.*' > /dev/null; then
      PRG="$link"
    else
      PRG=`dirname "$PRG"`"/$link"
    fi
  done

  JSWORD_HOME=`dirname "$PRG"`

  cd "$saveddir"

  # make it fully qualified
  JSWORD_HOME=`cd "$JSWORD_HOME" && pwd`
fi

cd $JSWORD_HOME

# Root contains Java directory with JREs, modules and mods.d
ROOT=`dirname $JSWORD_HOME`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$JSWORD_HOME" ] &&
    JSWORD_HOME=`cygpath --unix "$JSWORD_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

if [ -z "$JAVACMD" ] ; then 
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then 
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`ls -d $ROOT/Java/linux/jre*/bin/java 2> /dev/null`
    if [ -z "$JAVACMD" ] ; then 
      JAVACMD=`which java 2> /dev/null `
      if [ -z "$JAVACMD" ] ; then 
        JAVACMD=java
      fi
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

if [ -n "$CLASSPATH" ] ; then
  LOCALCLASSPATH="$CLASSPATH"
fi

# define the location of the jar files
JSWORD_LIB="$JSWORD_HOME"
if [ -e "${JSWORD_LIB}/lib" ] ; then
  JSWORD_LIB=$JSWORD_HOME
fi

# This is redundant if we are using the endorsed.dirs method
for i in "${JSWORD_LIB}"/*.jar
do
  # if the directory is empty, then it will return the input string
  # this is stupid, so case for it
  if [ -f "$i" ] ; then
    if [ -z "$LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH="$i"
    else
      LOCALCLASSPATH="$i":"$LOCALCLASSPATH"
    fi
  fi
done

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JSWORD_HOME=`cygpath --windows "$JSWORD_HOME"`
  JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  LOCALCLASSPATH=`cygpath --path --windows "$LOCALCLASSPATH"`
  CYGHOME=`cygpath --windows "$HOME"`
fi

# "-Djava.endorsed.dirs=${JSWORD_LIB}"
# -classpath "${JSWORD_HOME}/resource"
# Note: We always pass the "apple" arguments, even when not on a mac.
JSWORD_PROPERTIES=-Dapple.laf.useScreenMenuBar=true
JSWORD_PROPERTIES="$JSWORD_PROPERTIES -Dcom.apple.mrj.application.apple.menu.about.name=BibleDesktop"
[ -e "$JSWORD_HOME/JSword" ] && JSWORD_PROPERTIES="$JSWORD_PROPERTIES -Djsword.home=$JSWORD_HOME/JSword"
[ -e "$ROOT/mods.d" ]        && JSWORD_PROPERTIES="$JSWORD_PROPERTIES -Dsword.home=$ROOT"

"$JAVACMD" -classpath "${LOCALCLASSPATH}" $JSWORD_PROPERTIES org.crosswire.bibledesktop.desktop.Desktop
