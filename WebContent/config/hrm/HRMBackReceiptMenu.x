<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;fill;print;reportPrint;disCharge;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;fill;print;|;disCharge;|;close

disCharge.Type=TMenuItem
disCharge.Text=�˷�
disCharge.Tip=�˷�
disCharge.M=S
disCharge.key=Ctrl+S
disCharge.Action=onDisCharge
disCharge.pic=030.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

fill.Type=TMenuItem
fill.Text=�����嵥
fill.Tip=�����嵥
fill.M=F
fill.key=Ctrl+H
fill.Action=onFill
fill.pic=correct.gif

print.Type=TMenuItem
print.Text=����
print.Tip=Ʊ�ݲ���
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=print-1.gif

reportPrint.Type=TMenuItem
reportPrint.Text=������
reportPrint.Tip=������
reportPrint.M=F
reportPrint.key=Ctrl+P
reportPrint.Action=onReportPrint
reportPrint.pic=Print.gif
