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
UI.button=query;|;EKTprint;|;clear;|;card;|;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;card;|;export;|;close


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

card.Type=TMenuItem
card.Text=ҽ�ƿ�
card.Tip=ҽ�ƿ�
card.M=D
card.key=
card.Action=onCardNoAction
card.pic=042.gif

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

export.Type=TMenuItem
export.Text=���
export.Tip=���
execrpt.M=E
export.Action=onExport
export.pic=export.gif

EKTprint.Type=TMenuItem
EKTprint.Text=��ӡ
EKTprint.Tip=��ӡ
EKTprint.M=P
EKTprint.key=Ctrl+P
EKTprint.Action=onPrint
EKTprint.pic=print.gif