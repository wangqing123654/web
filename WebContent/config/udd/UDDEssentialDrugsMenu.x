 #
  # Title: ����ҩ���ֵ�ͳ�Ʊ���
  #
  # Description: ����ҩ���ֵ�ͳ�Ʊ���
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author yanj 2013.11.19
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=
export.Action=onExport
export.pic=exportexcel.gif


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
