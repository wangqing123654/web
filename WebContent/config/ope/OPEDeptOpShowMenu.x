#############################################
# <p>Title:�Ƴ���������ѡ�����Menu </p>
#
# <p>Description:�Ƴ���������ѡ�����Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.26
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File
UI.button=back;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=save;|;close

back.Type=TMenuItem
back.Text=�ش�ѡ��ֵ
back.zhText=�ش�ѡ��ֵ
back.enText=Fetch
back.Tip=�ش�ѡ��ֵ
back.zhTip=�ش�ѡ��ֵ
back.enTip=Fetch
back.Action=onBack
back.pic=Undo.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Close
close.Tip=�˳�(Alt+F4)
close.zhTip=�˳�
close.enTip=Close
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
