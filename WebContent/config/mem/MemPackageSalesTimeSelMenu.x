 #
  # Title: �ײ�����
  #
  # Description: �ײ�����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author duzhw 2014.01.08
 # @version 4.5
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;EKTcard;|;patinfoquery;|;package;|;caseHistory;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;EKTcard;|;patinfoquery;|;package;|;caseHistory;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

EKTcard.Type=TMenuItem
EKTcard.Text=ҽ�ƿ�
EKTcard.Tip=ҽ�ƿ�
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

package.Type=TMenuItem
package.Text=�ѹ��ײ�
package.Tip=�ѹ��ײ�
package.M=
package.key=
package.Action=onPackage
package.pic=openbill-2.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

caseHistory.Type=TMenuItem
caseHistory.Text=�����¼
caseHistory.Tip=�����¼
caseHistory.M=C
caseHistory.Action=onCaseHistory
caseHistory.pic=032.gif

EKTcard.Type=TMenuItem
EKTcard.Text=ҽ�ƿ�
EKTcard.Tip=ҽ�ƿ�
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif



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


