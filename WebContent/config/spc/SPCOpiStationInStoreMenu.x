 #
  # Title: �龫��ҩ�����ܹ�Menu
  #
  # Description:�龫��ҩ�����ܹ�Menu
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author shendr 2013.08.07
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;open;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;clear;|;open;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


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

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onCloseCabinet
close.pic=close.gif


open.Type=TMenuItem
open.Text=���ܹ��ѯ
open.Tip=���ܹ��ѯ
open.M=C
open.Action=onSearch
open.pic=search-2.gif