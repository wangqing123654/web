 #
  # Title: 医疗卡制卡操作
  #
  # Description: 医疗卡制卡操作
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;renew;|;cardprint;|;EKTcard;|;MEMcard;|;showpat;|;idcard;|;updateEKTpwd;|;reg;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;renew;|;cardprint;|;EKTcard;|;MEMcard;|;showpat;|;idcard;|;updateEKTpwd;|;reg;|;clear;|;close

reg.Type=TMenuItem
reg.Text=挂号
reg.Tip=挂号(Ctrl+R)
reg.M=R
reg.key=Ctrl+R
reg.Action=onReg
reg.pic=time.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

renew.Type=TMenuItem
renew.Text=补写卡
renew.Tip=补写卡
renew.M=R
renew.key=F5
renew.Action=onRenew
renew.pic=idcard.gif

cardprint.Type=TMenuItem
cardprint.Text=卡片打印
cardprint.Tip=卡片打印
cardprint.M=D
cardprint.key=Ctrl+D
cardprint.Action=onPrint
cardprint.pic=print.gif

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



EKTcard.Type=TMenuItem
EKTcard.Text=医疗卡
EKTcard.Tip=医疗卡
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

MEMcard.Type=TMenuItem
MEMcard.Text=会员卡售卡
MEMcard.Tip=会员卡售卡
MEMcard.M=E
MEMcard.Action=onMEMcard
MEMcard.pic=sta.gif

idcard.Type=TMenuItem
idcard.Text=二代身份证
idcard.Tip=二代身份证
idcard.M=M
idcard.Action=onIdCard
idcard.pic=038.gif

updateEKTpwd.Type=TMenuItem
updateEKTpwd.Text=医疗卡修改密码
updateEKTpwd.Tip=医疗卡修改密码
updateEKTpwd.M=U
updateEKTpwd.Action=updateEKTPwd
updateEKTpwd.pic=007.gif

showpat.Type=TMenuItem
showpat.Text=新注册患者
showpat.Tip=新注册患者
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onNewPat
showpat.pic=patlist.gif
