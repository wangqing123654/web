# 
#  Title:��ҽ����Menu
# 
#  Description:��ҽ����Menu
# 
#  Copyright: Copyright (c) Javahis 2009
# 
#  author WangM 2009.05.26
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;print;clear;close


save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

readCard.Type=TMenuItem
readCard.Text=����
readCard.Tip=����(Ctrl+R)
readCard.M=R
readCard.key=Ctrl+R
readCard.Action=onreadCard
readCard.pic=042.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=����EXECL
print.Tip=����EXECL
print.M=P
print.Action=onExecl
print.pic=export.gif