 #
  # Title:��Ʒ���ۿ�
  #
  # Description: ��Ʒ���ۿ�
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;query;|;delete;|;clear;|;MEMprint;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;new;|;query;|;delete;|;clear;|;MEMprint;|;close


save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=D
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

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


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif