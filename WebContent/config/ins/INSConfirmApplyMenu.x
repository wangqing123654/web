#
  # Title: �ʸ�ȷ��������/����
  #
  # Description:�ʸ�ȷ��������/����
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-11-30
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;resvNClose;|;admNClose;|;clear;|;delete;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;resvNClose;|;admNClose;|;clear;|;delete;|;close

save.Type=TMenuItem
save.Text=�ʸ�ȷ��������/����
save.Tip=�ʸ�ȷ��������/����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Commit.gif

resvNClose.Type=TMenuItem
resvNClose.Text=ԤԼδ�᰸
resvNClose.Tip=ԤԼδ�᰸
resvNClose.M=N
resvNClose.Action=onResvNClose
resvNClose.pic=046.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
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

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

admNClose.Type=TMenuItem
admNClose.Text=סԺδ�᰸
admNClose.Tip=סԺδ�᰸
admNClose.M=
admNClose.Action=onAdmNClose
admNClose.pic=046.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif