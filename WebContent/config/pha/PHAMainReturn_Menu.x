<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;EKT;clear;|;call;elecCaseHistory;returnDrugList;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;EKT;|;clear;|;close

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

returnDrugList.Type=TMenuItem
returnDrugList.Text=��ҩ��
returnDrugList.Tip=��ҩ��
//returnDrugList.M=PA
returnDrugList.Action=onPrint
returnDrugList.pic=print.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

