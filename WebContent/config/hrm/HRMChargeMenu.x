 #
  # Title: ����ɷ�
  #
  # Description:����ɷ�
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=delete;print;prePay;|;receiptDetail;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=delete;print;prePay;|;receiptDetail;|;clear;|;close

delete.Type=TMenuItem
delete.Text=�˷�
delete.Tip=�˷�
delete.M=C
delete.Action=onDelete
delete.pic=030.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=C
print.Action=onRePrint
print.pic=print_red.gif

prePay.Type=TMenuItem
prePay.Text=Ԥ����
prePay.Tip=Ԥ����
prePay.M=N
prePay.key=Delete
prePay.Action=onPrePay
prePay.pic=fee.GIF

receiptDetail.Type=TMenuItem
receiptDetail.Text=���ò�ѯ
receiptDetail.Tip=���ò�ѯ
receiptDetail.M=R
receiptDetail.key=F7
receiptDetail.Action=onReceiptDetail
receiptDetail.pic=detail-1.gif

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

fee.Type=TMenuItem
fee.Text=�շ�
fee.Tip=�շ�
fee.M=S
fee.key=Ctrl+S
fee.Action=onFee
fee.pic=emr-1.gif

charge.Type=TMenuItem
charge.Text=��Ʊ
charge.Tip=��Ʊ
charge.M=S
charge.key=Ctrl+S
charge.Action=onCharge
charge.pic=emr-2.gif