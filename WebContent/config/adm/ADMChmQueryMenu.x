##################################################
# <p>Title:��̬��¼��ѯMenu </p>
#
# <p>Description:��̬��¼��ѯMenu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangk 2009-08-14
# @version 4.0
##################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=S
query.key=Ctrl+F
query.Action=onBedTable
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=��ѯ
clear.Tip=��ѯ
clear.M=C
clear.key=Ctrl+Z
clear.Action=onBedTable
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif