 #
  # Title: 套餐购买查询
  #
  # Description: 套餐购买查询
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author duzhw 2014.02.21
 # @version 4.5
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;read;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;read;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

read.Type=TMenuItem
read.Text=医疗卡
read.Tip=医疗卡(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

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


