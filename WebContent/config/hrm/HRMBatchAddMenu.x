 #
  # Title:������������ɾ����ҽ��
  #
  # Description:HRM����������ɾ����ҽ��
  #
  # Copyright: JavaHis (c) 2012
  #
  # @author Yuanxm
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delTableRow;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delTableRow;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delTableRow.Type=TMenuItem
delTableRow.Text=ɾ��ҽ��
delTableRow.zhText=ɾ��ҽ��
delTableRow.enText=Delete
delTableRow.Tip=ɾ��ҽ��
delTableRow.zhTip=ɾ��ҽ��
delTableRow.enTip=Delete
delTableRow.M=D
delTableRow.Action=onDelRow
delTableRow.pic=delete.gif

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


