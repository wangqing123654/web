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
UI.button=download1;|;download2;|;download3;|;download4;|;export;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;card;|;export;|;close

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
download1.Text=�ܸ�����
download1.Tip=�ܸ�����
download1.M=E
download1.Action=onDownload|1
download1.pic=030.gif

download2.Type=TMenuItem
download2.Text=��֧����
download2.Tip=��֧����
download2.M=E
download2.Action=onDownload|2
download2.pic=025.gif

download3.Type=TMenuItem
download3.Text=��֧��������
download3.Tip=��֧��������
download3.M=E
download3.Action=onDownload|3
download3.pic=018.gif

download4.Type=TMenuItem
download4.Text=�ܸ���ϸ����
download4.Tip=�ܸ���ϸ����
download4.M=E
download4.Action=onDownload|4
download4.pic=detail.gif