 #
  # Title: 套餐与已开立医嘱比对
  #
  # Description: 套餐与已开立医嘱比对
  #
  # Copyright: bluecore
  #
  # @author huangtt 20141021
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=Q
save.Action=onSave
save.pic=save.gif

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
