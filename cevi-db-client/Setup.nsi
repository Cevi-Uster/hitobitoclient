; Taken from http://nsis.sourceforge.net/Simple_installer_with_JRE_check by weebib
; Use it as you desire.
 
; Credit given to so many people of the NSIS forum.
 
!define AppName "Cevi.DB-Client"
!define JarName "cevi-db-client-1.0-SNAPSHOT-jar-with-dependencies.jar"
!define AppVersion "@version@"
!define ShortName "Cevi.DB-Client"
!define Vendor "Marc Baumgartner"
 
!include "MUI.nsh"
!include "Sections.nsh"
  
;--------------------------------
;Configuration
 
  ;General
  Name "${AppName}"
  OutFile "target/${AppName}-${AppVersion}-Setup.exe"
 
  ;Folder selection page
  InstallDir "$PROGRAMFILES64\${SHORTNAME}"
 
  ;Get install folder from registry if available
  InstallDirRegKey HKLM "SOFTWARE\${Vendor}\${ShortName}" ""
 
; Installation types
;InstType "full"	; Uncomment if you want Installation types
 
;--------------------------------
;Pages
 
  ; License page
  LicenseLangString MUILicense ${LANG_ENGLISH} "License.txt"
  !insertmacro MUI_PAGE_LICENSE $(MUILicense)

 

  !insertmacro MUI_PAGE_INSTFILES
  !define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Installation complete"
  !define MUI_PAGE_HEADER_TEXT "Installing"
  !define MUI_PAGE_HEADER_SUBTEXT "Please wait while ${AppName} is being installed."
; Uncomment the next line if you want optional components to be selectable
;  !insertmacro MUI_PAGE_COMPONENTS
  !define MUI_PAGE_CUSTOMFUNCTION_PRE myPreInstfiles
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE RestoreSections
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
 
;--------------------------------
;Modern UI Configuration
 
  !define MUI_ABORTWARNING
 
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"
 
;--------------------------------
;Language Strings
 
  ;Description
  LangString DESC_SecAppFiles ${LANG_ENGLISH} "Application files copy"
 
  ;Header
  LangString TEXT_JRE_SUBTITLE ${LANG_ENGLISH} "Installation"
  LangString TEXT_PRODVER_TITLE ${LANG_ENGLISH} "Installed version of ${AppName}"
  LangString TEXT_PRODVER_SUBTITLE ${LANG_ENGLISH} "Installation cancelled"
 
;--------------------------------
;Reserve Files
 
  ;Only useful for BZIP2 compression
 
 
  ReserveFile "jre.ini"
  !insertmacro MUI_RESERVEFILE_INSTALLOPTIONS
 
;--------------------------------
;Installer Sections
 

 
Section "Installation of ${AppName}" SecAppFiles
  SectionIn 1 RO	; Full install, cannot be unselected
			; If you add more sections be sure to add them here as well
  SetOutPath $INSTDIR
;Copy the JRE
File /r "bin/jre"
;Copy the JAR
File /r "target/${JarName}"
File /r "src/main/resources/cevilogo.ico"

  ;Store install folder
  WriteRegStr HKLM "SOFTWARE\${Vendor}\${ShortName}" "" $INSTDIR
 
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "DisplayName" "${AppName}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "NoModify" "1"
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "NoRepair" "1"
 
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
 
SectionEnd
 
Section "Start menu shortcuts" SecCreateShortcut
  SectionIn 1	; Can be unselected
  CreateDirectory "$SMPROGRAMS\${AppName}"
  CreateShortCut "$SMPROGRAMS\${AppName}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${AppName}\${AppName}.lnk" "$INSTDIR\jre\bin\javaw" '-jar "$INSTDIR\${JarName}"'  "$INSTDIR\cevilogo.ico" 0
  CreateShortCut "$DESKTOP\${AppName}.lnk" "$INSTDIR\jre\bin\javaw" '-jar "$INSTDIR\${JarName}"'  "$INSTDIR\cevilogo.ico" 0
; Etc
SectionEnd
 
;--------------------------------
;Installer Functions
 
Function .onInit
 
  ;Extract InstallOptions INI Files
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "jre.ini"
  Call SetupSections
 
FunctionEnd
 
Function myPreInstfiles
 
  Call RestoreSections
  SetAutoClose true
 
FunctionEnd
  
Function RestoreSections
  !insertmacro SelectSection ${SecAppFiles}
  !insertmacro SelectSection ${SecCreateShortcut}
 
FunctionEnd
 
Function SetupSections
  !insertmacro UnselectSection ${SecAppFiles}
  !insertmacro UnselectSection ${SecCreateShortcut}
FunctionEnd
 
;--------------------------------
;Uninstaller Section
 
Section "Uninstall"
 
  ; remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}"
  DeleteRegKey HKLM  "SOFTWARE\${Vendor}\${AppName}"
  ; remove shortcuts, if any.
  Delete "$SMPROGRAMS\${AppName}\*.*"
  Delete "$DESKTOP\${AppName}.lnk"
  Delete "$SMPROGRAMS\${AppName}"
  ; remove files
  RMDir /r "$INSTDIR"
 
SectionEnd