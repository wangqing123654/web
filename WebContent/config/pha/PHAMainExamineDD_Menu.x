<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;|;paster;code;elecCaseHistory;queryDrug;checkDrugHand;|;close

//dispenseDrugList;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;paster;queryDrug;checkDrugHand;|;clear;|;close

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

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

//code.Type=TMenuItem
//code.Text=ȡҩ������
//code.Tip=ȡҩ������
//code.M=CO
//code.key=Ctrl+H
//code.Action=onCode
//code.pic=barcode.gif

paster.Type=TMenuItem
paster.Text=��ҩ��
paster.Tip=��ҩ��
paster.M=PA
paster.Action=onPaster
paster.pic=print.gif

elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=���Ӳ���
elecCaseHistory.Tip=���Ӳ���
elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=emr.gif

queryDrug.Type=TMenuItem
queryDrug.Text=ҩƷ��Ϣ
queryDrug.Tip=ҩƷ��Ϣ
queryDrug.M=QD
queryDrug.Action=queryDrug
queryDrug.pic=sta-4.gif

checkDrugHand.Type=TMenuItem
checkDrugHand.Text=������ҩ
checkDrugHand.Tip=������ҩ
checkDrugHand.M=CD
checkDrugHand.Action=checkDrugHand
checkDrugHand.pic=051.gif

dispenseDrugList.Type=TMenuItem
dispenseDrugList.Text=��ҩ��
dispenseDrugList.Tip=��ҩ��
dispenseDrugList.M=DD
dispenseDrugList.Action=onDispenseDrugList
dispenseDrugList.pic=print.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

