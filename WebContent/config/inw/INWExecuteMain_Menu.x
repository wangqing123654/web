<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;|;charge;emr;tpr|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
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

charge.Type=TMenuItem
charge.Text=����Ʒ�
charge.Tip=����Ʒ�
charge.M=H
charge.key=Ctrl+H
charge.Action=onCharge
charge.pic=bill-1.gif

emr.Type=TMenuItem
emr.Text=�ṹ������
emr.Tip=�ṹ������
emr.M=J
emr.key=Ctrl+J
emr.Action=onEmr
emr.pic=emr-2.gif

tpr.Type=TMenuItem
tpr.Text=���µ�
tpr.Tip=���µ�
tpr.M=J
tpr.key=Ctrl+T
tpr.Action=onVitalSign
tpr.pic=Column.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif