##############################################
# <p>Title:��Ժ֪ͨMenu </p>
#
# <p>Description:��Ժ֪ͨMenu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2010-2-25
# @version 1.0
##############################################
<Type=TMenuBar>
UI.Item=File
UI.button=save;|;clear;|;print;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;clear;|;print;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

print.Type=TMenuItem
print.Text=��Ժ֪ͨ��ӡ
print.Tip=��Ժ֪ͨ��ӡ
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=Z
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif