 #
  # Title: ������ִ��
  #
  # Description: ������ִ��
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.05.07
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ekt;|;skiResult;|;clear;|;card;|;print;|;bedout;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;skiResult;|;clear;|;card;|;print;|;bedout;|;close

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

print.Type=TMenuItem
print.Text=��ӡִ�е�
print.Tip=��ӡִ�е�
print.M=P
print.Action=onPrint
print.pic=print.gif

card.Type=TMenuItem
card.Text=��ͷ��
card.Tip=��ͷ��
card.M=P
card.Action=onCard
card.pic=card.gif


bedout.Type=TMenuItem
bedout.Text=��Ժ
bedout.Tip=��Ժ
bedout.M=P
bedout.Action=onBedOut
bedout.pic=export.gif

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

skiResult.Type=TMenuItem
skiResult.Text=Ƥ�Խ��
skiResult.Tip=Ƥ�Խ��
skiResult.M=P
skiResult.key=Ctrl+P
skiResult.Action=onSkiResult
skiResult.pic=032.gif

skiResult.Type=TMenuItem
skiResult.Text=ȡҽ��
skiResult.Tip=ȡҽ��
skiResult.M=P
skiResult.key=
skiResult.Action=onFetch
skiResult.pic=054.gif

