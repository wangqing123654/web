 #
 # Title: ��������ҽ������
 #
 # Description: ��������ҽ������
 #
 # Copyright: BlueCore (c) 2012
 #
 # @author WangLong 2012.09.21
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;card;|;clear;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;card;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

card.Type=TMenuItem
card.Text=����
card.Tip=����
card.M=D
card.key=
card.Action=onReadEKT
card.pic=042.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.Action=onQuery
query.pic=query.gif

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

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif