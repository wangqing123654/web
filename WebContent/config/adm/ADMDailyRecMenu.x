##################################################
# <p>Title:���ռ�¼�� </p>
#
# <p>Description: </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: </p>
#
# @author JiaoY
# @version 1.0
##################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;Refresh;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;Refresh;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=checkSave
save.pic=save.gif

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

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif