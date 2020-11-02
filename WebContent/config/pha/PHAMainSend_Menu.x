<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;EKT;clear;|;call;arrive;|;elecCaseHistory;|;bingli;|;sendbox;|;pasterSwab;|;onInfusion;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;EKT;|;clear;|;onInfusion;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

EKT.Type=TMenuItem
EKT.Text=医疗卡
EKT.Tip=医疗卡
EKT.key=Ctrl+K
EKT.Action=onEKT
EKT.pic=042.gif


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

call.Type=TMenuItem
call.Text=叫号
call.Tip=叫号
//call.M=CA
call.Action=onCall
call.pic=tel.gif

elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=电子病历
elecCaseHistory.Tip=电子病历
//elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=emr.gif

paster.Type=TMenuItem
paster.Text=贴纸
paster.Tip=贴纸
//paster.M=PA
paster.Action=onPaster
paster.pic=barcode.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

arrive.Type=TMenuItem
arrive.Text=已领药
arrive.Tip=已领药
//arrive.M=A
//arrive.key=
arrive.Action=onArrive
arrive.pic=017.gif

bingli.Type=TMenuItem
bingli.Text=病历
bingli.Tip=病历
//bingli.M=
//bingli.key=
bingli.Action=onErdSheet
bingli.pic=search.gif

sendbox.Type=TMenuItem
sendbox.Text=发药机发药
sendbox.Tip=发药机发药
//sendbox.M=R
//sendbox.key=F5
sendbox.Action=onSendBoxMachine
sendbox.pic=bank.gif

pasterSwab.Type=TMenuItem  
pasterSwab.Text=口服外用签
pasterSwab.Tip=口服外用签
//pasterSwab.M=PAS
pasterSwab.Action=onPasterSwab
pasterSwab.pic=print-2.gif

onInfusion.Type=TMenuItem
onInfusion.Text=注射输液签
onInfusion.Tip=注射输液签
//onInfusion.M=PAS
onInfusion.Action=onPrintInfusion
onInfusion.pic=print-2.gif

