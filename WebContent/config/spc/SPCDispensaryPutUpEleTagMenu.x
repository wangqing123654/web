#
# Title: ҩ����ͨҩ�ϼ�menu
#
# Description:ҩ����ͨҩ�ϼ�menu
#
# Copyright: BlueCore (c) 2012
#
# @author wangzl
# @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=Refresh;query;clear;|;close


delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=S
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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