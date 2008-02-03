; Java Launcher
;--------------

!include LogicLib.nsh

!define PRODUCT_NAME "BibleDesktop"
!define PRODUCT_VERSION "@release.version@"
!define CLASS "org.crosswire.bibledesktop.desktop.Desktop"

;!define JRE_VERSION "1.5.0"
;!define JRE_URL "http://dlc.sun.com/jdk/jre-1_5_0_01-windows-i586-p.exe"
; For a listing of Java 6 versions see: http://www.nabber.org/projects/appupdater/metalink/?app=Java
!define JRE_VERSION "1.6"
; The following is 6.0.3 aka 1.6.0 u3
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=12798"

SetCompressor lzma

Var HasJRE
Var JavaLib

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
Caption "${PRODUCT_NAME} ${PRODUCT_VERSION}"
Icon "${PRODUCT_NAME}.ico"
OutFile "${PRODUCT_NAME}.exe"
BrandingText " "
; Under Windows Vista run this as a user.
RequestExecutionLevel user

AutoCloseWindow true
ShowInstDetails nevershow

Section ""

  Call FindJRE
  Pop $R0

  ${If} $R0 == "No"
    MessageBox MB_OK "Could not find Java.$\rPlease install Java and try again."
    Quit
  ${EndIf}

  SetOutPath $EXEDIR

  StrCpy $JavaLib ""
  Call BuildClassPath
  Pop $R1

  ${If} $R1 == ""
    StrCpy $JavaLib "lib\"
    Call BuildClassPath
    Pop $R1
  ${EndIF}

  StrCpy $0 '"$R0" -classpath "$R1" ${CLASS}'

  ; The following is for debugging
  ClearErrors
  FileOpen $1 "${PRODUCT_NAME}.bat" w
  IfErrors done
  FileWrite $1 $0
  FileClose $1
  done:

  Exec $0
SectionEnd

Function BuildClassPath
; Builds the class path with all the jars in JavaLib
; and puts the results on the stack.

  ; save state
  Push $R0
  Push $R1
  Push $R2

  ; clear R0
  StrCpy $R0 ""
  
  ; Iterate over all the jar files in JAVALIB
  ClearErrors
  FindFirst $R1 $R2 "$JavaLib*.jar"
  ${Unless} ${Errors}
    ${Do}
      ${If} $R0 == ""
        StrCpy $R0 "$JavaLib$R2"
      ${Else}
        StrCpy $R0 "$R0;$JavaLib$R2"
      ${EndIf}
      FindNext $R1 $R2
    ${LoopUntil} ${Errors}
    FindClose $R1
  ${EndUnless}
  
  ; restore state and put results on the stack
  Pop $R2
  Pop $R1
  Exch $R0
FunctionEnd

Function .onInit
  Call FindJRE
  pop $HasJRE

  ${If} $HasJRE == "No"
    Call GetJRE
  ${EndIf}

  SetSilent silent
FunctionEnd

Function GetJRE
  MessageBox MB_OK "${PRODUCT_NAME} uses Java ${JRE_VERSION} or later, it will now be downloaded and installed."

  StrCpy $2 "$TEMP\Java Runtime Environment.exe"
  ; Obtain from http://nsis.sourceforge.net/InetLoad_plug-in
  ; and put inetload.dll from zip file in NSIS plugins directory
  InetLoad::load /POPUP "Getting Java for ${PRODUCT_NAME}" ${JRE_URL} $2
  Pop $R0 ;Get the return value
  StrCmp $R0 "OK" +5
  ;NSISdl::download /TIMEOUT=30000 ${JRE_URL} $2
  ;Pop $R0 ;Get the return value
  ;StrCmp $R0 "success" +5
  MessageBox MB_OK "Could not install Java for you. Please install Java from http://www.java.com then re-run ${PRODUCT_NAME}."
  StrCpy $0 "http://www.java.com"
  Call openLinkNewWindow
  Quit
  ExecWait $2
  Delete $2
FunctionEnd

# uses $0
Function openLinkNewWindow
  Push $3 
  Push $2
  Push $1
  Push $0
  ReadRegStr $0 HKCR "http\shell\open\command" ""
# Get browser path
    DetailPrint $0
  StrCpy $2 '"'
  StrCpy $1 $0 1
  StrCmp $1 $2 +2 # if path is not enclosed in " look for space as final char
    StrCpy $2 ' '
  StrCpy $3 1
  loop:
    StrCpy $1 $0 1 $3
    DetailPrint $1
    StrCmp $1 $2 found
    StrCmp $1 "" found
    IntOp $3 $3 + 1
    Goto loop
 
  found:
    StrCpy $1 $0 $3
    StrCmp $2 " " +2
      StrCpy $1 '$1"'
 
  Pop $0
  Exec '$1 $0'
  Pop $1
  Pop $2
  Pop $3
FunctionEnd

Function FindJRE
;
;  Find JRE (javaw.exe) and put it on the stack
;  1 - in ..\Java\Win32\jre*
;  2 - in .\jre directory (JRE Installed with application)
;  3 - in JAVA_HOME environment variable
;  4 - in the registry
;  Else an error
;
;  Note: It is possible that this will find a version of java
;        that is earlier than what is required.

  ; save state
  Push $R0

  ${If} ${FileExists} "$EXEDIR\..\Java\win32\jre\bin\javaw.exe"
    StrCpy $R0 "$EXEDIR\..\Java\win32\jre\bin\javaw.exe"
  ${ElseIf} ${FileExists} "$EXEDIR\Java\win32\jre\bin\javaw.exe"
     StrCpy $R0 "$EXEDIR\Java\win32\jre\bin\javaw.exe"
  ${Else}
    ReadEnvStr $R0 "JAVA_HOME"
    ${If} ${FileExists} "$R0\bin\javaw.exe"
      StrCpy $R0 "$R0\bin\javaw.exe"
    ${Else}
      ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
      ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R0" "JavaHome"
      ${If} ${FileExists} "$R0\bin\javaw.exe"
        StrCpy $R0 "$R0\bin\javaw.exe"
      ${Else}
        ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
        ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$R0" "JavaHome"
        ${If} ${FileExists} "$R0\bin\javaw.exe"
          StrCpy $R0 "$R0\bin\javaw.exe"
        ${Else}
          StrCpy $R0 "No"
        ${EndIf}
      ${EndIf}
    ${EndIf}
  ${EndIf}

  ; restore state and put results in R0
  Exch $R0
FunctionEnd
