# 
#  Title:账单审核
# 
#  Description:账单审核
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2010.06.11
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ibsRecp;|;newBill;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=Refresh;|;save;|;query;|;ibsRecp;|;newBill;|;clear;|;close

save.Type=TMenuItem
save.Text=审核
save.Tip=审核
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

ibsRecp.Type=TMenuItem
ibsRecp.Text=缴费作业
ibsRecp.Tip=缴费作业
ibsRecp.M=R
ibsRecp.key=
ibsRecp.Action=onBilIBSRecp
ibsRecp.pic=bank.gif

newBill.Type=TMenuItem
newBill.Text=账单调整
newBill.Tip=账单调整
newBill.M=S
newBill.key=
newBill.Action=onNewBill
newBill.pic=Create.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif