##############################################
# <p>Title:סԺ�Ǽ�Menu </p>
#
# <p>Description:סԺ�Ǽ�Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 1.0
##############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;stop;|;picture;|;EKTcard;|;patinfo;|;bilpay;|;greenpath;|;query;|;immunity;|;print;|;Wrist;|;merge;|;clear;|;regist;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;stop;|;picture;|;EKTcard;|;patinfo;|;bilpay;|;greenpath;|;query;|;immunity;|;print;|;Wrist;|;merge;|;clear;|;regist;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

EKTcard.Type=TMenuItem
EKTcard.Text=ҽ�ƿ�
EKTcard.Tip=ҽ�ƿ�
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

stop.Type=TMenuItem
stop.Text=ȡ��סԺ
stop.Tip=ȡ��סԺ
stop.M=
stop.key=
stop.Action=onStop
stop.pic=030.gif

picture.Type=TMenuItem
picture.Text=�������֤
picture.Tip=�������֤
picture.M=
picture.key=
picture.Action=onIdCardNo
picture.pic=idcard.gif

patinfo.Type=TMenuItem
patinfo.Text=��������
patinfo.Tip=��������
patinfo.M=
patinfo.key=
patinfo.Action=onPatInfo
patinfo.pic=038.gif

bed.Type=TMenuItem
bed.Text=��������
bed.Tip=��������
bed.M=
bed.key=
bed.Action=onBed
bed.pic=048.gif

bilpay.Type=TMenuItem
bilpay.Text=Ԥ����
bilpay.Tip=Ԥ����
bilpay.M=
bilpay.key=
bilpay.Action=onBilpay
bilpay.pic=openbill-2.gif

greenpath.Type=TMenuItem
greenpath.Text=��ɫͨ��
greenpath.Tip=��ɫͨ��
greenpath.M=
greenpath.key=
greenpath.Action=onGreenPath
greenpath.pic=017.gif

query.Type=TMenuItem
query.Text=������ѯ
query.Tip=������ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=search-1.gif

print.Type=TMenuItem
print.Text=סԺ֤��ӡ
print.Tip=סԺ֤��ӡ
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

child.Type=TMenuItem
child.Text=�������Ǽ�
child.Tip=�������Ǽ�
child.M=
child.key=
child.Action=onChild
child.pic=035.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=Z
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

regist.Type=TMenuItem
regist.Text=ע���������
regist.Tip=ע���������
regist.M=
regist.key=
regist.Action=onRegist
regist.pic=007.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

immunity.Type=TMenuItem
immunity.Text=����������
immunity.Tip=����������
immunity.Action=onImmunity
immunity.pic=013.gif

Wrist.Type=TMenuItem
Wrist.Text=��ӡ���
Wrist.Tip=��ӡ���
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif


