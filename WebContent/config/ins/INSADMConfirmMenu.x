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
UI.button=save;|;resvNClose;|;admNClose;|;readCard;|;confirmQuery;|;eveinspat;|;delayapp;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;resvNClose;|;admNClose;|;readCard;|;confirmQuery;|;eveinspat;|;delayapp;|;clear;|;close

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

readCard.Type=TMenuItem
readCard.Text=ˢ��
readCard.Tip=ˢ��
readCard.M=N
readCard.Action=onReadCard
readCard.pic=008.gif

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
admNClose.Action=onAdmNClose
admNClose.pic=048.gif

confirmQuery.Type=TMenuItem
confirmQuery.Text=�ʸ�ȷ������ʷ��¼��ѯ
confirmQuery.Tip=�ʸ�ȷ������ʷ��¼��ѯ
confirmQuery.Action=onConfirmNo
confirmQuery.pic=043.gif

eveinspat.Type=TMenuItem
eveinspat.Text=�����ҽ������
eveinspat.Tip=�����ҽ������
eveinspat.Action=onEveInsPat
eveinspat.pic=035.gif

delayapp.Type=TMenuItem
delayapp.Text=�ӳ��걨
delayapp.Tip=�ӳ��걨
delayapp.Action=onDelayApp
delayapp.pic=011.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif