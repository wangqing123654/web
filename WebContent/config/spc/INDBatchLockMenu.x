 #
  # Title: ���ν�����
  #
  # Description:���ν���
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.20
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=unlock;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=unlock;|;close

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

unlock.Type=TMenuItem
unlock.Text=����
unlock.Tip=����
unlock.M=X
unlock.Action=onUnLock
unlock.pic=unlock.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
