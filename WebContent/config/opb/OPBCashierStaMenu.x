#############################################
# <p>Title:�����շ�Ա������ͳ��Menu </p>
#
# <p>Description:�����շ�Ա������ͳ��Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2010.03.23
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif