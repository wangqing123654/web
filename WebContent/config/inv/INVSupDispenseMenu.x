 #
  # Title: ��Ӧ�����
  #
  # Description:��Ӧ�����
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;delete;|;query;|;import;|;print;|;clear;|;backStock;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;new;|;delete;|;query;|;import;|;print;|;clear;|;backStock;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onUpdate
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=N
new.key=Ctrl+N
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
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

import.Type=TMenuItem
import.Text=�������쵥
import.Tip=�������쵥
import.M=Q
import.key=Ctrl+I
import.Action=onSuprequestChoose
import.pic=detail-1.gif

print.Type=TMenuItem
print.Text=��ӡ��ⵥ
print.Tip=��ӡ��ⵥ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


backStock.Type=TMenuItem
backStock.Text=�˿�
backStock.Tip=�˿�
backStock.M=B
backStock.key=F7
backStock.Action=onBackDispense
backStock.pic=Redo.gif

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