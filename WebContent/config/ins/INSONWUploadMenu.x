<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;upload;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=print;query;upload;|;clear;|;close

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=S
print.key=Ctrl+S
print.Action=onPrint
print.pic=print.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

upload.Type=TMenuItem
upload.Text=�ϴ�
upload.Tip=�ϴ�
upload.M=I
upload.key=Ctrl+U
upload.Action=onUpload
upload.pic=CommitOne.gif

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
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

