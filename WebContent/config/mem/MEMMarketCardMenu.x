 #
  # Title: 医疗卡充值操作
  #
  # Description: 医疗卡充值操作
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;read;|;showpat;|;MEMprint;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;read;|;showpat;|;MEMprint;|;clear;|;close

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

MEMprint.Type=TMenuItem
MEMprint.Text=补印
MEMprint.Tip=补印
MEMprint.M=P
MEMprint.key=Ctrl+P
MEMprint.Action=onRePrint
MEMprint.pic=print_red.gif

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

showpat.Type=TMenuItem
showpat.Text=新注册患者
showpat.Tip=新注册患者
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onNewPat
showpat.pic=patlist.gif
