 #
  # Title: ҽ�ƿ��ƿ�����
  #
  # Description: ҽ�ƿ��ƿ�����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;renew;|;cardprint;|;EKTcard;|;MEMcard;|;showpat;|;idcard;|;updateEKTpwd;|;reg;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;renew;|;cardprint;|;EKTcard;|;MEMcard;|;showpat;|;idcard;|;updateEKTpwd;|;reg;|;clear;|;close

reg.Type=TMenuItem
reg.Text=�Һ�
reg.Tip=�Һ�(Ctrl+R)
reg.M=R
reg.key=Ctrl+R
reg.Action=onReg
reg.pic=time.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

renew.Type=TMenuItem
renew.Text=��д��
renew.Tip=��д��
renew.M=R
renew.key=F5
renew.Action=onRenew
renew.pic=idcard.gif

cardprint.Type=TMenuItem
cardprint.Text=��Ƭ��ӡ
cardprint.Tip=��Ƭ��ӡ
cardprint.M=D
cardprint.key=Ctrl+D
cardprint.Action=onPrint
cardprint.pic=print.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif



EKTcard.Type=TMenuItem
EKTcard.Text=ҽ�ƿ�
EKTcard.Tip=ҽ�ƿ�
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

MEMcard.Type=TMenuItem
MEMcard.Text=��Ա���ۿ�
MEMcard.Tip=��Ա���ۿ�
MEMcard.M=E
MEMcard.Action=onMEMcard
MEMcard.pic=sta.gif

idcard.Type=TMenuItem
idcard.Text=�������֤
idcard.Tip=�������֤
idcard.M=M
idcard.Action=onIdCard
idcard.pic=038.gif

updateEKTpwd.Type=TMenuItem
updateEKTpwd.Text=ҽ�ƿ��޸�����
updateEKTpwd.Tip=ҽ�ƿ��޸�����
updateEKTpwd.M=U
updateEKTpwd.Action=updateEKTPwd
updateEKTpwd.pic=007.gif

showpat.Type=TMenuItem
showpat.Text=��ע�Ỽ��
showpat.Tip=��ע�Ỽ��
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onNewPat
showpat.pic=patlist.gif
