 #
  # Title: ҽ�����ϲ�ѯ
  #
  # Description: ҽ�����ϲ�ѯ
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lim 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;EKTprint1;|;EKTprint2;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;EKTprint1;|;EKTprint2;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.Action=onQuery
query.pic=query.gif

EKTprint1.Type=TMenuItem
EKTprint1.Text=�ʸ�ȷ�����ӡ
EKTprint1.Tip=�ʸ�ȷ�����ӡ
EKTprint1.M=P
EKTprint1.key=Ctrl+P
EKTprint1.Action=onQualificationPrint
EKTprint1.pic=print.gif

EKTprint2.Type=TMenuItem
EKTprint2.Text=���ű��ӡ
EKTprint2.Tip=���ű��ӡ
EKTprint2.M=P
EKTprint2.key=Ctrl+P
EKTprint2.Action=onPrint2
EKTprint2.pic=print.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
execrpt.M=E
export.Action=onExport
export.pic=export.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
