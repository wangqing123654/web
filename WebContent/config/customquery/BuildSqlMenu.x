 #
  # Title: ������ѯSQL
  #
  # Description:������ѯSQL
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=set;delete;clear;close


delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=Q
delete.key=Ctrl+X
delete.Action=onDelete
delete.pic=delete.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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
