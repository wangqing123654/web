 #
  # Title: ҽ�ƿ���ֵ����
  #
  # Description: ҽ�ƿ���ֵ����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;read;|;showpat;|;MEMprint;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;read;|;showpat;|;MEMprint;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

read.Type=TMenuItem
read.Text=ҽ�ƿ�
read.Tip=ҽ�ƿ�(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

MEMprint.Type=TMenuItem
MEMprint.Text=��ӡ
MEMprint.Tip=��ӡ
MEMprint.M=P
MEMprint.key=Ctrl+P
MEMprint.Action=onRePrint
MEMprint.pic=print_red.gif

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

showpat.Type=TMenuItem
showpat.Text=��ע�Ỽ��
showpat.Tip=��ע�Ỽ��
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onNewPat
showpat.pic=patlist.gif
