<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;EKT;clear;|;code;elecCaseHistory;queryDrug;checkDrugHand;pasterSwab;|;bingli|;call;|;close

//dispenseDrugList;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;queryDrug;checkDrugHand;pasterSwab;|;clear;|;call;|;close

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

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

EKT.Type=TMenuItem
EKT.Text=ҽ�ƿ�
EKT.Tip=ҽ�ƿ�
EKT.key=Ctrl+K
EKT.Action=onEKT
EKT.pic=042.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

//code.Type=TMenuItem
//code.Text=ȡҩ������
//code.Tip=ȡҩ������
//code.M=CO
//code.key=Ctrl+H
//code.Action=onCode
//code.pic=barcode.gif

elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=���Ӳ���
elecCaseHistory.Tip=���Ӳ���
//elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=emr.gif

queryDrug.Type=TMenuItem
queryDrug.Text=ҩƷ��Ϣ
queryDrug.Tip=ҩƷ��Ϣ
//queryDrug.M=QD
queryDrug.Action=queryDrug
queryDrug.pic=sta-4.gif

checkDrugHand.Type=TMenuItem
checkDrugHand.Text=������ҩ
checkDrugHand.Tip=������ҩ
//checkDrugHand.M=CD
checkDrugHand.Action=checkDrugHand
checkDrugHand.pic=051.gif

pasterSwab.Type=TMenuItem
pasterSwab.Text=��ӡҩǩ
pasterSwab.Tip=��ӡҩǩ
//pasterSwab.M=PAS
//pasterSwab.key=Ctrl+P
pasterSwab.Action=onPasterSwab
pasterSwab.pic=print-2.gif

dispenseDrugList.Type=TMenuItem
dispenseDrugList.Text=��ҩ��
dispenseDrugList.Tip=��ҩ��
//dispenseDrugList.M=DD
dispenseDrugList.Action=onDispenseDrugList
dispenseDrugList.pic=print.gif

//pasterSwab.Type=TMenuItem
//pasterSwab.Text=��ӡҩǩ
//pasterSwab.Tip=��ӡҩǩ
//pasterSwab.M=PAS
//pasterSwab.Action=onPasterSwab
//pasterSwab.pic=print-2.gif


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


call.Type=TMenuItem
call.Text=�к�
call.Tip=�к�
//call.M=CA
call.Action=onCall
call.pic=tel.gif