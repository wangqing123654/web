 #
  # Title: ҽ�ƿ����׼�¼����
  #
  # Description: ҽ�ƿ����׼�¼����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangp 2011.12.20
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=EKTprint;|;query;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=EKTprint;|;close

EKTprint.Type=TMenuItem
EKTprint.Text=��ӡ
EKTprint.Tip=��ӡ(Ctrl+P)
EKTprint.M=P
EKTprint.key=Ctrl+P
EKTprint.Action=onPrint
EKTprint.pic=print.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Y
query.key=
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
execrpt.M=E
export.Action=onExport
export.pic=export.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif