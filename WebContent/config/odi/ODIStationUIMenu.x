<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;|;delTableRow;|;mrshow;|;planReport;|;cxMrshow;|pdf;|;medApplyNo;merge;|;singledise;|;close


Window.Type=TMenu
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=save;query;clear;|;delTableRow;|;mrshow;|;planReport;|;cxMrshow;|pdf;|;medApplyNo;merge;|;singledise;|;close


query.Type=TMenuItem
query.Text=��ѯ
query.zhText=��ѯ
query.enText=Query
query.Tip=��ѯ
query.zhTip=��ѯ
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=����
save.zhText=����
save.enText=Save
save.Tip=����
save.zhTip=����
save.enTip=Save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Clear
clear.Tip=���
clear.zhTip=���
clear.enTip=Clear
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

delTableRow.Type=TMenuItem
delTableRow.Text=ɾ��ҽ��
delTableRow.zhText=ɾ��ҽ��
delTableRow.enText=Delete
delTableRow.Tip=ɾ��ҽ��
delTableRow.zhTip=ɾ��ҽ��
delTableRow.enTip=Delete
delTableRow.M=D
delTableRow.Action=onDelRow
delTableRow.pic=delete.gif

mrshow.Type=TMenuItem
mrshow.Text=�������
mrshow.Tip=�������(Ctrl+W)
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=�������
cxMrshow.Tip=�������(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

planReport.Type=TMenuItem
planReport.Text=�������
planReport.Tip=�������
planReport.M=
planReport.key=
planReport.Action=onPlanrep
planReport.pic=detail-1.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

pdf.Type=TMenuItem
pdf.Text=��������
pdf.zhText=��������
pdf.enText=��������
pdf.Tip=��������
pdf.zhTip=��������
pdf.enTip=��������
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif

merge.Type=TMenuItem
merge.Text=�����ֺϲ�
merge.Tip=�����ֺϲ�
merge.M=M
merge.key=
merge.Action=onMerge
merge.pic=sta-1.gif

singledise.Type=TMenuItem
singledise.Text=������׼��
singledise.Tip=������׼��
singledise.M=S
singledise.Action=onSingleDise
singledise.pic=emr-1.gif

medApplyNo.Type=TMenuItem
medApplyNo.Text=��������
medApplyNo.Tip=��ӡ����
medApplyNo.M=C
medApplyNo.Action=onMedApplyPrint
medApplyNo.pic=barCode.gif