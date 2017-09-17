#define AppGuid "{@APP_GUID@"
#define AppId "@APP_ID@"
#define AppName "@APP_NAME@"
#define AppVersion "@APP_VERSION@"
#define AppProvider "@APP_MANUFACTURER@"
#define AppDesc "@APP_DESC@"
#define AppPath "@APP_PATH@"
#define AppLogPath "@APP_LOG_PATH@"

#define MyCurrentYear GetDateTimeString('yyyy', '', '');

[Setup]
AppId={#AppGuid}
AppName={#AppName}
AppVersion={#AppVersion}
AppVerName={#AppName} {#AppVersion}
AppPublisher={#AppProvider}
AppComments={#AppDesc}

WizardImageFile=setup.bmp
WizardSmallImageFile=setup-mini.bmp
LicenseFile=license.txt

DisableWelcomePage=no
DefaultDirName={#AppPath}
DisableDirPage=yes
DefaultGroupName={#AppName}
DisableProgramGroupPage=yes
UsePreviousAppDir=yes
SetupLogging=yes
ChangesEnvironment=yes

OutputBaseFilename=@APP_SETUPNAME@
Compression=lzma2
SolidCompression=yes

PrivilegesRequired=admin

UninstallDisplayName={#AppName}
UninstallDisplayIcon={app}\{#AppId}.ico

VersionInfoCompany={#AppProvider}
VersionInfoCopyright={#AppProvider} {#MyCurrentYear}
VersionInfoProductName={#AppName}
VersionInfoDescription={#AppName}
VersionInfoVersion={#AppVersion}

[Registry]
Root: HKLM; Subkey: "SOFTWARE\{#AppProvider}"; Flags: uninsdeletekeyifempty
Root: HKLM; Subkey: "SOFTWARE\{#AppProvider}\{#AppId}"
Root: HKLM; Subkey: "SOFTWARE\{#AppProvider}\{#AppId}"; ValueType: string; ValueName: "Path"; ValueData: "{app}"
Root: HKLM; Subkey: "SOFTWARE\{#AppProvider}\{#AppId}"; ValueType: string; ValueName: "Version"; ValueData: "{#AppVersion}"
Root: HKLM; Subkey: "SOFTWARE\Policies\Microsoft\Windows\ScPnP"; ValueType: dword; ValueName: "EnableScPnP"; ValueData: "0"; Flags: createvalueifdoesntexist

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Dirs]
Name: "{#AppPath}"
Name: "{#AppLogPath}"

[Files]
Source: "{#AppId}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{commondesktop}\{#AppName}"; Filename: "{app}\{#AppId}.exe"; WorkingDir: "{app}"; IconFilename: "{app}\{#AppId}.ico"
Name: "{commonprograms}\{#AppName}\{#AppName}"; Filename: "{app}\{#AppId}.exe"; WorkingDir: "{app}"; IconFilename: "{app}\{#AppId}.ico"
Name: "{commonprograms}\{#AppName}\Uninstall {#AppName}"; Filename: "{uninstallexe}"

[UninstallDelete]
Type: filesandordirs; Name: "{app}\ext"

[CustomMessages]
AlreadyUpdated={#AppName} est deja en version {#AppVersion} ou plus recent.
NoDowngrade=Une version plus recente de {#AppName} est deja installee.

[Code]

var
  CurrentDate: String;
  IsSilent: Boolean;
  // Uninstall args
  UninstallUpgrade : String;
  // Check current version
  IsUpgrade: Boolean;
  UninstallStr: String;
  CurrentVersion: String;
  // Labels
  StartTick: DWORD;
  PercentLabel: TNewStaticText;
  ElapsedLabel: TNewStaticText;
  RemainingLabel: TNewStaticText;

function GetTickCount: DWORD;
  external 'GetTickCount@kernel32.dll stdcall';

function TicksToStr(Value: DWORD): string;
var
  I: DWORD;
  Hours, Minutes, Seconds: Integer;
begin
  I := Value div 1000;
  Seconds := I mod 60;
  I := I div 60;
  Minutes := I mod 60;
  I := I div 60;
  Hours := I mod 24;
  Result := Format('%.2d:%.2d:%.2d', [Hours, Minutes, Seconds]);
end;

function GetNumber(var temp: String): Integer;
var
  part: String;
  pos1: Integer;
begin
  if Length(temp) = 0 then
  begin
    Result := -1;
    Exit;
  end;
    pos1 := Pos('.', temp);
    if (pos1 = 0) then
    begin
      Result := StrToInt(temp);
    temp := '';
    end
    else
    begin
    part := Copy(temp, 1, pos1 - 1);
      temp := Copy(temp, pos1 + 1, Length(temp));
      Result := StrToInt(part);
    end;
end;

function CompareInner(var temp1, temp2: String): Integer;
var
  num1, num2: Integer;
begin
    num1 := GetNumber(temp1);
  num2 := GetNumber(temp2);
  if (num1 = -1) or (num2 = -1) then
  begin
    Result := 0;
    Exit;
  end;
      if (num1 > num2) then
      begin
        Result := 1;
      end
      else if (num1 < num2) then
      begin
        Result := -1;
      end
      else
      begin
        Result := CompareInner(temp1, temp2);
      end;
end;

function CompareVersion(str1, str2: String): Integer;
var
  temp1, temp2: String;
begin
    temp1 := str1;
    temp2 := str2;
    Result := CompareInner(temp1, temp2);
end;

function CheckAppVersion(StrKeyPath: String): Boolean;
var
  CompareResult: Integer;
begin
  Result := True;
  if RegValueExists(HKEY_LOCAL_MACHINE, StrKeyPath, 'UninstallString') then begin
    RegQueryStringValue(HKEY_LOCAL_MACHINE, StrKeyPath, 'UninstallString', UninstallStr);
    RegQueryStringValue(HKEY_LOCAL_MACHINE, StrKeyPath, 'DisplayVersion', CurrentVersion);
    CompareResult := CompareVersion(CurrentVersion, '{#AppVersion}');
    if (CompareResult < 0) then begin
      Log('Current version ' + CurrentVersion + ' < ' + ExpandConstant('{#AppVersion} (upgrade)'));
      IsUpgrade := True;
      Result := True;
    end
    else if (CompareResult = 0) then begin
      Log('Current version ' + CurrentVersion + ' = ' + ExpandConstant('{#AppVersion}') + ' (already updated)');
      if not IsSilent then begin
        MsgBox(ExpandConstant('{cm:AlreadyUpdated}'), mbInformation, MB_OK);
      end;
      Result := False;
    end
    else begin
      Log('Current version ' + CurrentVersion + ' > ' + ExpandConstant('{#AppVersion}') + ' (no downgrade)');
      if not IsSilent then begin
        MsgBox(ExpandConstant('{cm:NoDowngrade}'), mbInformation, MB_OK);
      end;
      Result := False;
    end;
  end;
end;

function InitializeSetup: Boolean;
var
  j: Integer;
  Unins32: String;
  Unins64: String;
begin
  Log('-- InitializeSetup --');
  Result := True;
  IsSilent := False;
  CurrentDate := GetDateTimeString('yyyy-mm-dd-hhnnss', #0, #0);

  // Perform install args
  for j := 1 to ParamCount do begin
    if CompareText(ParamStr(j), '/SILENT') = 0 then begin
      IsSilent := True;
      Break;
    end;
  end;

  // Check app version
  Unins32 := ExpandConstant('SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{#AppGuid}_is1');
  Unins64 := ExpandConstant('SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\{#AppGuid}_is1');
  if RegValueExists(HKEY_LOCAL_MACHINE, Unins32, 'UninstallString') then begin
    Log('Start CheckAppVersion 32bits');
    Result := CheckAppVersion(Unins32);
  end
  else if IsWin64 and RegValueExists(HKEY_LOCAL_MACHINE, Unins64, 'UninstallString') then begin
    Log('Start CheckAppVersion 64bits');
    Result := CheckAppVersion(Unins64);
  end;
end;

function InitializeUninstall(): Boolean;
var
  LogFilePath: String;
begin
  LogFilePath := ExpandConstant('{#AppLogPath}/uninstall.' + CurrentDate + '.log');
  SaveStringToFile(LogFilePath, '-- InitializeUninstall --' + #13#10, False);
  Result := True;
  CurrentDate := GetDateTimeString('yyyy-mm-dd-hhnnss', #0, #0);
end;

procedure InitializeWizard;
begin
  Log('-- InitializeWizard --');
  PercentLabel := TNewStaticText.Create(WizardForm);
  PercentLabel.Parent := WizardForm.ProgressGauge.Parent;
  PercentLabel.Left := 0;
  PercentLabel.Top := WizardForm.ProgressGauge.Top + WizardForm.ProgressGauge.Height + 12;

  ElapsedLabel := TNewStaticText.Create(WizardForm);
  ElapsedLabel.Parent := WizardForm.ProgressGauge.Parent;
  ElapsedLabel.Left := 0;
  ElapsedLabel.Top := PercentLabel.Top + PercentLabel.Height + 4;

  RemainingLabel := TNewStaticText.Create(WizardForm);
  RemainingLabel.Parent := WizardForm.ProgressGauge.Parent;
  RemainingLabel.Left := 0;
  RemainingLabel.Top := ElapsedLabel.Top + ElapsedLabel.Height + 4;
end;

procedure DeinitializeSetup();
var
  TmpLogFilePath: String;
  LogFilePath: String;
begin
  Log('-- DeinitializeSetup --');
  TmpLogFilePath := ExpandConstant('{log}');
  LogFilePath := ExpandConstant('{#AppLogPath}/setup.' + CurrentDate + '.log');
  Log('Move log from ' + TmpLogFilePath + ' to ' + LogFilePath);
  FileCopy(TmpLogFilePath, LogFilePath, False);
end;

procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
var
  LogFilePath: String;
  PidFilePath: String;
begin
  LogFilePath := ExpandConstant('{#AppLogPath}/uninstall.' + CurrentDate + '.log');
  if CurUninstallStep = usUninstall then begin
    PidFilePath := ExpandConstant('{#AppPath}/{#AppId}.pid');

    SaveStringToFile(LogFilePath, 'Check PID file: ' + PidFilePath + #13#10, True);
    if FileExists(PidFilePath) then begin
      SaveStringToFile(LogFilePath, 'PID file found. Stopping app.' + #13#10, True);
      DeleteFile(PidFilePath);
      Sleep(5000);
    end;
  end
  else if CurUninstallStep = usPostUninstall then begin
    // Perform uninstall args
    UninstallUpgrade := ExpandConstant('{param:UNINSTALLUPGRADE}');
    if UninstallUpgrade = '' then begin
      UninstallUpgrade := '0';
    end;
    SaveStringToFile(LogFilePath, 'Upgrade: ' + UninstallUpgrade + #13#10, True);
  end;
end;

procedure CurPageChanged(CurPageID: Integer);
begin
  if CurPageID = wpInstalling then
  begin
    StartTick := GetTickCount;
  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
var
  ResultCode : Integer;
  PidFilePath : String;
begin
  if (CurStep = ssInstall) then begin
    if IsUpgrade then begin
      // Stop app
      PidFilePath := ExpandConstant('{#AppPath}/{#AppId}.pid');
      if FileExists(PidFilePath) then begin
        Log('PID file found. Stopping app.');
        DeleteFile(PidFilePath);
        Sleep(5000);
      end;

      Exec(RemoveQuotes(UninstallStr), '/SILENT /NORESTART /SUPPRESSMSGBOXES /UNINSTALLUPGRADE=1', '', SW_HIDE, ewWaitUntilTerminated, ResultCode);
      Log('Uninstall previous version: ' + IntToStr(ResultCode));
    end;
  end else if CurStep = ssPostInstall then begin
    Log('-- Process post install --');
    PercentLabel.Caption := ExpandConstant('Init and exec {#AppName}...');
    ElapsedLabel.Caption := '';
    RemainingLabel.Caption := '';
  end;
end;

procedure CurInstallProgressChanged(CurProgress, MaxProgress: Integer);
var
  CurTick: DWORD;
begin
  CurTick := GetTickCount;
  PercentLabel.Caption := Format('Done: %.2f %%', [(CurProgress * 100.0) / MaxProgress]);
  ElapsedLabel.Caption :=  Format('Elapsed: %s', [TicksToStr(CurTick - StartTick)]);
  if CurProgress > 0 then begin
    RemainingLabel.Caption := Format('Remaining: %s', [TicksToStr(((CurTick - StartTick) / CurProgress) * (MaxProgress - CurProgress))]);
  end;
end;
