 #
  # Title:�����ײ��趨
  #
  # Description:�����ײ��趨
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;new;|;newcopy;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;new;|;newcopy;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=Q
new.key=Ctrl+F
new.Action=onNew
new.pic=new.gif


newcopy.Type=TMenuItem
newcopy.Text=�ײ͸���
newcopy.Tip=�ײ͸���
newcopy.M=Q
newcopy.key=Ctrl+P
newcopy.Action=onNewCopy
newcopy.pic=detail-1.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
