<Type=TMenuBar>
UI.Item=File
UI.button=save;|;query;|;check;|;export;|;print;|;clear;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;check;|;export;|;print;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=F4
export.Action=onExport
export.pic=export.gif


check.Type=TMenuItem
check.Text=�̵�
check.Tip=�̵�
check.M=P
check.key=F9
check.Action=onCheck
check.pic=026.gif



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


print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=F
print.key=Alt+F8
print.Action=onPrint
print.pic=print.gif