#################################################
# <p>Title:������ѯMenu </p>
#
# <p>Description:������ѯMenu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY 2009.04.30
# @version 1.0
#################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;bilpay;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh


File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;bilpay;|;print;|;clear;|;close

bilpay.Type=TMenuItem
bilpay.Text=Ԥ����
bilpay.Tip=Ԥ����
bilpay.M=
bilpay.key=
bilpay.Action=onBilpay
bilpay.pic=openbill-2.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=
query.key=
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=סԺ֤��ӡ
print.Tip=סԺ֤��ӡ
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

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=Z
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif
