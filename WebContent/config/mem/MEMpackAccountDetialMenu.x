 #
  # Title: 会员卡套餐结转明细表
  #
  # Description: 会员卡套餐结转明细表
  #
  # Copyright: bluecore
  #
  # @author huangtt 
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;execle;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;print;|;execle;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

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

execle.Type=TMenuItem
execle.Text=导出EXECLE
execle.Tip=导出EXECLE
execle.M=I
execle.key=Ctrl+F
execle.Action=onExport
execle.pic=export.gif