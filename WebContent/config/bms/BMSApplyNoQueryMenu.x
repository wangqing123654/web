 #
  # Title: ��Ѫ���뵥��ѯ
  #
  # Description: ��Ѫ���뵥��ѯ
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=query;|;clear;|;apply;|;check;|;cross;|;out;|;feeDetail;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;apply;|;check;|;cross;|;out;|;feeDetail;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
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

apply.Type=TMenuItem
apply.Text=��Ѫ���뵥
apply.Tip=��Ѫ���뵥
apply.M=P
apply.Action=onApply
apply.pic=046.gif

check.Type=TMenuItem
check.Text=�����¼
check.Tip=�����¼
check.M=P
check.Action=onCheck
check.pic=search-2.gif

cross.Type=TMenuItem
cross.Text=������Ѫ
cross.Tip=������Ѫ
cross.M=P
cross.Action=onCross
cross.pic=tempsave.gif

out.Type=TMenuItem
out.Text=ѪҺ����
out.Tip=ѪҺ����
out.M=P
out.Action=onOut
out.pic=export.gif

feeDetail.Type=TMenuItem
feeDetail.Text=Ѫ����ϸ
feeDetail.Tip=Ѫ����ϸ
feeDetail.M=P
feeDetail.Action=onFeeDetail
feeDetail.pic=convert.gif
