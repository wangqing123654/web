 #
  # Title: �Զ����ѯ
  #
  # Description:�Զ����ѯ
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=Insert;|;Delete;|;Clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=Insert;Delete;Clear;close

set.Type=TMenuItem
set.Text=������ѯ
set.Tip=������ѯ
set.M=S
set.key=F2
set.Action=onInsert
set.pic=new.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=Q
delete.key=Ctrl+X
delete.Action=onDelete
delete.pic=delete.gif



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=R
clear.key=F5
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
