<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;cancel;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;cancel;|;clear;|;close


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

cancel.Type=TMenuItem
cancel.Text=ȡ���걨
cancel.Tip=ȡ���걨
cancel.M=R
cancel.key=Ctrl+C
cancel.Action=onCancel
cancel.pic=emr-1.gif



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

