<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;print;code;paster;charge;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;|;clear;code;paster;charge;|;close

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

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

code.Type=TMenuItem
code.Text=�����ӡ
code.Tip=�����ӡ
code.M=CO
code.Action=onBarCode
code.pic=barcode.gif

paster.Type=TMenuItem
paster.Text=��ӡ��ֽ
paster.Tip=��ӡ��ֽ
paster.Action=onPrintPaster
paster.pic=048.gif

charge.Type=TMenuItem
charge.Text=����Ʒ�
charge.Tip=����Ʒ�
charge.M=H
charge.key=Ctrl+H
charge.Action=onCharge
charge.pic=bill-1.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif