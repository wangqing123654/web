 #
  # Title: ������ҵ
  #
  # Description:������ҵ
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author zhangy 2009.05.06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;save;|;exportxml;|;exportxls;|;print;|;onSynchronous;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;save;|;exportxml;|;exportxls;|;print;|;onSynchronous;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif


exportxml.Type=TMenuItem
exportxml.Text=�������㵥(XML)
exportxml.Tip=�������㵥(XML)
exportxml.M=P
exportxml.Action=onExportXml
exportxml.pic=export.gif

exportxls.Type=TMenuItem
exportxls.Text=�������㵥(XLS)
exportxls.Tip=�������㵥(XLS)
exportxls.M=P
exportxls.Action=onExportXls
exportxls.pic=export.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

onSynchronous.Type=TMenuItem
onSynchronous.Text=����
onSynchronous.Tip=���� 
onSynchronous.M=X
onSynchronous.key= 
onSynchronous.Action=onSynchronous
onSynchronous.pic=054.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif



