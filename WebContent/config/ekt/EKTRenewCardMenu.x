 #
  # Title: ҽ�ƿ���ʧ/����
  #
  # Description: ҽ�ƿ���ʧ/����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author pangben 20111007
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;read;|;query;|;cardprint;|;updateEKTpwd;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;read;|;query;|;renew;|;cardprint;|;updateEKTpwd;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onRenew
save.pic=save.gif

read.Type=TMenuItem
read.Text=ҽ�ƿ�
read.Tip=ҽ�ƿ�(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

query.Type=TMenuItem
query.Text=������ѯ
query.Tip=������ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

cardprint.Type=TMenuItem
cardprint.Text=��Ƭ��ӡ
cardprint.Tip=��Ƭ��ӡ
cardprint.M=D
cardprint.key=Ctrl+D
cardprint.Action=onPrint
cardprint.pic=print.gif

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

updateEKTpwd.Type=TMenuItem
updateEKTpwd.Text=ҽ�ƿ��޸�����
updateEKTpwd.Tip=ҽ�ƿ��޸�����
updateEKTpwd.M=U
updateEKTpwd.Action=updateEKTPwd
updateEKTpwd.pic=007.gif