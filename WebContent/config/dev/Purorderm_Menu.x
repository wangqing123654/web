 #
 # Title: �豸�������趨�趨
 #
 # Description:�豸�������趨�趨
 #
 # Copyright: JavaHis (c) 2008
 #
 # @author sundx
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=quary;clear;generateReceipt;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=quary;clear;generateReceipt;|;close



quary.Type=TMenuItem
quary.Text=��ѯ
quary.Tip=��ѯ
quary.M=Q
quary.key=Ctrl+F
quary.Action=onQuery
quary.pic=query.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

generateReceipt.Type=TMenuItem
generateReceipt.Text=���ɶ�����
generateReceipt.Tip=���ɶ�����
generateReceipt.M=R
generateReceipt.key=Ctrl+R
generateReceipt.Action=onGenerateReceipt
generateReceipt.pic=037.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif