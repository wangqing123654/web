 #
  # Title: ��Ա�������
  #
  # Description: ��Ա�������
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author duzhw 20131224
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


