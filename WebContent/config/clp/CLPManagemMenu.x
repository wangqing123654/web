 #
  # Title: �ٴ�·��׼��׼��
  #
  # Description: �ٴ�·��׼��׼��
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author luhai 2010.05.09
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;add;|;change;|;timeInterval;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;add;|;change;|;timeInterval;|;close

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

add.Type=TMenuItem
add.Text=����
add.Tip=����(Ctrl+N)
add.M=A
add.key=Ctrl+N
add.Action=onAdd
add.pic=039.gif

change.Type=TMenuItem
change.Text=����ٴ�·��
change.Tip=����ٴ�·��
change.M=A
change.key=
change.Action=onChange
change.pic=026.gif

//timeInterval.Type=TMenuItem
//timeInterval.Text=ʵ��ʱ���趨
//timeInterval.Tip=ʵ��ʱ���趨
//timeInterval.M=A
//timeInterval.key=
//timeInterval.Action=durationConfig
//timeInterval.pic=spreadout.gif

timeInterval.Type=TMenuItem
timeInterval.Text=·��չ��
timeInterval.Tip=·��չ��
timeInterval.M=A
timeInterval.key=
timeInterval.Action=durationConfig
timeInterval.pic=spreadout.gif

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