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
UI.button=print;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh


File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=print;|;close

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=S
print.key=Ctrl+S
print.Action=onPrint
print.pic=Undo.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif