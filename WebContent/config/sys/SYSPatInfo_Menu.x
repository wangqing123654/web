<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;new;delete;query;querycrm;querypat;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.enText=Window
Window.enTip=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.enText=File
File.enTip=File
File.M=F
File.Item=save;new;delete;Refresh;query;querycrm;querypat;|;clear;|;regist;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.enText=save
save.enTip=save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.enText=Add
new.enTip=Add
new.M=S
new.key=Ctrl+S
new.Action=onNew
new.pic=039.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.enText=delete
delete.enTip=delete
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.enTip=query
query.enText=query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


querycrm.Type=TMenuItem
querycrm.Text=CRMԤԼ��Ϣ��ѯ
querycrm.Tip=CRMԤԼ��Ϣ��ѯ
querycrm.enTip=query
querycrm.enText=query
querycrm.M=Q
querycrm.key=Ctrl+F
querycrm.Action=onCrmQuery
querycrm.pic=query.gif

querypat.Type=TMenuItem
querypat.Text=������ѯ
querypat.Tip=������ѯ
querypat.M=Q
querypat.key=Ctrl+F
querypat.Action=onQueryPat
querypat.pic=search-1.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.enText=Refresh
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.enTip=Empty
clear.enText=Empty
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

regist.Type=TMenuItem
regist.Text=ע���������
regist.Tip=ע���������
regist.M=R
regist.key=
regist.Action=onRegist
regist.pic=007.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.enText=Log out
close.enTip=Log out
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

