; Java Launcher
;--------------

!include LogicLib.nsh

!define PRODUCT_NAME "BibleDesktop"
!define PRODUCT_VERSION "1.0"
!define CLASS "org.crosswire.bibledesktop.desktop.Desktop"
!define JAVALIB "$EXEDIR"

!define JRE_VERSION "1.4.2"
!define JRE_URL "http://dlc.sun.com/jdk/j2re-1_4_2_07-windows-i586-p.exe"

SetCompressor lzma

Var HasJRE

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
Caption "${PRODUCT_NAME} ${PRODUCT_VERSION}"
Icon "bibledesktop.ico"
OutFile "${PRODUCT_NAME}.exe"
BrandingText " "

AutoCloseWindow true
ShowInstDetails nevershow

Section ""

  Call BuildClassPath
  Pop $R1

  Call FindJRE
  Pop $R0

  StrCpy $0 '"$R0" -classpath "$R1" ${CLASS}'

;ClearErrors
;FileOpen $1 $EXEDIR\java.log w
;IfErrors done
;FileWrite $1 $0
;FileClose $1
;done:

  SetOutPath $EXEDIR
  Exec $0
SectionEnd

Function BuildClassPath
; Builds the class path with all the jars in JAVALIB
; and puts the results on the stack.

  ; save state
  Push $R0
  Push $R1
  Push $R2

  ; clear R0
  StrCpy $R0 ""
  
  ; Iterate over all the jar files in JAVALIB
  FindFirst $R1 $R2 "${JAVALIB}\*.jar"
  ${Unless} ${Errors}
    ${Do}
      ${If} $R0 == ""
        StrCpy $R0 "${JAVALIB}\$R2"
      ${Else}
        StrCpy $R0 "$R0;${JAVALIB}\$R2"
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
  Call DetectJRE
  pop $HasJRE

  ${If} $HasJRE == "No"
    Call GetJRE
  ${EndIf}

  SetSilent silent
FunctionEnd

Function GetJRE
  MessageBox MB_OK "$(^NAME) uses Java ${JRE_VERSION} or later, it will now be downloaded and installed."

  StrCpy $2 "$TEMP\Java Runtime Environment.exe"
  InetLoad::load /POPUP "Getting Java for ${PRODUCT_NAME}" ${JRE_URL} $2
  Pop $R0 ;Get the return value
  StrCmp $R0 "OK" +3
  ;NSISdl::download /TIMEOUT=30000 ${JRE_URL} $2
  ;Pop $R0 ;Get the return value
  ;StrCmp $R0 "success" +3
  MessageBox MB_OK "Download failed: $R0"
  Quit
  ExecWait $2
  Delete $2
FunctionEnd

Function DetectJRE
  ; Put "Yes" or "No" on the stack

  ; save state
  push $R0

  ; When the JRE is installed so is Web Start.
  ; The current version of WebStart is something like 1.4.2_07
  ; This is more fine grained than Java Runtime Environment
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Web Start" "CurrentVersion"

  ${If} ${JRE_VERSION} S> $R0
     StrCpy $R0 "No"
  ${Else}
     StrCpy $R0 "Yes"
  ${EndIf}

  ; restore state and put results in R0
  Exch $R0
FunctionEnd

Function FindJRE
;
;  Find JRE (javaw.exe) and put it on the stack
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  Else an error
;
;  Note: It is possible that this will find a version of java
;        that is earlier than what is required.

  ; save state
  Push $R0

  ${If} ${FileExists} "$EXEDIR\jre\bin\javaw.exe"
     StrCpy $R0 "$EXEDIR\jre\bin\javaw.exe"
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
          MessageBox MB_OK "Could not find Java.$\rPlease install Java and try again."
          Quit
        ${EndIf}
      ${EndIf}
    ${EndIf}
  ${EndIf}

  ; restore state and put results in R0
  Exch $R0
FunctionEnd