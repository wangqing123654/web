  # Title: 
  #
  # Description:��������
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author yanj 2013-08-26
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;consApplyDetail;|;delete;|;email;|;message;|;query;|;cancel;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;consApplyDetail;|;delete;|;email;|;message;|;query;|;cancel;|;clear;|;close


save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

consApplyDetail.Type=TMenuItem
consApplyDetail.Text=������������
consApplyDetail.zhText=������������
consApplyDetail.enText=consapply Profile
consApplyDetail.Tip=������������
consApplyDetail.zhTip=������������
consApplyDetail.enTip=consapply Profile
consApplyDetail.M=
consApplyDetail.key=
consApplyDetail.Action=onConsApplyDetail
consApplyDetail.pic=detail-1.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

email.Type=TMenuItem
email.Text=�����ʼ�
email.Tip=�����ʼ�
email.M=E
email.key=EMAIL
email.Action=onBoardMessage
email.pic=010.gif

message.Type=TMenuItem
message.Text=���Ͷ���
message.Tip=���Ͷ���
message.M=M
message.key=Message
message.Action=onMessage
message.pic=014.gif


cancel.Type=TMenuItem
cancel.Text=ȡ������
cancel.Tip=ȡ������
cancel.M=U
cancel.key=
cancel.Action=onCancel
cancel.pic=030.gif

query.Type=TMenuItem
query.Text=�Ű��ѯ
query.Tip=�Ű��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif