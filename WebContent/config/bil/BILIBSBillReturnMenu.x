<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;returnFee;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;clear;returnFee;|;Refresh;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=S
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



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

returnFee.Type=TMenuItem
returnFee.Text=�����˵�
returnFee.Tip=�����˵�
returnFee.M=P
returnFee.Action=onReturnFee
returnFee.pic=030.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

