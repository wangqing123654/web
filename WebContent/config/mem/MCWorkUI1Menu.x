#
  # Title: ��Աע��
  #
  # Description:��Աע��
  #
  # Copyright: Bluecore (c) 2014
  #
  # @author duzhw
  # @version 4.5
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;query;|;EKTcard;|;idcard;|;buycard;|;makecard;|;MEMprint;|;hl;|;Wrist;|;crmreg;|;updateBirth;|;clear;|;close;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;new;|;query;|;EKTcard;|;idcard;|;buycard;|;makecard;|;MEMprint;|;hl;|;Wrist;|;crmreg;|;updateBirth;|;clear;|;close;|

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif

EKTcard.Type=TMenuItem
EKTcard.Text=ҽ�ƿ�
EKTcard.Tip=ҽ�ƿ�
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

buycard.Type=TMenuItem
buycard.Text=����
buycard.Tip=����
buycard.M=R
buycard.key=F5
buycard.Action=onBuy
buycard.pic=038.gif

makecard.Type=TMenuItem
makecard.Text=�ƿ�
makecard.Tip=�ƿ�
makecard.M=M
makecard.key=F5
makecard.Action=onEKTBuy
makecard.pic=sta.gif

idcard.Type=TMenuItem
idcard.Text=���֤
idcard.Tip=���֤
idcard.M=R
idcard.key=F5
idcard.Action=onIdCard
idcard.pic=idcard.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

new.Type=TMenuItem
new.Text=����
new.zhText=����
new.enText=New
new.Tip=����
new.zhTip=����
new.enTip=New
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

MEMprint.Type=TMenuItem
MEMprint.Text=��ӡ
MEMprint.Tip=��ӡ
MEMprint.M=P
MEMprint.key=Ctrl+P
MEMprint.Action=onRePrint
MEMprint.pic=print_red.gif

hl.Type=TMenuItem
hl.Text=��ʷ����
hl.zhText=��ʷ����
hl.enText=��ʷ����
hl.Tip=��ʷ����
hl.zhTip=��ʷ����
hl.enTip=��ʷ����
hl.M=S
hl.Action=onHisInfo
hl.pic=048.gif

Wrist.Type=TMenuItem
Wrist.Text=��ӡ
Wrist.Tip=��ӡ
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

crmreg.Type=TMenuItem
crmreg.Text=CRMԤԼ��Ϣ
crmreg.Tip=CRMԤԼ��Ϣ
crmreg.M=C
crmreg.Action=onCRM
crmreg.pic=032.gif


updateBirth.Type=TMenuItem
updateBirth.Text=��������������
updateBirth.Tip=��������������
updateBirth.M=U
updateBirth.key=Ctrl+U
updateBirth.Action=onUpdateBirthDate
updateBirth.pic=time.gif