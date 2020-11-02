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
UI.button=;save;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=;save;|;close

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

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=Q
save.key=Ctrl+F
save.Action=onSave
save.pic=save.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

