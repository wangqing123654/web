#
  # Title: סԺ���÷ָ�
  #
  # Description:סԺ���÷ָ�
  #
  # Copyright: Bluecore(c) 2012
  #
  # @author pangben 2012-2-3
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;changeInfo;|;apply;|;upload;|;onSave;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;changeInfo;|;apply;|;upload;|;onSave;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

changeInfo.Type=TMenuItem
changeInfo.Text=ת������������
changeInfo.Tip=ת������������
changeInfo.M=I
changeInfo.Action=onQueryInfo
changeInfo.pic=pat.gif

apply.Type=TMenuItem
apply.Text=ת�걨
apply.Tip=ת�걨
apply.M=A
apply.Action=onApply
apply.pic=sta-1.gif

upload.Type=TMenuItem
upload.Text=�ָ�
upload.Tip=�ָ�
upload.M=U
upload.Action=onUpdate
upload.pic=016.gif

onSave.Type=TMenuItem
onSave.Text=����
onSave.Tip=����
onSave.M=S
onSave.Action=onSettlement
onSave.pic=018.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif