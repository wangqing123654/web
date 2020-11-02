 #
  # Title: 感染病例登记
  #
  # Description: 感染病例登记
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.22
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;clear;print;print1;report;testrep;temperature;showcase;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;query;clear;print;print1;report;testrep;temperature;showcase;export;|;close

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

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
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

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif

//=====start liling 20140402 添加
print1.Type=TMenuItem
print1.Text=登记表
print1.Tip=登记表
print1.M=P
print1.Action=onPrintINFCase
print1.pic=print.gif
//=====end liling 20140402 添加

report.Type=TMenuItem
report.Text=检验报告
report.Tip=检验报告
report.M=P
report.Action=onReport
report.pic=Lis.gif

testrep.Type=TMenuItem
testrep.Text=检查报告
testrep.Tip=检查报告
testrep.M=
testrep.key=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

temperature.Type=TMenuItem
temperature.Text=体温
temperature.Tip=体温
temperature.M=P
temperature.Action=onTemperature
temperature.pic=Column.gif

showcase.Type=TMenuItem
showcase.Text=病历浏览
showcase.Tip=病历浏览
showcase.M=P
showcase.Action=onShowCase
showcase.pic=043.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif