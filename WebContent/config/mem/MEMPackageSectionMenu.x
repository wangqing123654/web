 #
  # Title: 会员种类管理
  #
  # Description: 会员种类管理
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author duzhw 20131224
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;detailquery;|;onFeeRate;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;new;|;delete;|;query;|;detailquery;|;onFeeRate;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

onFeeRate.Type=TMenuItem
onFeeRate.Text=计算折扣
onFeeRate.Tip=计算折扣(Rate)
onFeeRate.M=N
onFeeRate.key=rate
onFeeRate.Action=onFeeRate
onFeeRate.pic=026.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

detailquery.Type=TMenuItem
detailquery.Text=套餐明细查询
detailquery.Tip=套餐明细查询(Ctrl+D)
detailquery.M=D
detailquery.key=Ctrl+D
detailquery.Action=onDetailQuery
detailquery.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


