 #
  # Title: ҽ�ƿ����׼�¼
  #
  # Description: ҽ�ƿ����׼�¼
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2010.09.16
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=download1;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=download1;|;clear;|;close

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

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
execrpt.M=E
export.Action=onExport
export.pic=export.gif

download1.Type=TMenuItem
download1.Text=����
download1.Tip=����
download1.M=E
download1.Action=onDownload
download1.pic=030.gif
