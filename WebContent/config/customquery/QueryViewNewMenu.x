 #
  # Title: 自定义查询
  #
  # Description:自定义查询
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=export;clear;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.Action=onExport
export.pic=export.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=R
clear.key=F5
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
