<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;cancel;|;printlist;|;printpaster;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;cancel;|;printlist;|;printpaster;|;clear;|;close

save.Type=TMenuItem
save.Text=ȷ��
save.Tip=ȷ��
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

cancel.Type=TMenuItem
cancel.Text=ȡ��
cancel.Tip=ȡ��
cancel.M=Q
cancel.key=
cancel.Action=onCancel
cancel.pic=cancle.gif

printlist.Type=TMenuItem
printlist.Text=��ӡ�嵥
printlist.Tip=��ӡ�嵥
printlist.M=P
printlist.Action=onPrintList
printlist.pic=print.gif

printpaster.Type=TMenuItem
printpaster.Text=��ӡ��ֽ
printpaster.Tip=��ӡ��ֽ
printpaster.M=P
printpaster.Action=onPrintPaster
printpaster.pic=pha_print.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

