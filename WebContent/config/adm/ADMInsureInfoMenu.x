#################################################
# <p>Title:������ѯMenu </p>
#
# <p>Description:������ѯMenu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY 2009.04.30
# @version 1.0
#################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh


File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Undo.gif

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
close.Action=onClose
close.pic=close.gif