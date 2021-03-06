#################################################
# <p>Title:病患查询Menu </p>
#
# <p>Description:病患查询Menu </p>
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
Window.Text=窗口
Window.M=W
Window.Item=Refresh


File.Type=TMenu
File.Text=文件
File.M=F
File.Item=print;|;close

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=S
print.key=Ctrl+S
print.Action=onPrint
print.pic=Undo.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif