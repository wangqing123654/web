 #
  # Title: ���ʲ�����
  #
  # Description:���ʲ�����
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author wangzl 2013.01.05
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;unlock;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;unlock;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

unlock.Type=TMenuItem
unlock.Text=���ν���
unlock.Tip=���ν���
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
