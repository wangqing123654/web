#############################################
# <p>Title:�Һ��հ���ѯ�Һ���Ϣ����Menu </p>
#
# <p>Description:�Һ��հ���ѯ�Һ���Ϣ����Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2010.04.14
# @version 4.0
#############################################
<Type=TMenuBar>
UI.button=unreg;|;print;|;execle;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=unreg;|;print;|;execle;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

execle.Type=TMenuItem
execle.Text=����EXECLE
execle.Tip=����EXECLE
execle.M=I
execle.key=Ctrl+F
execle.Action=onExecl
execle.pic=export.gif

unreg.Type=TMenuItem
unreg.Text=�˹�
unreg.Tip=�˹�
unreg.M=U
unreg.key=
unreg.Action=onUnReg
unreg.pic=030.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

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

info.Type=TMenuItem
info.Text=������ϸ����
info.Tip=������ϸ����
info.Action=onInfo
info.pic=spreadout.gif
