 #
  # Title: ���뵥�Ų�ѯ
  #
  # Description:���뵥�Ų�ѯ
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-05-05
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=new;|;return;|;close

Window.Type=TMenu
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=new;|;return;|;close

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�(Alt+F4)
close.zhTip=�˳�(Alt+F4)
close.enTip=Quit(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

return.Type=TMenuItem
return.Text=����
return.zhText=����
return.enText=Retrieve
return.Tip=����
return.zhTip=����
return.enTip=Retrieve
return.M=R
return.Action=onReturn
return.pic=054.gif

new.Type=TMenuItem
new.Text=�½�
new.zhText=�½�
new.enText=New
new.Tip=�½�(Ctrl+N)
new.zhTip=�½�(Ctrl+N)
new.enTip=New(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif



