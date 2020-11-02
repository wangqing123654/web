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
UI.button=sendBack;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=sendBack;|;query;|;clear;|;close

sendBack.Type=TMenuItem
sendBack.Text=传回
sendBack.Tip=传回
sendBack.M=
sendBack.key=
sendBack.Action=onSendBack
sendBack.pic=Undo.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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


