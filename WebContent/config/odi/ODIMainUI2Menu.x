<Type=TMenuBar>
UI.Item=File;Tool;Window
UI.button=query;|;card;bedcard;|;cln;|;emr;|;bas;|;bab;|;sel;|;twd;|;tprC;|;newtpr;|;hl;|;smz;|;res;|;opd;|;lis;|;ris;|;hos;|;ibs;|;pdf;|;export;|;clear;|;close

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
File.Item=query;|;card;bedcard;|;hl;|;opd;|;lis;|;ris;|;tnb;|;export;|;clear;|;close

Tool.Type=TMenu
Tool.Text=����
Tool.zhText=����
Tool.enText=Tool
Tool.M=F
Tool.Item=cln;|;emr;|;bas;|;bab;|;sel;|;twd;|;smz;|;res;|;hos;|;ibs;|;pdf

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

card.Type=TMenuItem
card.Text=��ͷ��
card.zhText=��ͷ��
card.enText=bed card
card.Tip=��ͷ��
card.zhTip=��ͷ��
card.enTip=bed card
card.M=B
card.Action=onBedCard
card.pic=card.gif

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

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

bedcard.Type=TMenuItem
bedcard.Text=����
bedcard.zhText=������Ϣ
bedcard.enText=Pat Info
bedcard.Tip=������Ϣ
bedcard.zhTip=������Ϣ
bedcard.enTip=Pat Info
bedcard.M=P
bedcard.Action=onPatInfo
bedcard.pic=bedcard.gif



pdf.Type=TMenuItem
pdf.Text=�ϲ���
pdf.zhText=��������
pdf.enText=��������
pdf.Tip=��������
pdf.zhTip=��������
pdf.enTip=��������
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif


cln.Type=TMenuItem
cln.Text=���
cln.zhText=���
cln.enText=���
cln.Tip=���
cln.zhTip=���
cln.enTip=���
cln.M=Q
cln.Action=onAddCLNCPath
cln.pic=009.gif

emr.Type=TMenuItem
emr.Text=д����
emr.zhText=������д
emr.enText=������д
emr.Tip=������д
emr.zhTip=������д
emr.enTip=������д
emr.M=S
emr.Action=onAddEmrWrite
emr.pic=emr-1.gif

bas.Type=TMenuItem
bas.Text=����
bas.Tip=������Ŀ
bas.zhTip=������Ŀ
bas.enTip=������Ŀ
bas.M=A
bas.Action=onAddBASY
bas.pic=012.gif


bab.Type=TMenuItem
bab.Text=����
bab.zhText=�������
bab.enText=�������
bab.Tip=�������
bab.zhTip=�������
bab.enTip=�������
bab.M=S
bab.Action=onBABM
bab.pic=029.gif

sel.Type=TMenuItem
sel.Text=ҽ����
sel.zhText=ҽ����
sel.enText=ҽ����
sel.Tip=ҽ����
sel.zhTip=ҽ����
sel.enTip=ҽ����
sel.M=S
sel.Action=onSelYZD
sel.pic=017.gif

twd.Type=TMenuItem
twd.Text=���µ�
twd.zhText=���µ�
twd.enText=���µ�
twd.Tip=���µ�
twd.zhTip=���µ�
twd.enTip=���µ�
twd.M=S
twd.Action=onSelTWD
twd.pic=037.gif

tprC.Type=TMenuItem
tprC.Text=��ͯ���µ�
tprC.Tip=��ͯ���µ�
tprC.M=N
tprC.Action=onVitalSignChild
tprC.pic=048.gif

newtpr.Type=TMenuItem
newtpr.Text=���������µ�
newtpr.Tip=���������µ�
newtpr.M=N
newtpr.Action=onNewArrival
newtpr.pic=035.gif

hl.Type=TMenuItem
hl.Text=����
hl.zhText=�����¼
hl.enText=�����¼
hl.Tip=�����¼
hl.zhTip=�����¼
hl.enTip=�����¼
hl.M=S
hl.Action=onHLSel
hl.pic=048.gif

smz.Type=TMenuItem
smz.Text=����
smz.zhText=��������
smz.enText=��������
smz.Tip=��������
smz.zhTip=��������
smz.enTip=��������
smz.M=S
smz.Action=onSSMZ
smz.pic=051.gif

res.Type=TMenuItem
res.Text=��Ѫ
res.zhText=��Ѫ����
res.enText=��Ѫ����
res.Tip=��Ѫ����
res.zhTip=��Ѫ����
res.enTip=��Ѫ����
res.M=S
res.Action=onBXResult
res.pic=blood.gif

opd.Type=TMenuItem
opd.Text=�ż�����
opd.zhText=�ż��ﲡ��
opd.enText=�ż��ﲡ��
opd.Tip=�ż��ﲡ��
opd.zhTip=�ż��ﲡ��
opd.enTip=�ż��ﲡ��
opd.M=S
opd.Action=onOpdBL
opd.pic=032.gif

lis.Type=TMenuItem
lis.Text=����
lis.zhText=���鱨��
lis.enText=���鱨��
lis.Tip=���鱨��
lis.zhTip=���鱨��
lis.enTip=���鱨��
lis.M=S
lis.Action=onLis
lis.pic=LIS.gif

ris.Type=TMenuItem
ris.Text=���
ris.zhText=��鱨��
ris.enText=��鱨��
ris.Tip=��鱨��
ris.zhTip=��鱨��
ris.enTip=��鱨��
ris.M=S
ris.Action=onRis
ris.pic=RIS.gif

hos.Type=TMenuItem
hos.Text=��Ժ
hos.zhText=��Ժ֪ͨ
hos.enText=��Ժ֪ͨ
hos.Tip=��Ժ֪ͨ
hos.zhTip=��Ժ֪ͨ
hos.enTip=��Ժ֪ͨ
hos.M=S
hos.Action=onOutHosp
hos.pic=015.gif

ibs.Type=TMenuItem
ibs.Text=����
ibs.zhText=���ò�ѯ
ibs.enText=���ò�ѯ
ibs.Tip=���ò�ѯ
ibs.zhTip=���ò�ѯ
ibs.enTip=���ò�ѯ
ibs.M=S
ibs.Action=onSelIbs
ibs.pic=fee.gif

tnb.Type=TMenuItem
tnb.Text=Ѫ��
tnb.zhText=Ѫ�Ǳ���
tnb.enText=Ѫ�Ǳ���
tnb.Tip=Ѫ�Ǳ���
tnb.zhTip=Ѫ�Ǳ���
tnb.enTip=Ѫ�Ǳ���
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif

export.Type=TMenuItem
export.Text=����
export.Tip=����(Ctrl+E)
export.M=E
export.Action=onExport
export.pic=export.gif