<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;EKT;clear;|;code;elecCaseHistory;queryDrug;checkDrugHand;pasterSwab;|;bingli|;call;|;close

//dispenseDrugList;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;queryDrug;checkDrugHand;pasterSwab;|;clear;|;call;|;close

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

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

EKT.Type=TMenuItem
EKT.Text=医疗卡
EKT.Tip=医疗卡
EKT.key=Ctrl+K
EKT.Action=onEKT
EKT.pic=042.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

//code.Type=TMenuItem
//code.Text=取药号码牌
//code.Tip=取药号码牌
//code.M=CO
//code.key=Ctrl+H
//code.Action=onCode
//code.pic=barcode.gif

elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=电子病历
elecCaseHistory.Tip=电子病历
//elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=emr.gif

queryDrug.Type=TMenuItem
queryDrug.Text=药品信息
queryDrug.Tip=药品信息
//queryDrug.M=QD
queryDrug.Action=queryDrug
queryDrug.pic=sta-4.gif

checkDrugHand.Type=TMenuItem
checkDrugHand.Text=合理用药
checkDrugHand.Tip=合理用药
//checkDrugHand.M=CD
checkDrugHand.Action=checkDrugHand
checkDrugHand.pic=051.gif

pasterSwab.Type=TMenuItem
pasterSwab.Text=打印药签
pasterSwab.Tip=打印药签
//pasterSwab.M=PAS
//pasterSwab.key=Ctrl+P
pasterSwab.Action=onPasterSwab
pasterSwab.pic=print-2.gif

dispenseDrugList.Type=TMenuItem
dispenseDrugList.Text=配药单
dispenseDrugList.Tip=配药单
//dispenseDrugList.M=DD
dispenseDrugList.Action=onDispenseDrugList
dispenseDrugList.pic=print.gif

//pasterSwab.Type=TMenuItem
//pasterSwab.Text=打印药签
//pasterSwab.Tip=打印药签
//pasterSwab.M=PAS
//pasterSwab.Action=onPasterSwab
//pasterSwab.pic=print-2.gif


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


call.Type=TMenuItem
call.Text=叫号
call.Tip=叫号
//call.M=CA
call.Action=onCall
call.pic=tel.gif