#############################################
# <p>Title:ÿ�շ����嵥 </p>
#
# <p>Description:ÿ�շ����嵥 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2010.09.28
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;printview;chnEnfill;|;print;|;push;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=query;|;printview;chnEnfill;|;print;|;push;|;clear;|;close


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

printview.Type=TMenuItem
printview.Text=��ӡԤ��
printview.Tip=��ӡԤ��(Ctrl+P)
printview.M=P
printview.key=Ctrl+P
printview.Action=onPrintView
printview.pic=print.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=
print.key=
print.Action=onPrint
print.pic=print-1.gif


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��(F5)
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

chnEnfill.Type=TMenuItem
chnEnfill.Text=��Ӣ�ķ����嵥
chnEnfill.Tip=��Ӣ�ķ����嵥
chnEnfill.M=E
chnEnfill.key=Ctrl+E
chnEnfill.Action=onChnEnFill
chnEnfill.pic=patlist.gif

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Empty
clear.Tip=���(Ctrl+Z)
clear.zhTip=���
clear.enTip=Empty
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�(Alt+F4)
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

push.Type=TMenuItem
push.Text=��ӡ���˵�
push.zhText=��ӡ���˵�
push.enText=
push.Tip=��ӡ���˵�
push.zhTip=��ӡ���˵�
push.enTip=
push.M=X
push.key=
push.Action=onPush
push.pic=print-2.gif
.