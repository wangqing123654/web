#############################################
# <p>Title:���ﻤʿվ�����鱨�����Menu </p>
#
# <p>Description:���ﻤʿվ�����鱨�����Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2010.02.02
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File
UI.button=query;|;preview;|;print;|;clear;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;close

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

preview.Type=TMenuItem
preview.Text=�Ķ�����
preview.Tip=�Ķ�����
preview.M=R
preview.key=Ctrl+R
preview.Action=onTableDoubleCliecked
preview.pic=preview.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=D
clear.key=Ctrl+D
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif