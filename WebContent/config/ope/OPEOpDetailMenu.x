#############################################
# <p>Title:������¼Menu </p>
#
# <p>Description:������¼Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.28
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;opstmp;|;clear;|;close

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
File.Item=save;|;opstmp;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.zhText=����
save.enText=Save
save.Tip=����(Ctrl+S)
save.zhTip=����(Ctrl+S)
save.enTip=Save(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

opstmp.Type=TMenuItem
opstmp.Text=����ģ��
opstmp.zhText=����ģ��
opstmp.enText=Operation Template
opstmp.Tip=����ģ��
opstmp.zhTip=����ģ��
opstmp.enTip=Operation Template
opstmp.Action=onOpstmp
opstmp.pic=new.gif

emr.Type=TMenuItem
emr.Text=�ṹ��������¼
emr.zhText=�ṹ��������¼
emr.enText=Operation Rec
emr.Tip=�ṹ��������¼
emr.zhTip=�ṹ��������¼
emr.enTip=Operation Rec
emr.Action=onEmr
emr.pic=emr-2.gif

print.Type=TMenuItem
print.Text=��ӡ
print.zhText=��ӡ
print.enText=Print
print.Tip=��ӡ(Ctrl+P)
print.zhTip=��ӡ
print.enTip=Print
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif