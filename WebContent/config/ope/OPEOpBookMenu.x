#############################################
# <p>Title:��������Menu </p>
#
# <p>Description:��������Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.23
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;cancel;|;clear;|;blood;|;patInfo;|;Detail;|;close

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
File.Item=save;|;cancel;|;clear;|;blood;|;patInfo;|;Detail;|;close

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

cancel.Type=TMenuItem
cancel.Text=ȡ������
cancel.zhText=ȡ������
cancel.enText=Cancel
cancel.Tip=ȡ������
cancel.zhTip=ȡ������
cancel.enTip=Cancel
cancel.M=
cancel.key=
cancel.Action=onCancel
cancel.pic=004.gif

ana.Type=TMenuItem
ana.Text=��������
ana.zhText=��������
ana.enText=Anesthetize
ana.Tip=��������
ana.zhTip=��������
ana.enTip=Anesthetize
ana.M=
ana.key=
ana.Action=onANA
ana.pic=PHL.gif

blood.Type=TMenuItem
blood.Text=��Ѫ֪ͨ
blood.zhText=��Ѫ֪ͨ
blood.enText=Blood Notice
blood.Tip=��Ѫ֪ͨ
blood.zhTip=��Ѫ֪ͨ
blood.enTip=Blood Notice
blood.Action=onBlood
blood.pic=blood.gif

patInfo.Type=TMenuItem
patInfo.Text=��������
patInfo.zhText=��������
patInfo.enText=Pat Profile
patInfo.Tip=��������
patInfo.zhTip=��������
patInfo.enTip=Pat Profile
patInfo.Action=onPatInfo
patInfo.pic=038.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��(F5)
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Empty
clear.Tip=���(Ctrl+Z)
clear.zhTip=���
clear.enTip=Empty
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�(Alt+F4)
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Detail.Type=TMenuItem
Detail.Text=������¼
Detail.zhText=������¼
Detail.enText=Detail
Detail.Tip=������¼
Detail.zhTip=������¼
Detail.enTip=Detail
Detail.M=
Detail.key=
Detail.Action=onDetail
Detail.pic=046.gif