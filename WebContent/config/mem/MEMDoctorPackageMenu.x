 #
  # Title: �ײ͹����ѯ
  #
  # Description: �ײ͹����ѯ
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author duzhw 2014.02.21
 # @version 4.5
<Type=TMenuBar>
UI.Item=File;Window
UI.button=sendBack;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=sendBack;|;query;|;clear;|;close

sendBack.Type=TMenuItem
sendBack.Text=����
sendBack.Tip=����
sendBack.M=
sendBack.key=
sendBack.Action=onSendBack
sendBack.pic=Undo.gif

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

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


