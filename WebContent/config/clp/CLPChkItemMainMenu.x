 #
  # Title: �Ž��� �ؼ�������Ŀ
  #
  # Description: �Ž��� �ؼ�������Ŀ
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author Zhangjg 2011.04.11
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;clear;preview;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;query;clear;preview;|;close

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

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

preview.Type=TMenuItem
preview.Text=�ٴ�·��ģ��
preview.Tip=�ٴ�·��ģ��(Ctrl+P)
preview.M=P
preview.key=Ctrl+P
preview.Action=onPreview
preview.pic=025.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif