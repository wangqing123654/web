<Type=TMenuBar>
UI.Item=File;Window
UI.button=print;|;reprint;|;disCharge;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=print;|;reprint;|;disCharge;|;clear;|;close

print.Type=TMenuItem
print.Text=�����嵥
print.Tip=�����嵥
print.M=F
print.key=Ctrl+H
print.Action=onPrintDetail
print.pic=print-2.gif

reprint.Type=TMenuItem
reprint.Text=����
reprint.Tip=Ʊ�ݲ���
reprint.M=F
reprint.key=Ctrl+P
reprint.Action=onRePrint
reprint.pic=print_red.gif

disCharge.Type=TMenuItem
disCharge.Text=�˷�
disCharge.Tip=�˷�
disCharge.M=S
disCharge.key=Ctrl+S
disCharge.Action=onDisCharge
disCharge.pic=030.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
