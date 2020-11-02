#
  # Title: 门诊收费
  #
  # Description:门诊收费和补充计价
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ektCard;|;insureinfo;|;queryPack;|;ctzModify;|;CaseHistory;|;delete;|;record;|;backReceipt;|;insCard;|;ektPrint;|;operation;|;fee;|;orderPack;|;Wrist;|;reduceCheck;|;clear;|;close;|;
//去掉 |;insPrint;
Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;delete;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


caseHistory.Type=TMenuItem
caseHistory.Text=就诊记录
caseHistory.Tip=就诊记录
caseHistory.M=C
caseHistory.Action=onCaseHistory
caseHistory.pic=032.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

reduceCheck.Type=TMenuItem
reduceCheck.Text=减免审批
reduceCheck.Tip=减免审批
//backReceipt.M=F
//backReceipt.key=F7
reduceCheck.Action=onReduceCheck
reduceCheck.pic=Retrieve.gif


queryPack.Type=TMenuItem
queryPack.Text=套餐查询
queryPack.Tip=套餐查询
//queryPack.M=T
//queryPack.key=
queryPack.Action=onQueryPack
queryPack.pic=search-1.gif


ctzModify.Type=TMenuItem
ctzModify.Text=身份修改
ctzModify.Tip=身份修改
//ctzModify.M=G
//ctzModify.key=
ctzModify.Action=onCtzModify
ctzModify.pic=007.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=D
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

backReceipt.Type=TMenuItem
backReceipt.Text=费用查询
backReceipt.Tip=费用查询
//backReceipt.M=F
//backReceipt.key=F7
backReceipt.Action=onBackReceipt
backReceipt.pic=detail-1.gif

backContract.Type=TMenuItem
backContract.Text=记账查询
backContract.Tip=记账查询
//backContract.M=J
//backContract.key=F8
backContract.Action=onBackContract
backContract.pic=011.gif

ektCard.Type=TMenuItem
ektCard.Text=医疗卡
ektCard.Tip=读医疗卡
//ektCard.M=
//ektCard.key=F6
ektCard.Action=onEKT
ektCard.pic=042.gif

insCard.Type=TMenuItem
insCard.Text=医保卡
insCard.Tip=读医保卡
//insCard.M=
insCard.Action=readINSCard
insCard.pic=008.gif

//insPrint.Type=TMenuItem
//insPrint.Text=结算打印
//insPrint.Tip=结算打印
//insPrint.M=
//insPrint.Action=exeInsPrint
//insPrint.pic=018.gif

ektPrint.Type=TMenuItem
ektPrint.Text=打票
ektPrint.Tip=打票
//ektPrint.M=
//ektPrint.key=F6
ektPrint.Action=onEKTPrint
ektPrint.pic=print.gif

fee.Type=TMenuItem
fee.Text=医疗卡充值
fee.Tip=医疗卡充值
//fee.M=S
//fee.key=
fee.Action=onFee
fee.pic=bill.gif


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

record.Type=TMenuItem
record.Text=就诊号
record.Tip=就诊号
//record.M=R
//record.key=F5
record.Action=onRecord
record.pic=012.gif

operation.Type=TMenuItem
operation.Text=计费模版
operation.Tip=计费模版
//operation.M=P
operation.Action=onOperation
operation.pic=operation.gif

Wrist.Type=TMenuItem
Wrist.Text=腕带
Wrist.Tip=腕带
//Wrist.M=
//Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif

insureinfo.Type=TMenuItem
insureinfo.Text=保险
insureinfo.Tip=保险
insureinfo.M=
insureinfo.key=
insureinfo.Action=onInsureinfo
insureinfo.pic=038.gif


orderPack.Type=TMenuItem
orderPack.Text=比对
orderPack.Tip=比对
orderPack.M=
orderPack.key=
orderPack.Action=onOrderPackComparison
orderPack.pic=029.gif