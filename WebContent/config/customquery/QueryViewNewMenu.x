 #
  # Title: �Զ����ѯ
  #
  # Description:�Զ����ѯ
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
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
File.Item=export;clear;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.Action=onExport
export.pic=export.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=R
clear.key=F5
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
