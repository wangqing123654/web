 #
  # Title: ��Ѫ����
  #
  # Description: ��Ѫ����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;print;|;close

Window.Type=TMenu
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;print;|;close

save.Type=TMenuItem
save.Text=����
save.zhText=����
save.enText=Save
save.Tip=����(Ctrl+S)
save.zhTip=����(Ctrl+S)
save.enTip=Save(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.zhText=ɾ��
delete.enText=Delete
delete.Tip=ɾ��(Delete)
delete.zhTip=ɾ��
delete.enTip=Delete
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=��ѯ
query.zhText=��ѯ
query.enText=Query
query.Tip=��ѯ(Ctrl+F)
query.zhTip=��ѯ(Ctrl+F)
query.enTip=Query(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Clear
clear.Tip=���(Ctrl+Z)
clear.zhTip=���(Ctrl+Z)
clear.enTip=Clear(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Close
close.Tip=�˳�(Alt+F4)
close.zhTip=�˳�(Alt+F4)
close.enTip=Close(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=��ӡ
print.zhText=��ӡ
print.enText=Print
print.Tip=��ӡ
print.zhTip=��ӡ
print.enTip=Print
print.M=P
print.Action=onPrint
print.pic=print.gif
