############################################
# <p>Title:ת������Menu </p>
#
# <p>Description:ת������Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 1.0
############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif