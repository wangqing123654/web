<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;print;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;clear;export;print;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=S
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif


print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=B
print.Action=onPrint
print.pic=print.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif