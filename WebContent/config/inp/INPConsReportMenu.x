 #
  # Title: HRM�Ű�
  #
  # Description:HRM�Ű�
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=Window
UI.button=save;|;query;|;consApplyDetail;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;consApplyDetail;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

consApplyDetail.Type=TMenuItem
consApplyDetail.Text=���ﱨ��
consApplyDetail.zhText=���ﱨ��
consApplyDetail.enText=consapply Profile
consApplyDetail.Tip=���ﱨ��
consApplyDetail.zhTip=���ﱨ��
consApplyDetail.enTip=consapply Profile
consApplyDetail.M=
consApplyDetail.key=
consApplyDetail.Action=onConsReport
consApplyDetail.pic=detail-1.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=S
new.key=Ctrl+S
new.Action=onNew
new.pic=new.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=N
query.key=Delete
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
