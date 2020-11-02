<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;EKT;|;ATC;|;clear;|;unDispense;|;dispense;|;dispenseCtrl;|;barCode;pasterSwab;|;onPrint;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.key=D
delete.Action=onDelete
delete.pic=delete.gif

barCode.Type=TMenuItem
barCode.Text=药品条码
barCode.Tip=药品条码
barCode.Action=GeneratPhaBarcode
barCode.pic=PHL.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

ATC.Type=TMenuItem
ATC.Text=送包药机
ATC.Tip=送包药机
//ATC.M=A
ATC.key=Ctrl+G
ATC.Action=onGenATCFile
ATC.pic=nurse.gif

unDispense.Type=TMenuItem
unDispense.Text=统药单
unDispense.Tip=统药单
unDispense.Action=onUnDispense
unDispense.pic=pharm.GIF

dispense.Type=TMenuItem
dispense.Text=配药确认单
dispense.Tip=配药确认单
dispense.Action=onDispenseSheet
dispense.pic=inwimg.gif


pasterBottle.Type=TMenuItem
pasterBottle.Text=打印贴纸
pasterBottle.Tip=打印贴纸
pasterBottle.Action=onPrintPasterBottle
pasterBottle.pic=048.gif

EKT.Type=TMenuItem
EKT.Text=读医疗卡
EKT.Tip=读医疗卡
//EKT.M=E
EKT.key=Ctrl+K
EKT.Action=onEKT
EKT.pic=042.gif

dispenseCtrl.Type=TMenuItem
dispenseCtrl.Text=麻精配药确认单
dispenseCtrl.Tip=麻精配药确认单
dispenseCtrl.Action=onPrintCtrlDispenseSheet
dispenseCtrl.pic=bank.gif


pasterSwab.Type=TMenuItem
pasterSwab.Text=口服外用签
pasterSwab.Tip=口服外用签
//pasterSwab.M=PAS
pasterSwab.Action=onPrintSwab
pasterSwab.pic=print-2.gif


onPrint.Type=TMenuItem
onPrint.Text=注射输液签
onPrint.Tip=注射输液签
//onPrint.M=PAS   
onPrint.Action=onPrintPasterBottle
onPrint.pic=print-2.gif
