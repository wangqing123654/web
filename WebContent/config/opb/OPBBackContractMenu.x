<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;fill;|;save;|;close;|;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=fill;|;query;|;save;|;close

save.Type=TMenuItem
save.Text=�˷�
save.Tip=�˷�
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Undo.gif

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
fill.pic=detail.gif

print.Type=TMenuItem
print.Text=����
print.Tip=Ʊ�ݲ���
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=print_red.gif

