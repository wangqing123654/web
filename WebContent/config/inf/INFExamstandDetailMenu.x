 #
 # Title: ���Ҽ�ⷽ�������Ŀ����
 #
 # Description: ���Ҽ�ⷽ�������Ŀ����
 #
 # Copyright: JavaHis (c) 2009
 #
 # @author sundx 2009.10.15
 # @version 1.0
 
 <Type=TMenuBar>
 UI.Item=File;Window
 UI.button=save;new;delete;|;clear;|;close
 
 Window.Type=TMenu
 Window.Text=����
 Window.M=W
 Window.Item=Refresh

 File.Type=TMenu
 File.Text=�ļ�
 File.M=F
 File.Item=save;new;delete;|;clear;|;close

 save.Type=TMenuItem
 save.Text=����
 save.Tip=����(Ctrl+S)
 save.M=S
 save.key=Ctrl+S
 save.Action=onSave
 save.pic=save.gif

 new.Type=TMenuItem
 new.Text=����
 new.Tip=����(Ctrl+N)
 new.M=N
 new.key=Ctrl+N
 new.Action=onNew
 new.pic=039.gif

 delete.Type=TMenuItem
 delete.Text=ɾ��
 delete.Tip=ɾ��(Delete)
 delete.M=N
 delete.key=Delete
 delete.Action=onDelete
 delete.pic=delete.gif

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
 
 Refresh.Type=TMenuItem
 Refresh.Text=ˢ��
 Refresh.Tip=ˢ��
 Refresh.M=R
 Refresh.key=F5
 Refresh.Action=onReset
 Refresh.pic=Refresh.gif