<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;EKT;|;clear;|;elecCaseHistory;queryDrug;checkDrugHand;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;|;clear;|;elecCaseHistory;queryDrug;checkDrugHand;|;close

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

delete.Type=TMenuItem
delete.Text=取消审核
delete.Tip=取消审核
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

EKT.Type=TMenuItem
EKT.Text=读医疗卡
EKT.Tip=读医疗卡
EKT.key=Ctrl+K
EKT.Action=onEKT
EKT.pic=042.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=E
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


elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=电子病历
elecCaseHistory.Tip=电子病历
//elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=012.gif

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
