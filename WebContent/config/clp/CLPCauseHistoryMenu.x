 #
  # Title: �ٴ�·��������ԭ��
  #
  # Description: �ٴ�·��������ԭ��
  #
  # Copyright: bluecore (c) 2015
  #
  # @author pangb 20150810
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;execl;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;execl;|;clear;|;close

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

execl.Type=TMenuItem
execl.Text=����Excel
execl.Tip=����Excel
execl.M=
execl.key=
execl.Action=onExport
execl.pic=export.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

preview.Type=TMenuItem
preview.Text=�ٴ�·��ģ��
preview.Tip=�ٴ�·��ģ��(Ctrl+P)
preview.M=P
preview.key=Ctrl+P
preview.Action=onPreview
preview.pic=025.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif