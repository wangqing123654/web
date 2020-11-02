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
UI.button=query;|;bilpay;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh


File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;bilpay;|;print;|;clear;|;close

bilpay.Type=TMenuItem
bilpay.Text=预交金
bilpay.Tip=预交金
bilpay.M=
bilpay.key=
bilpay.Action=onBilpay
bilpay.pic=openbill-2.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=
query.key=
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=住院证打印
print.Tip=住院证打印
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=Z
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif
