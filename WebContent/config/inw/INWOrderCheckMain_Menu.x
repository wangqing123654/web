<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;Newprint;|;categoryPrint;|;temporaryPrint;|;medPrint;|;medApplyNo;|;send;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;Newprint;categoryPrint;temporaryPrint;medPrint;medApplyNo;|;send;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

send.Type=TMenuItem
send.Text=����
send.Tip=����
send.M=O
send.Action=onReSendGYPha
send.pic=Commit.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Newprint.Type=TMenuItem
Newprint.Text=���˴�ӡ
Newprint.Tip=���˴�ӡ
Newprint.M=PN
Newprint.Action=onPrintExe
Newprint.pic=print-1.gif

medPrint.Type=TMenuItem
medPrint.Text=ȡҩ����ӡ
medPrint.Tip=ȡҩ����ӡ
medPrint.Action=onDispenseSheet
medPrint.pic=print-2.gif

categoryPrint.Type=TMenuItem
categoryPrint.Text=����ִ�е���ӡ
categoryPrint.Tip=����ִ�е���ӡ
categoryPrint.M=PN
categoryPrint.Action=onCategoryPrint
categoryPrint.pic=print-1.gif

temporaryPrint.Type=TMenuItem
temporaryPrint.Text=����ִ�е���ӡ
temporaryPrint.Tip=����ִ�е���ӡ
temporaryPrint.M=PN
temporaryPrint.Action=onTemporaryPrint
temporaryPrint.pic=print-1.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

medApplyNo.Type=TMenuItem
medApplyNo.Text=��������
medApplyNo.Tip=��ӡ����
medApplyNo.M=C
medApplyNo.Action=onMedApplyPrint
medApplyNo.pic=barCode.gif