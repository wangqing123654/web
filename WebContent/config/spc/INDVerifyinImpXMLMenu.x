 #
  # Title: �������-���������
  #
  # Description:�������-���������
  #
  # Copyright: bluecore (c) 2012
  #
  # @author zhangy 2009-05-06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;return;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=return;|;close



close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

return.Type=TMenuItem
return.Text=����
return.Tip=����
return.M=R
return.Action=onReturn
return.pic=054.gif

