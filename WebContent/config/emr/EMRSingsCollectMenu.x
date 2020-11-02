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
UI.button=query;|;close

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

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


