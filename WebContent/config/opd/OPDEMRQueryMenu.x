# 
#  Title:�Һ���ʷ��ѯ
# 
#  Description:�Һ���ʷ��ѯ
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.10.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;read;|;bingli;|;emr;|;lend;|;patquery;|;caseSheet;|;close;|;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=export;|;print;|;Refresh;|;query;|;clear;|;patquery;|;close

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

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

patquery.Type=TMenuItem
patquery.Text=������ѯ
patquery.Tip=������ѯ
patquery.M=
patquery.key=
patquery.Action=onPatQuery
patquery.pic=search-1.gif

read.Type=TMenuItem
read.Text=����
read.Tip=����(Ctrl+R)
read.M=
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

bingli.Type=TMenuItem
bingli.Text=����
bingli.Tip=����
bingli.M=
bingli.key=
bingli.Action=onErdSheet
bingli.pic=search.gif

caseSheet.Type=TMenuItem
caseSheet.Text=��ӡ����ǩ
caseSheet.Tip=��ӡ����ǩ
caseSheet.M=
caseSheet.key=
caseSheet.Action=onCaseSheet
caseSheet.pic=018.gif

lend.Type=TMenuItem
lend.Text=���Ĳ���
lend.Tip=���Ĳ���(Alt+L)
lend.M=
lend.key=
lend.Action=onlend
lend.pic=emr.gif

emr.Type=TMenuItem
emr.Text=д����
emr.zhText=д����
emr.enText=д����
emr.Tip=д����
emr.zhTip=д����
emr.enTip=д����
emr.M=S
emr.Action=onAddEmrWrite
emr.pic=emr-1.gif