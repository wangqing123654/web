 #
  # Title: HRM���Լ����
  #
  # Description:HRM���Լ����
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=Window
UI.button=print;execle;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=print;execle;clear;|;close

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=C
print.Action=onPrint
print.pic=print.gif

execle.Type=TMenuItem
execle.Text=����
execle.Tip=����
execle.M=I
execle.Action=onExecl
execle.pic=export.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
