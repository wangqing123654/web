# 
#  Title:�Һ�����
# 
#  Description:�Һ�����
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=unreg;arrive;print;Wrist;clear|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=savePat;fee;unreg;arrive;Refresh;print;Wrist;clear;|;close

unreg.Type=TMenuItem
unreg.Text=�˹�
unreg.Tip=�˹�
unreg.M=U
unreg.key=Ctrl+U
unreg.Action=onUnReg
unreg.pic=030.gif

arrive.Type=TMenuItem
arrive.Text=����
arrive.Tip=����
arrive.M=A
arrive.key=Ctrl+A
arrive.Action=onArrive
arrive.pic=017.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Wrist.Type=TMenuItem
Wrist.Text=���
Wrist.Tip=���
Wrist.M=W
Wrist.key=Ctrl+W
Wrist.Action=onWrist
Wrist.pic=print-1.gif

savePat.Type=TMenuItem
savePat.Text=������Ϣ����
savePat.zhText=������Ϣ����
savePat.enText=Save
savePat.Tip=������Ϣ����
savePat.zhTip=������Ϣ����
savePat.enTip=Save
savePat.M=S
savePat.key=Ctrl+S
savePat.Action=onSavePat
savePat.pic=save.gif

fee.Type=TMenuItem
fee.Text=�Һ��շ�
fee.zhText=�Һ��շ�
fee.enText=Fee
fee.Tip=�Һ��շ�
fee.zhTip=�Һ��շ�
fee.enTip=Fee
fee.M=F
fee.key=Ctrl+F
fee.Action=onSaveReg
fee.pic=openbill-2.gif

