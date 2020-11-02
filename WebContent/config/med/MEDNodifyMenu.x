#
  # Title:
  #
  # Description: 
  #
  # Copyright: BlueCore (c) 
  #
  # @author wanglong 2013.11.12
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=;query;|;update;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=;query;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=Q
clear.key=Ctrl+F
clear.Action=onClear
clear.pic=clear.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+Q)
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

update.Type=TMenuItem
update.Text=全部标记
update.Tip=全部标记
update.M=T
update.key=Ctrl+T
update.Action=onAll
update.pic=tempsave.gif

