 #
  # Title: �����ұ���
  #
  # Description: �����ұ���
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.05.07
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ekt;|;clear;|;card;|;bill;|;print1;|;print2;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;ekt;|;clear;|;card;|;bill;|;print1;|;print2;|;close

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

print1.Type=TMenuItem
print1.Text=ƿǩ��ӡ
print1.Tip=ƿǩ��ӡ
print1.M=P
print1.Action=onBottlePrint
print1.pic=print.gif

card.Type=TMenuItem
card.Text=��ͷ��
card.Tip=��ͷ��
card.M=P
card.Action=onCard
card.pic=card.gif

bill.Type=TMenuItem
bill.Text=����Ƽ�
bill.Tip=����Ƽ�
bill.M=P
bill.Action=onBill
bill.pic=bill.gif

print2.Type=TMenuItem
print2.Text=��/����ӡ
print2.Tip=��/����ӡ
print2.M=P
print2.Action=onBedPrint
print2.pic=pha_print.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

ekt.Type=TMenuItem
ekt.Text=ҽ�ƿ�
ekt.Tip=ҽ�ƿ�
ekt.M=S
ekt.key=
ekt.Action=onEkt
ekt.pic=042.gif
