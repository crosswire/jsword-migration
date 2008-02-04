; Java Launcher
;--------------

!include LogicLib.nsh

!define PRODUCT_NAME "PortableBibleDesktop"
!define PRODUCT_VERSION "@release.version@"
!define CLASS "org.crosswire.bibledesktop.desktop.Desktop"

SetCompressor lzma

Var JavaLIB

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
Caption "${PRODUCT_NAME} ${PRODUCT_VERSION}"
Icon "BibleDesktop.ico"
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

  StrCpy $JavaLib ""
  Call BuildClassPath
  Pop $R1

  ${If} $R1 == ""
    StrCpy $JavaLib "lib\"
    Call BuildClassPath
    Pop $R1
  ${EndIF}

  StrCpy $0 '"$R0" -classpath "$R1" ${CLASS}'
  ; Use the following for USB/CD installs
  ; put JSword on the class path so that resources in it on a CD can be found.
  StrCpy $0 '"$R0" -classpath "$R1;$EXEDIR\JSword" -Djsword.home="$EXEDIR\JSword" -Dsword.home="$EXEDIR\.." ${CLASS}'

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
  SetSilent silent
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
