<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;card;|;deptcln;|;drcln;|;close

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
File.Item=save;|;delete;|;card;|;deptcln;|;drcln;|;close

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

delete.Type=TMenuItem
delete.Text=ɾ��
delete.zhText=ɾ��
delete.enText=Delete
delete.Tip=ɾ��
delete.zhTip=ɾ��
delete.enTip=Delete
delete.M=D
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

card.Type=TMenuItem
card.Text=��Ⱦ�����濨
card.zhText=��Ⱦ�����濨
card.enText=DiseaseCard
card.Tip=��Ⱦ�����濨
card.zhTip=��Ⱦ�����濨
card.enTip=DiseaseCard
card.M=C
card.Action=onCard
card.pic=card.gif

deptcln.Type=TMenuItem
deptcln.Text=�����
deptcln.zhText=�����
deptcln.enText=DeptDiag
deptcln.Tip=�����
deptcln.zhTip=�����
deptcln.enTip=DeptDiag
deptcln.M=C
deptcln.Action=onDeptCln
deptcln.pic=emr-1.gif

drcln.Type=TMenuItem
drcln.Text=ҽʦ���
drcln.zhText=ҽʦ���
drcln.enText=DrDiag
drcln.Tip=ҽʦ���
drcln.zhTip=ҽʦ���
drcln.enTip=DrDiag
drcln.M=C
drcln.Action=onDrCln
drcln.pic=emr-2.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Close
close.Tip=�˳�(Alt+F4)
close.zhTip=�˳�(Alt+F4)
close.enTip=Close(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

