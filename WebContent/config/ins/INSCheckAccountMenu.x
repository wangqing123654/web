#
  # Title: �������
  #
  # Description:������ˣ��Զ����� �ܶ��� ��ϸ���� ����ȷ��
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2012-1-8
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=autoSave;|;selectMrNo;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=autoSave;|;selectMrNo;|;clear;|;close

autoSave.Type=TMenuItem
autoSave.Text=�Զ�����
autoSave.Tip=�Զ�����
autoSave.M=S
autoSave.key=Ctrl+S
autoSave.Action=onAutoSave
autoSave.pic=046.gif

selectMrNo.Type=TMenuItem
selectMrNo.Text=��ѯ������Ϣ
selectMrNo.Tip=��ѯ������Ϣ
selectMrNo.M=N
selectMrNo.Action=onQueryMrNo
selectMrNo.pic=detail-1.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif