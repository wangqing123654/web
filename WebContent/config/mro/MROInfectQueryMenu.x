#############################################
# <p>Title:��Ⱦ�����濨��ѯMenu </p>
#
# <p>Description:��Ⱦ�����濨��ѯMenu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.10.14
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;back;|;new;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;new;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

back.Type=TMenuItem
back.Text=�ش�ѡ��ֵ
back.Tip=�ش�ѡ��ֵ
back.Action=onBack
back.pic=Undo.gif

new.Type=TMenuItem
new.Text=�½����濨
new.Tip=�½����濨
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=Z
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
