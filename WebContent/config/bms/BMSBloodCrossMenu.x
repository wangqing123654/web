 #
  # Title: ������Ѫ
  #
  # Description: ������Ѫ
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.09.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=save;|;delete;|;clear;|;stock;|;lis;|;ris;|;tnb;|;pay;|;dispense;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;clear;|;stock;|;pay;|;dispense;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

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

stock.Type=TMenuItem
stock.Text=����ѯ
stock.Tip=����ѯ
stock.M=P
stock.Action=onStock
stock.pic=query.gif

pay.Type=TMenuItem
pay.Text=ѪҺ�Ƽ�
pay.Tip=ѪҺ�Ƽ�
pay.M=P
pay.Action=onPay
pay.pic=bill-1.gif

dispense.Type=TMenuItem
dispense.Text=ѪҺ����
dispense.Tip=ѪҺ����
dispense.M=P
dispense.Action=onDispense
dispense.pic=export.gif

lis.Type=TMenuItem
lis.Text=���鱨��
lis.zhText=���鱨��
lis.enText=���鱨��
lis.Tip=���鱨��
lis.zhTip=���鱨��
lis.enTip=���鱨��
lis.M=S
lis.Action=onLis
lis.pic=LIS.gif

ris.Type=TMenuItem
ris.Text=��鱨��
ris.zhText=��鱨��
ris.enText=��鱨��
ris.Tip=��鱨��
ris.zhTip=��鱨��
ris.enTip=��鱨��
ris.M=S
ris.Action=onRis
ris.pic=RIS.gif

tnb.Type=TMenuItem
tnb.Text=����
tnb.zhText=����
tnb.enText=����
tnb.Tip=����
tnb.zhTip=����
tnb.enTip=����
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif