 #
  # Title: ҽ�ƿ�����ѯ
  #
  # Description: ҽ�ƿ�����ѯ
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangp 2011.12.26
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;card;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;card;|;clear;|;close

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

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

card.Type=TMenuItem
card.Text=���
card.Tip=���
card.M=D
card.key=
card.Action=onExport
card.pic=exportexcel.gif