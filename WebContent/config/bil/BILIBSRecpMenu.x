<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;clear;|;EKTcard;|;insureInfo;|;outHosp;|;upLoad;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;clear;EKTcard;|;outHosp;upLoad;|;Refresh;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif



clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

outHosp.Type=TMenuItem
outHosp.Text=再次出院
outHosp.Tip=再次出院
outHosp.M=P
outHosp.Action=onOutHosp
outHosp.pic=tempsave.gif

print.Type=TMenuItem
print.Text=自定义打印
print.Tip=自定义打印
print.M=B
print.Action=onPrint
print.pic=print.gif

insureInfo.Type=TMenuItem
insureInfo.Text=病患保险信息
insureInfo.Tip=病患保险信息
insureInfo.M=
insureInfo.key=Ctrl+F
insureInfo.Action=onInsureInfo
insureInfo.pic=013.gif
 
upLoad.Type=TMenuItem
upLoad.Text=医保申报
upLoad.Tip=医保申报
upLoad.M=U
upLoad.Action=onInsUpload
upLoad.pic=032.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
//close.Action=onClosePanel
close.Action=onClose
close.pic=close.gif

EKTcard.Type=TMenuItem
EKTcard.Text=医疗卡
EKTcard.Tip=医疗卡
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

