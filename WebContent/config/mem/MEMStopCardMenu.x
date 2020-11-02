 #
  # Title:会员卡停卡
  #
  # Description: 会员卡停卡
  #
  # Copyright: 
  #
  # @author huangtt 20140424
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;read;|;revoke;|;reprint;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;read;|;revoke;|;reprint;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

read.Type=TMenuItem
read.Text=医疗卡
read.Tip=医疗卡(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

revoke.Type=TMenuItem
revoke.Text=撤消停卡
revoke.Tip=撤消停卡
revoke.M=P
revoke.key=Ctrl+P
revoke.Action=onRevoke
revoke.pic=030.gif

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

reprint.Type=TMenuItem
reprint.Text=补印
reprint.Tip=补印
reprint.M=P
reprint.key=Ctrl+P
reprint.Action=onRePrint
reprint.pic=print_red.gif
