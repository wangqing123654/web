 #
  # Title: ��Ⱦ�����Ǽ�
  #
  # Description: ��Ⱦ�����Ǽ�
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.22
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;clear;print;print1;report;testrep;temperature;showcase;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;query;clear;print;print1;report;testrep;temperature;showcase;export;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

//=====start liling 20140402 ���
print1.Type=TMenuItem
print1.Text=�ǼǱ�
print1.Tip=�ǼǱ�
print1.M=P
print1.Action=onPrintINFCase
print1.pic=print.gif
//=====end liling 20140402 ���

report.Type=TMenuItem
report.Text=���鱨��
report.Tip=���鱨��
report.M=P
report.Action=onReport
report.pic=Lis.gif

testrep.Type=TMenuItem
testrep.Text=��鱨��
testrep.Tip=��鱨��
testrep.M=
testrep.key=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

temperature.Type=TMenuItem
temperature.Text=����
temperature.Tip=����
temperature.M=P
temperature.Action=onTemperature
temperature.pic=Column.gif

showcase.Type=TMenuItem
showcase.Text=�������
showcase.Tip=�������
showcase.M=P
showcase.Action=onShowCase
showcase.pic=043.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif