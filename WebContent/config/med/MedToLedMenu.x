 #
  # Title: ������к�
  #
  # Description: ������к�
  #
  # Copyright: BlueCore (c) 
  #
  # @author wanglong 2013.11.12
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;read;|;regist;|;unRegist;|;charge;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

read.Type=TMenuItem
read.Text=ҽ�ƿ�
read.Tip=ҽ�ƿ�(Ctrl+C)
read.M=C
read.key=Ctrl+C
read.Action=onReadCard
read.pic=042.gif

regist.Type=TMenuItem
regist.Text=����
regist.Tip=����(Ctrl+R)
regist.M=G
regist.key=Ctrl+R
regist.Action=onRegist
regist.pic=017.gif

unRegist.Type=TMenuItem
unRegist.Text=ȡ������
unRegist.Tip=ȡ������(Ctrl+U)
unRegist.M=U
unRegist.key=Ctrl+U
unRegist.Action=onUnRegist
unRegist.pic=Undo.gif

charge.Type=TMenuItem
charge.Text=����Ƽ�
charge.Tip=����Ƽ�
charge.M=
charge.key=
charge.Action=onCharge
charge.pic=bill.gif

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
clear.M=Z
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

