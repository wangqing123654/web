 #
  # Title: ���� ���ұ�ҩ����
  #
  # Description:���ұ�ҩ����
  #
  # Copyright: bluecore (c) 2008
  #
  # @author liyh 20130601
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;printM;|;printRecipe;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;clear;|;printM;|;printD;|;printRecipe;|;close

save.Type=TMenuItem
save.Text=�������쵥
save.Tip=�������쵥
save.M=C
save.Action=onSave
save.pic=save.gif

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

printM.Type=TMenuItem
printM.Text=���ܴ�ӡ
printM.Tip=���ܴ�ӡ
printM.M=P
printM.Action=onPrintM
printM.pic=print.gif

printD.Type=TMenuItem
printD.Text=��ϸ��ӡ
printD.Tip=��ϸ��ӡ
printD.M=P
printD.Action=onPrintD
printD.pic=print.gif

printRecipe.Type=TMenuItem
printRecipe.Text=��ӡ����ǩ
printRecipe.Tip=��ӡ����ǩ
printRecipe.M=R
printRecipe.Action=onPrintRecipe
printRecipe.pic=print.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
