<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;print;toExcel;|;close

Window.Type=TMenu
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=query;clear;print;toExcel;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.zhText=��ѯ
query.enText=Query
query.Tip=��ѯ
query.zhTip=��ѯ
query.enTip=Query
query.M=S
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Clear
clear.Tip=���
clear.zhTip=���
clear.enTip=Clear
clear.M=S
clear.key=F5
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=��ӡ
print.zhText=��ӡ
print.enText=Print
print.Tip=��ӡ
print.zhTip=��ӡ
print.enTip=Print
print.M=S
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

toExcel.Type=TMenuItem
toExcel.Text=���EXCEL
toExcel.zhText=���EXCEL
toExcel.enText=Expt to Excel
toExcel.Tip=���EXCEL
toExcel.zhTip=���EXCEL
toExcel.enTip=Expt to Excel
toExcel.M=S
toExcel.key=Ctrl+E
toExcel.Action=onExcel
toExcel.pic=export.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif