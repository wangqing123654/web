##############################################
# <p>Title:��Ժ���� </p>
#
# <p>Description:��Ժ���� </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author caowl 2012-07-04
# @version 4.0
##############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;bill;|;inCludeBatch;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;inCludeBatch;|;clear;|;close

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=X
clear.key=
clear.Action=onClear
clear.pic=clear.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

bill.Type=TMenuItem
bill.Text=�����˵�
bill.Tip=�����˵�
bill.M=Q
bill.key=Ctrl+F
bill.Action=onBill
bill.pic=Correct.gif

inCludeBatch.Type=TMenuItem
inCludeBatch.Text=�ײ�����ִ��
inCludeBatch.Tip=�ײ�����ִ��
inCludeBatch.M=G
inCludeBatch.key=Ctrl+G
inCludeBatch.Action=onExeIncludeBatch
inCludeBatch.pic=032.gif

print.Type=TMenuItem
print.Text=סԺ֤
print.Tip=סԺ֤
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
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

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif