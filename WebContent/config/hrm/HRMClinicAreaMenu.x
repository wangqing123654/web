 #
  # Title: HRM�ײ��趨
  #
  # Description:HRM�ײ��趨
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=Window
UI.button=save;delete;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=

save.Type=TMenuItem
save.Text=�½�����
save.Tip=�½�����
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


newRoom.Type=TMenuItem
newRoom.Text=�½����
newRoom.Tip=�½����
newRoom.M=Q
newRoom.key=Ctrl+F
newRoom.Action=onNewRoom
newRoom.pic=org-1.gif

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
