<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;EKT;clear;ATC;|;call;elecCaseHistory;paster;dispenseDrugList;pasterSwab|;onInfusion;|;bingli;|;sendbox;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;EKT;ATC;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

EKT.Type=TMenuItem
EKT.Text=ҽ�ƿ�
EKT.Tip=ҽ�ƿ�
EKT.key=Ctrl+K
EKT.Action=onEKT
EKT.pic=042.gif

ATC.Type=TMenuItem
ATC.Text=�Ͱ�ҩ��
ATC.Tip=�Ͱ�ҩ��
ATC.key=Ctrl+G
ATC.Action=onGenATCFile
ATC.pic=nurse.gif

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

call.Type=TMenuItem
call.Text=�к�
call.Tip=�к�
//call.M=CA
call.Action=onCall
call.pic=tel.gif

elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=���Ӳ���
elecCaseHistory.Tip=���Ӳ���
//elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=emr.gif

paster.Type=TMenuItem
paster.Text=��ҩ��
paster.Tip=��ҩ��
//paster.M=PA
paster.key=Ctrl+P
paster.Action=onPaster
paster.pic=print.gif

//dispenseDrugList.Type=TMenuItem
//dispenseDrugList.Text=��ҩ��
//dispenseDrugList.Tip=��ҩ��
//dispenseDrugList.M=DD
//dispenseDrugList.Action=onDispenseDrugList
//dispenseDrugList.pic=print.gif

pasterSwab.Type=TMenuItem
pasterSwab.Text=�ڷ�����ǩ
pasterSwab.Tip=�ڷ�����ǩ
//pasterSwab.M=PAS
pasterSwab.Action=onPasterSwab
pasterSwab.pic=print-2.gif

close.Type=TMenuItem  
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

bingli.Type=TMenuItem
bingli.Text=����
bingli.Tip=����
//bingli.M=
//bingli.key=
bingli.Action=onErdSheet
bingli.pic=search.gif


sendbox.Type=TMenuItem
sendbox.Text=�ͺ�װ��ҩ��
sendbox.Tip=�ͺ�װ��ҩ��
//sendbox.M=A
sendbox.key=Ctrl+G
sendbox.Action=onDispenseBoxMachine
sendbox.pic=bank.gif

test.Type=TMenuItem
test.Text=�ͺ�װ��ҩ��
test.Tip=�ͺ�װ��ҩ��   
test.M=A
test.key=Ctrl+G
test.Action=onTest
test.pic=bank.gif

onInfusion.Type=TMenuItem
onInfusion.Text=ע����Һǩ  
onInfusion.Tip=ע����Һǩ
//onInfusion.M=PAS
onInfusion.Action=onPrintInfusion
onInfusion.pic=print-2.gif