#
  # Title: �����շ�
  #
  # Description:�����շѺͲ���Ƽ�
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ektCard;|;insureinfo;|;queryPack;|;ctzModify;|;CaseHistory;|;delete;|;record;|;backReceipt;|;insCard;|;ektPrint;|;operation;|;fee;|;orderPack;|;Wrist;|;reduceCheck;|;clear;|;close;|;
//ȥ�� |;insPrint;
Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;delete;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


caseHistory.Type=TMenuItem
caseHistory.Text=�����¼
caseHistory.Tip=�����¼
caseHistory.M=C
caseHistory.Action=onCaseHistory
caseHistory.pic=032.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

reduceCheck.Type=TMenuItem
reduceCheck.Text=��������
reduceCheck.Tip=��������
//backReceipt.M=F
//backReceipt.key=F7
reduceCheck.Action=onReduceCheck
reduceCheck.pic=Retrieve.gif


queryPack.Type=TMenuItem
queryPack.Text=�ײͲ�ѯ
queryPack.Tip=�ײͲ�ѯ
//queryPack.M=T
//queryPack.key=
queryPack.Action=onQueryPack
queryPack.pic=search-1.gif


ctzModify.Type=TMenuItem
ctzModify.Text=����޸�
ctzModify.Tip=����޸�
//ctzModify.M=G
//ctzModify.key=
ctzModify.Action=onCtzModify
ctzModify.pic=007.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=D
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

backReceipt.Type=TMenuItem
backReceipt.Text=���ò�ѯ
backReceipt.Tip=���ò�ѯ
//backReceipt.M=F
//backReceipt.key=F7
backReceipt.Action=onBackReceipt
backReceipt.pic=detail-1.gif

backContract.Type=TMenuItem
backContract.Text=���˲�ѯ
backContract.Tip=���˲�ѯ
//backContract.M=J
//backContract.key=F8
backContract.Action=onBackContract
backContract.pic=011.gif

ektCard.Type=TMenuItem
ektCard.Text=ҽ�ƿ�
ektCard.Tip=��ҽ�ƿ�
//ektCard.M=
//ektCard.key=F6
ektCard.Action=onEKT
ektCard.pic=042.gif

insCard.Type=TMenuItem
insCard.Text=ҽ����
insCard.Tip=��ҽ����
//insCard.M=
insCard.Action=readINSCard
insCard.pic=008.gif

//insPrint.Type=TMenuItem
//insPrint.Text=�����ӡ
//insPrint.Tip=�����ӡ
//insPrint.M=
//insPrint.Action=exeInsPrint
//insPrint.pic=018.gif

ektPrint.Type=TMenuItem
ektPrint.Text=��Ʊ
ektPrint.Tip=��Ʊ
//ektPrint.M=
//ektPrint.key=F6
ektPrint.Action=onEKTPrint
ektPrint.pic=print.gif

fee.Type=TMenuItem
fee.Text=ҽ�ƿ���ֵ
fee.Tip=ҽ�ƿ���ֵ
//fee.M=S
//fee.key=
fee.Action=onFee
fee.pic=bill.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=E
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

record.Type=TMenuItem
record.Text=�����
record.Tip=�����
//record.M=R
//record.key=F5
record.Action=onRecord
record.pic=012.gif

operation.Type=TMenuItem
operation.Text=�Ʒ�ģ��
operation.Tip=�Ʒ�ģ��
//operation.M=P
operation.Action=onOperation
operation.pic=operation.gif

Wrist.Type=TMenuItem
Wrist.Text=���
Wrist.Tip=���
//Wrist.M=
//Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif

insureinfo.Type=TMenuItem
insureinfo.Text=����
insureinfo.Tip=����
insureinfo.M=
insureinfo.key=
insureinfo.Action=onInsureinfo
insureinfo.pic=038.gif


orderPack.Type=TMenuItem
orderPack.Text=�ȶ�
orderPack.Tip=�ȶ�
orderPack.M=
orderPack.key=
orderPack.Action=onOrderPackComparison
orderPack.pic=029.gif