# 
#  Title:�˵����
# 
#  Description:�˵����
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2010.06.11
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ibsRecp;|;newBill;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=Refresh;|;save;|;query;|;ibsRecp;|;newBill;|;clear;|;close

save.Type=TMenuItem
save.Text=���
save.Tip=���
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

ibsRecp.Type=TMenuItem
ibsRecp.Text=�ɷ���ҵ
ibsRecp.Tip=�ɷ���ҵ
ibsRecp.M=R
ibsRecp.key=
ibsRecp.Action=onBilIBSRecp
ibsRecp.pic=bank.gif

newBill.Type=TMenuItem
newBill.Text=�˵�����
newBill.Tip=�˵�����
newBill.M=S
newBill.key=
newBill.Action=onNewBill
newBill.pic=Create.gif

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
close.Action=onClose
close.pic=close.gif