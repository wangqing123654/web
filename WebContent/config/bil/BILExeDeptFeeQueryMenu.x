#
  # Title: ����Ʊ��
  #
  # Description:����Ʊ��
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;excel;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;excel;|;clear;|;close

excel.Type=TMenuItem
excel.Text=���Excel
excel.Tip=���Excel
excel.M=S
excel.key=Ctrl+S
excel.Action=onExcel
excel.pic=045.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=R
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

