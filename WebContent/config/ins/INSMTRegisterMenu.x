#
  # Title: ���صǼ� ��˲�ѯ
  #
  # Description:���صǼ� \���:���صǼǿ��߻����� \��˲�ѯ
  #
  # Copyright: BlueCore (c) 2011
  #
  # @author pangben 2012-1-13
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;readCard;command;|;loadDown;|;update;|;share;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;readCard;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

readCard.Type=TMenuItem
readCard.Text=����
readCard.Tip=����
readCard.M=R
readCard.Action=onReadCard
readCard.pic=042.gif

command.Type=TMenuItem
command.Text=����
command.Tip=����
command.M=S
command.Action=onCommandButSave
command.pic=convert.gif

loadDown.Type=TMenuItem
loadDown.Text=����
loadDown.Tip=����
loadDown.M=D
loadDown.Action=onLoadDown
loadDown.pic=031.gif

update.Type=TMenuItem
update.Text=���
update.Tip=���
update.M=U
update.Action=onUpdate
update.pic=046.gif

share.Type=TMenuItem
share.Text=������Ϣ
share.Tip=������Ϣ
share.M=D
share.Action=onShare
share.pic=detail-1.gif

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