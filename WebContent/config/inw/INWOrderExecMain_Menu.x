<Type=TMenuBar> 
UI.Item=File;Window
UI.button=save;query;clear;|;Newprint;|;longTimePrint;|;code;pasterBottle;|;BloodTransfusion;|;barCode;|;checkrep;|;checkPrint;|;testrep;|;skiResult;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;Refresh;query;|;Newprint;|;longTimePrint;|;code;pasterBottle;|;BloodTransfusion;|;checkrep;|;checkPrint;|;testrep;clear;|;skiResult;|;close

skiResult.Type=TMenuItem
skiResult.Text=Ƥ�Խ��
skiResult.Tip=Ƥ�Խ��
skiResult.M=P
skiResult.Action=onSkiResult
skiResult.pic=032.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=����ִ�е�
print.Tip=����ִ�е�
print.M=P
print.Action=onPrint
print.pic=print.gif

Newprint.Type=TMenuItem
Newprint.Text=���˴�ӡ
Newprint.Tip=���˴�ӡ
Newprint.M=PN
Newprint.Action=onPrintExe
Newprint.pic=print-1.gif

temporaryPrint.Type=TMenuItem
temporaryPrint.Text=����ִ�е�
temporaryPrint.Tip=����ִ�е�
temporaryPrint.M=
temporaryPrint.Action=onTemporaryPrint
temporaryPrint.pic=print.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

code.Type=TMenuItem
code.Text=�����ӡ
code.Tip=�����ӡ
code.M=CO
code.Action=onBarCode
code.pic=barcode.gif

paster.Type=TMenuItem
paster.Text=��ӡ��ֽ
paster.Tip=��ӡ��ֽ
paster.Action=onPrintPaster
paster.pic=048.gif

pasterBottle.Type=TMenuItem
pasterBottle.Text=��ӡ��Һǩ
pasterBottle.Tip=��ӡ��Һǩ
pasterBottle.Action=onPrintPasterBottle
pasterBottle.pic=print-2.gif

BloodTransfusion.Type=TMenuItem
BloodTransfusion.Text=��ӡ��Ѫǩ
BloodTransfusion.Tip=��ӡ��Ѫǩ
BloodTransfusion.Action=onPrintBloodTransfusion
BloodTransfusion.pic=modify.gif

barCode.Type=TMenuItem
barCode.Text=ҩƷ����
barCode.Tip=ҩƷ����
barCode.Action=GeneratPhaBarcode
barCode.pic=PHL.gif


checkrep.Type=TMenuItem
checkrep.Text=���鱨��
checkrep.Tip=���鱨��
checkrep.M=
checkrep.Action=onCheckrep
checkrep.pic=Lis.gif

checkPrint.Type=TMenuItem
checkPrint.Text=���鱨���ӡ
checkPrint.Tip=���鱨���ӡ
checkPrint.M=
checkPrint.Action=onCheckPrint
checkPrint.pic=pha_print.gif

testrep.Type=TMenuItem
testrep.Text=��鱨��
testrep.Tip=��鱨��
testrep.M=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

//charge.Type=TMenuItem
//charge.Text=����Ʒ�
//charge.Tip=����Ʒ�
//charge.M=H
//charge.key=Ctrl+H
//charge.Action=onCharge
//charge.pic=bill-1.gif

//emr.Type=TMenuItem
//emr.Text=�ṹ������
//emr.Tip=�ṹ������
//emr.M=J
//emr.key=Ctrl+J
//emr.Action=onEmr
//emr.pic=emr-2.gif

//tpr.Type=TMenuItem
//tpr.Text=���µ�
//tpr.Tip=���µ�
//tpr.M=J
//tpr.key=Ctrl+T
//tpr.Action=onVitalSign
//tpr.pic=Column.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif