#############################################
# <p>Title:�ṩ�Ի���������ط���֧�����ʵʱ��أ��Ը�ֵ�Ĳġ�����ҩƷʹ�õļ�ع����ܡ�Menu </p>
#
# <p>Description:�ṩ�Ի���������ط���֧�����ʵʱ��أ��Ը�ֵ�Ĳġ�����ҩƷʹ�õļ�ع����ܡ�</p>
#
# <p>Copyright: Copyright (c) �����к� 2011</p>
#
# <p>Company: Javahis </p>
#
# @author ZhenQin 2011-05-09
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;print;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;export;|;print;|;close

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

export.Type=TMenuItem
export.Text=����Xls
export.Tip=����Xls(Ctrl+P)
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

