#############################################
# <p>Title:������ҳMenu </p>
#
# <p>Description:������ҳMenu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.10.14
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;print;|;outhospital;|;into;|;finance;|;child;|;studycase;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;print;|;outhospital;|;into;|;finance;|;child;|;studycase;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

outhospital.Type=TMenuItem
outhospital.Text=��Ժ��Ƭ
outhospital.Tip=��Ժ��Ƭ
outhospital.M=O
outhospital.Action=onOutHospital
outhospital.pic=idcard.gif

into.Type=TMenuItem
into.Text=סԺ������
into.Tip=סԺ������
into.M=0
into.Action=onInto
into.pic=012.gif

intoDr.Type=TMenuItem
intoDr.Text=ҽʦ����
intoDr.Tip=ҽʦ����
intoDr.M=0
intoDr.Action=onIntoDr
intoDr.pic=odidrimg.gif

finance.Type=TMenuItem
finance.Text=�������
finance.Tip=�������
finance.M=I
finance.Action=onFinance
finance.pic=bill.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

child.Type=TMenuItem
child.Text=���������
child.Tip=���������
child.Action=onChild
child.pic=013.gif

studycase.Type=TMenuItem
studycase.Text=�����о�����
studycase.Tip=�����о�����
studycase.Action=onCase
studycase.pic=Line.gif