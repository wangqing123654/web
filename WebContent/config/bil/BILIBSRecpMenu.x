<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;clear;|;EKTcard;|;insureInfo;|;outHosp;|;upLoad;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;clear;EKTcard;|;outHosp;upLoad;|;Refresh;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

outHosp.Type=TMenuItem
outHosp.Text=�ٴγ�Ժ
outHosp.Tip=�ٴγ�Ժ
outHosp.M=P
outHosp.Action=onOutHosp
outHosp.pic=tempsave.gif

print.Type=TMenuItem
print.Text=�Զ����ӡ
print.Tip=�Զ����ӡ
print.M=B
print.Action=onPrint
print.pic=print.gif

insureInfo.Type=TMenuItem
insureInfo.Text=����������Ϣ
insureInfo.Tip=����������Ϣ
insureInfo.M=
insureInfo.key=Ctrl+F
insureInfo.Action=onInsureInfo
insureInfo.pic=013.gif
 
upLoad.Type=TMenuItem
upLoad.Text=ҽ���걨
upLoad.Tip=ҽ���걨
upLoad.M=U
upLoad.Action=onInsUpload
upLoad.pic=032.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
//close.Action=onClosePanel
close.Action=onClose
close.pic=close.gif

EKTcard.Type=TMenuItem
EKTcard.Text=ҽ�ƿ�
EKTcard.Tip=ҽ�ƿ�
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

