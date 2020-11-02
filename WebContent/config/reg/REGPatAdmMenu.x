# 
#  Title:挂号主档
# 
#  Description:挂号主档
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=unreg;arrive;print;Wrist;clear|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=savePat;fee;unreg;arrive;Refresh;print;Wrist;clear;|;close

unreg.Type=TMenuItem
unreg.Text=退挂
unreg.Tip=退挂
unreg.M=U
unreg.key=Ctrl+U
unreg.Action=onUnReg
unreg.pic=030.gif

arrive.Type=TMenuItem
arrive.Text=报到
arrive.Tip=报到
arrive.M=A
arrive.key=Ctrl+A
arrive.Action=onArrive
arrive.pic=017.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

print.Type=TMenuItem
print.Text=补印
print.Tip=补印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Wrist.Type=TMenuItem
Wrist.Text=腕带
Wrist.Tip=腕带
Wrist.M=W
Wrist.key=Ctrl+W
Wrist.Action=onWrist
Wrist.pic=print-1.gif

savePat.Type=TMenuItem
savePat.Text=病患信息保存
savePat.zhText=病患信息保存
savePat.enText=Save
savePat.Tip=病患信息保存
savePat.zhTip=病患信息保存
savePat.enTip=Save
savePat.M=S
savePat.key=Ctrl+S
savePat.Action=onSavePat
savePat.pic=save.gif

fee.Type=TMenuItem
fee.Text=挂号收费
fee.zhText=挂号收费
fee.enText=Fee
fee.Tip=挂号收费
fee.zhTip=挂号收费
fee.enTip=Fee
fee.M=F
fee.key=Ctrl+F
fee.Action=onSaveReg
fee.pic=openbill-2.gif

