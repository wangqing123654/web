 #
  # Title: �ٴ�·��չ��
  #
  # Description: �ٴ�·��չ��
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author luhai 2010.05.17
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;deployExecute;|;deployPractice;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;deployExecute;|;deployPractice;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

deployPractice.Type=TMenuItem
deployPractice.Text=·��չ��
deployPractice.Tip=·��չ��
deployPractice.M=A
deployPractice.key=
deployPractice.Action=deployPractice
deployPractice.pic=spreadout.gif

revertPractice.Type=TMenuItem
revertPractice.Text=��ԭʵ��
revertPractice.Tip=��ԭʵ��
revertPractice.M=A
revertPractice.key=
revertPractice.Action=returnPractice
revertPractice.pic=Redo.gif

deployStandard.Type=TMenuItem
deployStandard.Text=չ����׼
deployStandard.Tip=չ����׼
deployStandard.M=A
deployStandard.key=
deployStandard.Action=deployStandard
deployStandard.pic=search-2.gif

deployExecute.Type=TMenuItem
deployExecute.Text=չ��ִ�л���
deployExecute.Tip=չ��ִ�л���
deployExecute.M=A
deployExecute.key=
deployExecute.Action=nurseWorkListOpen
deployExecute.pic=023.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
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


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif