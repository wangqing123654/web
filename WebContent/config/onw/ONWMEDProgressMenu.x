#############################################
# <p>Title:���ﻤʿվҽ������Menu </p>
#
# <p>Description:���ﻤʿվҽ������Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.11.24
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File
UI.button=query;|;clear;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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