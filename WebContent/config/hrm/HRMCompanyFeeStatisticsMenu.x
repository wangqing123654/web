<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif