 #
  # Title: ѪҺ���
  #
  # Description: ѪҺ���
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.09.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=save;|;delete;|;clear;|;history;|;bloodNo;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;clear;|;history;|;bloodNo;|;close

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
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
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

bloodNo.Type=TMenuItem
bloodNo.Text=��Ѫ���Ų�ѯ
bloodNo.Tip=��Ѫ���Ų�ѯ
bloodNo.M=P
bloodNo.Action=onBloodNoQuery
bloodNo.pic=query.gif

history.Type=TMenuItem
history.Text=��ʷ��¼
history.Tip=��ʷ��¼
history.M=P
history.Action=onHistoryQuery
history.pic=patlist.gif