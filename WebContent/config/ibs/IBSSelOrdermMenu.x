<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;print;|;Refresh;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;export;|;print;|;Refresh;|;close

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



//clear.Type=TMenuItem
//clear.Text=���
//clear.Tip=���
//clear.M=C
//clear.Action=onClear
//clear.pic=clear.gif


export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=F4
export.Action=onExport
export.pic=export.gif


print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

showpat.Type=TMenuItem
showpat.Text=�˷�
showpat.Tip=�˷�
showpat.M=P
showpat.Action=onShowPat
showpat.pic=patlist.gif

bedcard.Type=TMenuItem
bedcard.Text=����
bedcard.Tip=����
bedcard.M=B
bedcard.Action=onBedCard
bedcard.pic=bedcard.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
//close.Action=onClose
close.pic=close.gif

