#############################################
# <p>Title:手术排程Menu </p>
#
# <p>Description:手术排程Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.27
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;asg;opInfo;opRecord;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

asg.Type=TMenuItem
asg.Text=手术排程
asg.Tip=手术排程
asg.Action=onAsg
asg.pic=time.gif

opInfo.Type=TMenuItem
opInfo.Text=手术申请明细
opInfo.Tip=手术申请明细
opInfo.Action=onOpInfo
opInfo.pic=detail-1.gif

opRecord.Type=TMenuItem
opRecord.Text=手术记录
opRecord.Tip=手术记录
opRecord.Action=onOpRecord
opRecord.pic=031.gif