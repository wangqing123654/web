 #
  # Title: �ٴ�·���趨
  #
  # Description: �ٴ�·���趨
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author Zhangjg 2011.04.11
 # @version 1.0
 #20140822 liling add  release����   revise�޶�
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;print;revise;release;|;export;|;allExport;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;query;print;export;|;allExport;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

use.Type=TMenuItem
use.Text=����
use.Tip=����(Ctrl+U)
use.M=U
use.key=Ctrl+U
use.Action=onUse
use.pic=save-1.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

revise.Type=TMenuItem
revise.Text=�޶�
revise.Tip=�޶�
revise.Action=onRevise
revise.pic=026.gif

allExport.Type=TMenuItem
allExport.Text=����ȫ��
allExport.Tip=����ȫ��
allExport.Action=onAllExport
allExport.pic=045.gif

release.Type=TMenuItem
release.Text=����
release.Tip=����
release.Action=onRelease
release.pic=007.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=(Ctrl+P)
print.Action=onPrint
print.pic=print.gif

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

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

export.Type=TMenuItem
export.Text=����
export.Tip=����
execrpt.M=E
export.Action=onExport
export.pic=exportexcel.gif
