#
  # Title: ҽ����Ŀ�ֵ��Ӧ
  #
  # Description:ҽ����Ŀ�ֵ��Ӧ
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-12-10
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;removeUpdate;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;removeUpdate;|;clear;|;close

save.Type=TMenuItem
save.Text=�����޸�
save.Tip=�����޸�
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

addUpdate.Type=TMenuItem
addUpdate.Text=����޸�ҽ��
addUpdate.Tip=����޸�ҽ��
addUpdate.M=N
addUpdate.Action=addUpdateSysFee
addUpdate.pic=Commit.gif

removeUpdate.Type=TMenuItem
removeUpdate.Text=�Ƴ��޸�ҽ��
removeUpdate.Tip=�Ƴ��޸�ҽ��
removeUpdate.M=N
removeUpdate.key=Delete
removeUpdate.Action=onRemoveUpdate
removeUpdate.pic=Commit.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=R
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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