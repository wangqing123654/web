##################################################
# <p>Title:������̬�� </p>
#
# <p>Description:������̬�� </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 1.0
##################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh


File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=
query.key=
query.Action=onQuery
query.pic=query.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif