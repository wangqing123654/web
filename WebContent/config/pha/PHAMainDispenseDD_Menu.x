<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;EKT;clear;ATC;|;call;elecCaseHistory;paster;dispenseDrugList;|;close

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

EKT.Type=TMenuItem
EKT.Text=ҽ�ƿ�
EKT.Tip=ҽ�ƿ�
EKT.M=E
EKT.key=Ctrl+E
EKT.Action=onEKT
EKT.pic=042.gif

ATC.Type=TMenuItem
ATC.Text=�Ͱ�ҩ��
ATC.Tip=�Ͱ�ҩ��
ATC.M=A
ATC.key=Ctrl+G
ATC.Action=onGenATCFile
ATC.pic=nurse.gif

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
clear.Action=onClear
clear.pic=clear.gif

call.Type=TMenuItem
call.Text=�к�
call.Tip=�к�
call.M=CA
call.Action=onCall
call.pic=tel.gif

elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=���Ӳ���
elecCaseHistory.Tip=���Ӳ���
elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=emr.gif

//paster.Type=TMenuItem
//paster.Text=��ҩ��
//paster.Tip=��ҩ��
//paster.M=PA
//paster.Action=onPaster
//paster.pic=print.gif

//dispenseDrugList.Type=TMenuItem
//dispenseDrugList.Text=��ҩ��
//dispenseDrugList.Tip=��ҩ��
//dispenseDrugList.M=DD
//dispenseDrugList.Action=onDispenseDrugList
//dispenseDrugList.pic=print.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

