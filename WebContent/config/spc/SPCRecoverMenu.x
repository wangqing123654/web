 #
  # Title: HRM�ײ��趨
  #
  # Description:HRM�ײ��趨
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;query;clear;|;close

saveCom.Type=TMenuItem
saveCom.Text=����
saveCom.Tip=����
saveCom.M=S
saveCom.key=Ctrl+S
saveCom.Action=onSaveCom
saveCom.pic=013.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=S
query.key=
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


saveContractM.Type=TMenuItem
saveContractM.Text=���������ͬ��Ϣ
saveContractM.Tip=���������ͬ��Ϣ
saveContractM.M=Q
saveContractM.key=Ctrl+F
saveContractM.Action=onSaveContractM
saveContractM.pic=038.gif

saveContractD.Type=TMenuItem
saveContractD.Text=����Ա����Ϣ
saveContractD.Tip=����Ա����Ϣ
saveContractD.M=Q
saveContractD.key=Ctrl+F
saveContractD.Action=onSaveContractD
saveContractD.pic=036.gif

newPat.Type=TMenuItem
newPat.Text=�½�Ա����Ϣ
newPat.Tip=�½�Ա����Ϣ
newPat.M=Q
newPat.key=
newPat.Action=onInsertPat
newPat.pic=sta-1.gif

newPats.Type=TMenuItem
newPats.Text=���������Ϣ
newPats.Tip=���������Ϣ
newPats.M=Q
newPats.key=
newPats.Action=onInsertPatByExl
newPats.pic=convert.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
