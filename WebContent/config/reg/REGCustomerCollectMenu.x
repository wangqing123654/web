# 
#  Title:��������������ҽԺ�ͻ����ܱ�Menu
# 
#  Description:��������������ҽԺ�ͻ����ܱ�Menu
# 
#  Copyright: Copyright (c) Javahis 2009
# 
#  author huzc 20151224
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;printO;|;printI;|;onExportO;|;onExportI;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;printO;|;printI;|;onExportO;|;onExportI;|;clear;|;close



Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif



close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

printO.Type=TMenuItem
printO.Text=�����ӡ
printO.Tip=��ӡ
printO.M=P
printO.Action=onPrintO
printO.pic=print.gif

printI.Type=TMenuItem
printI.Text=סԺ��ӡ
printI.Tip=��ӡ
printI.M=P
printI.Action=onPrintI
printI.pic=print.gif

onExportO.Type=TMenuItem
onExportO.Text=������
onExportO.Tip=���
onExportO.M=E
onExportO.Action=onExportO
onExportO.pic=045.gif

onExportI.Type=TMenuItem
onExportI.Text=סԺ���
onExportI.Tip=���
onExportI.M=E
onExportI.Action=onExportI
onExportI.pic=045.gif