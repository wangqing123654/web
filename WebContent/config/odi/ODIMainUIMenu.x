<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;bedcard;|;card;|;tpr;|;tprC;newtpr;pdf;|;tnb;|;EKTcard;|;clear;|;exportxml;|;print;|;queryMem;|;lock;|;unLock;|;close

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
File.Item=query;|;card;|;bedcard;|;tpr;|;tprC;newtpr;pdf;|;tnb;|;EKTcard;|;clear;|;exportxml;|;print;|;queryMem;|;lock;|;unLock;|;close

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
bedcard.Text=������Ϣ
bedcard.zhText=������Ϣ
bedcard.enText=Pat Info
bedcard.Tip=������Ϣ
bedcard.zhTip=������Ϣ
bedcard.enTip=Pat Info
bedcard.M=P
bedcard.Action=onPatInfo
bedcard.pic=bedcard.gif

tpr.Type=TMenuItem
tpr.Text=���µ�
tpr.Tip=���µ�
tpr.M=J
tpr.Action=onVitalSign
tpr.pic=023.gif

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

tnb.Type=TMenuItem
tnb.Text=Ѫ�Ǳ���
tnb.zhText=Ѫ�Ǳ���
tnb.enText=Ѫ�Ǳ���
tnb.Tip=Ѫ�Ǳ���
tnb.zhTip=Ѫ�Ǳ���
tnb.enTip=Ѫ�Ǳ���
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif

exportxml.Type=TMenuItem
exportxml.Text=��������
exportxml.Tip=��������
exportxml.M=P
exportxml.Action=onExport
exportxml.pic=export.gif

print.Type=TMenuItem
print.Text=��ͷ����ӡ
print.Tip=��ͷ����ӡ
print.M=F
print.Action=onPrintO
print.pic=Print.gif

EKTcard.Type=TMenuItem
EKTcard.Text=ҽ�ƿ�
EKTcard.Tip=ҽ�ƿ�
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

queryMem.Type=TMenuItem
queryMem.Text=�ײͲ�ѯ
queryMem.zhText=�ײͲ�ѯ
queryMem.enText=
queryMem.Tip=�ײͲ�ѯ
queryMem.zhTip=�ײͲ�ѯ
queryMem.enTip=
queryMem.M=Q
queryMem.Action=onQueryMemPackage
queryMem.pic=query.gif

lock.Type=TMenuItem
lock.Text=��������
lock.zhText=��������
lock.enText=
lock.Tip=��������
lock.zhTip=��������
lock.enTip=
lock.M=Q
lock.Action=onLockEmr
lock.pic=lock.gif

unLock.Type=TMenuItem
unLock.Text=��������
unLock.zhText=��������
unLock.enText=
unLock.Tip=��������
unLock.zhTip=��������
unLock.enTip=
unLock.M=Q
unLock.Action=onUnLockEmr
unLock.pic=unlock.gif
