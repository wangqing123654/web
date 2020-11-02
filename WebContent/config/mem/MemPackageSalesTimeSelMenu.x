 #
  # Title: 套餐销售
  #
  # Description: 套餐销售
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author duzhw 2014.01.08
 # @version 4.5
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;EKTcard;|;patinfoquery;|;package;|;caseHistory;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;EKTcard;|;patinfoquery;|;package;|;caseHistory;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

EKTcard.Type=TMenuItem
EKTcard.Text=医疗卡
EKTcard.Tip=医疗卡
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

package.Type=TMenuItem
package.Text=已购套餐
package.Tip=已购套餐
package.M=
package.key=
package.Action=onPackage
package.pic=openbill-2.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

caseHistory.Type=TMenuItem
caseHistory.Text=就诊记录
caseHistory.Tip=就诊记录
caseHistory.M=C
caseHistory.Action=onCaseHistory
caseHistory.pic=032.gif

EKTcard.Type=TMenuItem
EKTcard.Text=医疗卡
EKTcard.Tip=医疗卡
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif



clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


