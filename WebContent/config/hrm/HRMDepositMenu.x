 #
  # Title: �������
  #
  # Description:�������
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;print;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ȡ������
delete.Tip=ȡ������
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

print.Type=TMenuItem
print.Text=��ӡ�˵���ϸ
print.Tip=��ӡ�˵���ϸ
print.M=S
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.GIF

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
