#############################################
# <p>Title:�����ų̲�ѯMenu </p>
#
# <p>Description:�����ų̲�ѯMenu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.12.09
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;export2;|;info;|;opRecord;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;info;opRecord;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

info.Type=TMenuItem
info.Text=������ϸ����
info.Tip=������ϸ����
info.Action=onInfo
info.pic=spreadout.gif

opRecord.Type=TMenuItem
opRecord.Text=������¼
opRecord.Tip=������¼
opRecord.Action=onOpRecord
opRecord.pic=031.gif

export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

export2.Type=TMenuItem
export2.Text=JCI�˲����ӡ
export2.Tip=JCI�˲����ӡ
export2.M=
export2.key=
export2.Action=onExport2
export2.pic=print.gif
