<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;save;separate;merge;print;printSmall;clear;|;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu  
File.Text=�ļ�
File.M=F
File.Item=query;|save;|;Refresh;|merge;|;separate;|;print;|;printSmall;|;clear;|;export;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

separate.Type=TMenuItem
separate.Text=�ֲ�
separate.Tip=�ֲ�
separate.M=
separate.key=
separate.Action=onSeparate
separate.pic=012.gif

merge.Type=TMenuItem
merge.Text=�ϲ�
merge.Tip=�ϲ�
merge.M=
merge.key=
merge.Action=onMerge
merge.pic=mro.gif

print.Type=TMenuItem
print.Text=���򰸾���(��)
print.Tip=���򰸾���(��)
print.M=
print.key=
print.Action=onPrint
print.pic=barcode.gif

printSmall.Type=TMenuItem
printSmall.Text=������ǩ
printSmall.Tip=������ǩ
printSmall.M=
printSmall.key=
printSmall.Action=onPrintSmall
printSmall.pic=barcode.gif

export.Type=TMenuItem
export.Text=����Excel
export.Tip=����Excel
export.M=
export.key=
export.Action=onExport
export.pic=exportexcel.gif