<Type=TMenuBar>
UI.Item=File;Window
UI.button=Print;|;PRINT_LIST;PrintSetup;|;ShowZoom;|;exit

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=PrintSetup;|;PSetup;|;Print;|;LinkPrint;ShowRowIDSwitch;|;exit

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Save;Load

Save.Type=TMenuItem
Save.Text=��������(������)
Save.M=S
Save.Action=onSave

Load.Type=TMenuItem
Load.Text=��ȡ����(������)
Load.M=L
Load.Action=onLoad

Print.Type=TMenuItem
Print.Text=��ӡ
Print.Enabled=true
Print.M=P
Print.Action=onPrint
Print.pic=print.gif

PrintSetup.Type=TMenuItem
PrintSetup.Text=��ӡ����
PrintSetup.Enabled=true
PrintSetup.M=S
PrintSetup.Action=onPrintSetup
PrintSetup.pic=print-2.gif

PSetup.Type=TMenuItem
PSetup.Text=��ӡ����
PSetup.Enabled=true
PSetup.M=E
PSetup.Action=onPSetup

LinkPrint.Type=TMenuItem
LinkPrint.Text=��ӡ
LinkPrint.Enabled=true
LinkPrint.M=T
LinkPrint.Action=onPrintXDDialog

ShowRowIDSwitch.Type=TMenuItem
ShowRowIDSwitch.Text=��ʾ�кſ���
ShowRowIDSwitch.Enabled=true
ShowRowIDSwitch.M=T
ShowRowIDSwitch.Action=onShowRowIDSwitch

ShowZoom.type=TShowZoomCombo
ShowZoom.PreferredWidth=80
ShowZoom.ShowID=false
ShowZoom.SelectedAction=onShowZoom

PRINT_LIST.Type=TPrintListCombo
PRINT_LIST.PreferredWidth=300
PRINT_LIST.ShowID=false
PRINT_LIST.SelectedAction=onPrintList

exit.Type=TMenuItem
exit.Text=�˳�
exit.Tip=close
exit.M=X
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif

