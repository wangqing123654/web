<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;fill;chnEnfill;|;print;|;save;|;top;|;backReceipt;|;close;|;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=fill;chnEnfill;|;query;|;print;|;save;|;top;|;close

save.Type=TMenuItem
save.Text=��Ʊ
save.Tip=��Ʊ
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

backReceipt.Type=TMenuItem
backReceipt.Text=������ϸ��ѯ
backReceipt.Tip=������ϸ��ѯ
backReceipt.M=
backReceipt.key=
backReceipt.Action=onBackReceipt
backReceipt.pic=detail-1.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

fill.Type=TMenuItem
fill.Text=���ý����嵥
fill.Tip=���ý����嵥
fill.M=F
fill.key=Ctrl+H
fill.Action=onFill
fill.pic=detail.gif

chnEnfill.Type=TMenuItem
chnEnfill.Text=��Ӣ�ķ����嵥
chnEnfill.Tip=��Ӣ�ķ����嵥
chnEnfill.M=E
chnEnfill.key=Ctrl+E
chnEnfill.Action=onChnEnFill
chnEnfill.pic=patlist.gif

print.Type=TMenuItem
print.Text=����
print.Tip=Ʊ�ݲ���
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=print_red.gif

top.Type=TMenuItem
top.Text=ҽ�ƿ���ֵ
top.Tip=ҽ�ƿ���ֵ
top.M=T
top.key=Ctrl+T
top.Action=onTop
top.pic=bill.gif


