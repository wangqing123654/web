 #
  # Title: �Ƚ�IND_VERIFYIND��IND_ACCOUNT
  #
  # Description:�Ƚ�IND_VERIFYIND��IND_ACCOUNT
  #
  # Copyright: JavaHis (c) 2013
  #
  # @author shendr 2013-06-27
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

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)   
close.M=X
close.key=Alt+F4
close.Action=onClose   
close.pic=close.gif
