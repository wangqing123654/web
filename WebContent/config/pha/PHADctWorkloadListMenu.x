<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;detial;print;excel;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=Refresh;query;clear;detial;print;excel;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
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

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

excel.Type=TMenuItem
excel.Text=����EXCEL
excel.Tip=����EXCEL
excel.M=E
excel.Action=onExcel
excel.pic=exportexcel.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

detial.Type=TMenuItem
detial.Text=��ϸ
detial.Tip=��ϸ
detial.M=D
detial.key=Ctrl+D
detial.Action=onDetail
detial.pic=detail.gif
