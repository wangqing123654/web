<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;EKT;clear;ATC;|;call;elecCaseHistory;paster;dispenseDrugList;pasterSwab|;onInfusion;|;bingli;|;sendbox;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;EKT;ATC;|;clear;|;close

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

ATC.Type=TMenuItem
ATC.Text=送包药机
ATC.Tip=送包药机
ATC.key=Ctrl+G
ATC.Action=onGenATCFile
ATC.pic=nurse.gif

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
paster.Text=配药单
paster.Tip=配药单
//paster.M=PA
paster.key=Ctrl+P
paster.Action=onPaster
paster.pic=print.gif

//dispenseDrugList.Type=TMenuItem
//dispenseDrugList.Text=配药单
//dispenseDrugList.Tip=配药单
//dispenseDrugList.M=DD
//dispenseDrugList.Action=onDispenseDrugList
//dispenseDrugList.pic=print.gif

pasterSwab.Type=TMenuItem
pasterSwab.Text=口服外用签
pasterSwab.Tip=口服外用签
//pasterSwab.M=PAS
pasterSwab.Action=onPasterSwab
pasterSwab.pic=print-2.gif

close.Type=TMenuItem  
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

bingli.Type=TMenuItem
bingli.Text=病历
bingli.Tip=病历
//bingli.M=
//bingli.key=
bingli.Action=onErdSheet
bingli.pic=search.gif


sendbox.Type=TMenuItem
sendbox.Text=送盒装发药机
sendbox.Tip=送盒装发药机
//sendbox.M=A
sendbox.key=Ctrl+G
sendbox.Action=onDispenseBoxMachine
sendbox.pic=bank.gif

test.Type=TMenuItem
test.Text=送盒装包药机
test.Tip=送盒装包药机   
test.M=A
test.key=Ctrl+G
test.Action=onTest
test.pic=bank.gif

onInfusion.Type=TMenuItem
onInfusion.Text=注射输液签  
onInfusion.Tip=注射输液签
//onInfusion.M=PAS
onInfusion.Action=onPrintInfusion
onInfusion.pic=print-2.gif