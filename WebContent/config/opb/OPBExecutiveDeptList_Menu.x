##############################################
# <p>Title:ִ�п���ͳ�Ʊ� </p>
#
# <p>Description:ִ�п���ͳ�Ʊ� </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2010-4-9
# @version 1.0
##############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;detial;|;export;|;clear;|;print;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;detial;|;export;|;print;|;clear;|;close;|;Refresh

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

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
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

preview.Type=TMenuItem
preview.Text=Ԥ��
preview.Tip=Ԥ��
preview.M=R
preview.key=Ctrl+R
preview.Action=onPreview
preview.pic=Preview1.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

detial.Type=TMenuItem
detial.Text=��ϸ
detial.Tip=��ϸ
detial.M=D
detial.key=Ctrl+D
detial.Action=onDetial
detial.pic=detail.gif

