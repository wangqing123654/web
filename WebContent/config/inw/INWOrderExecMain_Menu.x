<Type=TMenuBar> 
UI.Item=File;Window
UI.button=save;query;clear;|;Newprint;|;longTimePrint;|;code;pasterBottle;|;BloodTransfusion;|;barCode;|;checkrep;|;checkPrint;|;testrep;|;skiResult;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;Refresh;query;|;Newprint;|;longTimePrint;|;code;pasterBottle;|;BloodTransfusion;|;checkrep;|;checkPrint;|;testrep;clear;|;skiResult;|;close

skiResult.Type=TMenuItem
skiResult.Text=皮试结果
skiResult.Tip=皮试结果
skiResult.M=P
skiResult.Action=onSkiResult
skiResult.pic=032.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=分类执行单
print.Tip=分类执行单
print.M=P
print.Action=onPrint
print.pic=print.gif

Newprint.Type=TMenuItem
Newprint.Text=多人打印
Newprint.Tip=多人打印
Newprint.M=PN
Newprint.Action=onPrintExe
Newprint.pic=print-1.gif

temporaryPrint.Type=TMenuItem
temporaryPrint.Text=汇总执行单
temporaryPrint.Tip=汇总执行单
temporaryPrint.M=
temporaryPrint.Action=onTemporaryPrint
temporaryPrint.pic=print.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

code.Type=TMenuItem
code.Text=条码打印
code.Tip=条码打印
code.M=CO
code.Action=onBarCode
code.pic=barcode.gif

paster.Type=TMenuItem
paster.Text=打印贴纸
paster.Tip=打印贴纸
paster.Action=onPrintPaster
paster.pic=048.gif

pasterBottle.Type=TMenuItem
pasterBottle.Text=打印输液签
pasterBottle.Tip=打印输液签
pasterBottle.Action=onPrintPasterBottle
pasterBottle.pic=print-2.gif

BloodTransfusion.Type=TMenuItem
BloodTransfusion.Text=打印输血签
BloodTransfusion.Tip=打印输血签
BloodTransfusion.Action=onPrintBloodTransfusion
BloodTransfusion.pic=modify.gif

barCode.Type=TMenuItem
barCode.Text=药品条码
barCode.Tip=药品条码
barCode.Action=GeneratPhaBarcode
barCode.pic=PHL.gif


checkrep.Type=TMenuItem
checkrep.Text=检验报告
checkrep.Tip=检验报告
checkrep.M=
checkrep.Action=onCheckrep
checkrep.pic=Lis.gif

checkPrint.Type=TMenuItem
checkPrint.Text=检验报告打印
checkPrint.Tip=检验报告打印
checkPrint.M=
checkPrint.Action=onCheckPrint
checkPrint.pic=pha_print.gif

testrep.Type=TMenuItem
testrep.Text=检查报告
testrep.Tip=检查报告
testrep.M=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

//charge.Type=TMenuItem
//charge.Text=补充计费
//charge.Tip=补充计费
//charge.M=H
//charge.key=Ctrl+H
//charge.Action=onCharge
//charge.pic=bill-1.gif

//emr.Type=TMenuItem
//emr.Text=结构化病历
//emr.Tip=结构化病历
//emr.M=J
//emr.key=Ctrl+J
//emr.Action=onEmr
//emr.pic=emr-2.gif

//tpr.Type=TMenuItem
//tpr.Text=体温单
//tpr.Tip=体温单
//tpr.M=J
//tpr.key=Ctrl+T
//tpr.Action=onVitalSign
//tpr.pic=Column.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif