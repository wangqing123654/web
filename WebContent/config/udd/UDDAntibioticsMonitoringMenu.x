 #
  # Title: ����ҩ���ٴ�Ӧ�ü��
  #
  # Description:����ҩ���ٴ�Ӧ�ü��
  #
  # Copyright: JavaHis (c) 2012
  #
  # @author yuanxm
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;clear;|;export;|;close;

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=N
query.key=Query
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=
export.Action=onExport
export.pic=exportexcel.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
