<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;EKT;|;ATC;|;clear;|;unDispense;|;dispense;|;dispenseCtrl;|;barCode;pasterSwab;|;onPrint;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.key=D
delete.Action=onDelete
delete.pic=delete.gif

barCode.Type=TMenuItem
barCode.Text=ҩƷ����
barCode.Tip=ҩƷ����
barCode.Action=GeneratPhaBarcode
barCode.pic=PHL.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

ATC.Type=TMenuItem
ATC.Text=�Ͱ�ҩ��
ATC.Tip=�Ͱ�ҩ��
//ATC.M=A
ATC.key=Ctrl+G
ATC.Action=onGenATCFile
ATC.pic=nurse.gif

unDispense.Type=TMenuItem
unDispense.Text=ͳҩ��
unDispense.Tip=ͳҩ��
unDispense.Action=onUnDispense
unDispense.pic=pharm.GIF

dispense.Type=TMenuItem
dispense.Text=��ҩȷ�ϵ�
dispense.Tip=��ҩȷ�ϵ�
dispense.Action=onDispenseSheet
dispense.pic=inwimg.gif


pasterBottle.Type=TMenuItem
pasterBottle.Text=��ӡ��ֽ
pasterBottle.Tip=��ӡ��ֽ
pasterBottle.Action=onPrintPasterBottle
pasterBottle.pic=048.gif

EKT.Type=TMenuItem
EKT.Text=��ҽ�ƿ�
EKT.Tip=��ҽ�ƿ�
//EKT.M=E
EKT.key=Ctrl+K
EKT.Action=onEKT
EKT.pic=042.gif

dispenseCtrl.Type=TMenuItem
dispenseCtrl.Text=�龫��ҩȷ�ϵ�
dispenseCtrl.Tip=�龫��ҩȷ�ϵ�
dispenseCtrl.Action=onPrintCtrlDispenseSheet
dispenseCtrl.pic=bank.gif


pasterSwab.Type=TMenuItem
pasterSwab.Text=�ڷ�����ǩ
pasterSwab.Tip=�ڷ�����ǩ
//pasterSwab.M=PAS
pasterSwab.Action=onPrintSwab
pasterSwab.pic=print-2.gif


onPrint.Type=TMenuItem
onPrint.Text=ע����Һǩ
onPrint.Tip=ע����Һǩ
//onPrint.M=PAS   
onPrint.Action=onPrintPasterBottle
onPrint.pic=print-2.gif
