 #
  # Title: ҽ�ƿ���ֵ����
  #
  # Description: ҽ�ƿ���ֵ����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;checkAll;|;checkDetailAccnt;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;checkAll;|;checkDetailAccnt;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.Action=onQuery
query.pic=query.gif

checkAll.Type=TMenuItem
checkAll.Text=������
checkAll.Tip=������
checkAll.M=E
checkAll.Action=onCheckAll
checkAll.pic=037.gif

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

checkDetailAccnt.Type=TMenuItem
checkDetailAccnt.Text=����ϸ��
checkDetailAccnt.Tip=����ϸ��
checkDetailAccnt.M=E
checkDetailAccnt.Action=onCheckDetailAccnt
checkDetailAccnt.pic=detail-1.gif