# 
#  Title:�ٴ�·�����ԭ���
# 
#  Description:�ٴ�·�����ԭ���
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011.07.07
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;delete;|;EKT;|;operation;|;selFee;|;selRFee;|;clear;|;close;|;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;delete;|;EKT;|;operation;|;selFee;|;clear;|;close;|;

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

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

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=deleteRow
delete.pic=delete.gif

EKT.Type=TMenuItem
EKT.Text=ҽ�ƿ�
EKT.Tip=ҽ�ƿ�
EKT.M=E
EKT.key=Ctrl+E
EKT.Action=onReadCard
EKT.pic=042.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

operation.Type=TMenuItem
operation.Text=�Ʒ�ģ��
operation.Tip=�Ʒ�ģ��
operation.M=P
operation.Action=onTmplt
operation.pic=operation.gif

selFee.Type=TMenuItem
selFee.Text=���ò�ѯ
selFee.Tip=���ò�ѯ
selFee.M=IS
selFee.Action=onSelFee
selFee.pic=inscon.gif

selRFee.Type=TMenuItem
selRFee.Text=�˷Ѳ�ѯ
selRFee.Tip=�˷Ѳ�ѯ
selRFee.M=IS
selRFee.Action=onSelReturnFee
selRFee.pic=inscon.gif


