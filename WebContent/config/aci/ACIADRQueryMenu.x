<Type=TMenuBar>
UI.Item=File;Window
UI.button=report;unReport;|;query;|;delete;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=report;unReport;|;query;|;delete;|;export;|;clear;|;close

report.Type=TMenuItem
report.Text=���ϱ�
report.Tip=���ϱ�
report.M=R
report.key=Ctrl+R
report.Action=onReport
report.pic=032.gif

unReport.Type=TMenuItem
unReport.Text=ȡ�����ϱ�
unReport.Tip=ȡ�����ϱ�
unReport.M=R
unReport.key=Ctrl+R
unReport.Action=onUnReport
unReport.pic=Undo.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=D
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Q
clear.Action=onClear
clear.pic=clear.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
