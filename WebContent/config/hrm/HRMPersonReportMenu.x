 #
  # Title:������˱���
  #
  # Description:������˱���
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;backReceipt;barCode;readCard;|;picture;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;barCode;readCard;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSaveCom
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

disCharge.Type=TMenuItem
disCharge.Text=�˷�
disCharge.Tip=�˷�
disCharge.M=N
disCharge.key=Delete
disCharge.Action=onDisCharge
disCharge.pic=030.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

barCode.Type=TMenuItem
barCode.Text=��ӡ����
barCode.Tip=��ӡ����
barCode.M=C
barCode.Action=onBarCode
barCode.pic=barcode.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

backReceipt.Type=TMenuItem
backReceipt.Text=���ò�ѯ
backReceipt.Tip=���ò�ѯ
backReceipt.M=R
backReceipt.key=F7
backReceipt.Action=onBackReceipt
backReceipt.pic=detail-1.gif

creatCard.Type=TMenuItem
creatCard.Text=�ƿ�
creatCard.Tip=�ƿ�
creatCard.M=R
creatCard.key=F11
creatCard.Action=onCreatCard
creatCard.pic=card.gif

readCard.Type=TMenuItem
readCard.Text=����
readCard.Tip=����
readCard.M=R
readCard.key=F12
readCard.Action=onReadCard
readCard.pic=042.gif

reportSheet.Type=TMenuItem
reportSheet.Text=������
reportSheet.Tip=������
reportSheet.M=C
reportSheet.Action=onReportPrint
reportSheet.pic=print.gif

picture.Type=TMenuItem
picture.Text=�������֤
picture.Tip=�������֤
picture.M=
picture.key=
picture.Action=onIdCardNo
picture.pic=idcard.gif
