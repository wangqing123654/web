#
  # Title: 会员注册
  #
  # Description:会员注册
  #
  # Copyright: Bluecore (c) 2014
  #
  # @author duzhw
  # @version 4.5
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;query;|;EKTcard;|;idcard;|;buycard;|;makecard;|;MEMprint;|;hl;|;Wrist;|;crmreg;|;updateBirth;|;clear;|;close;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;new;|;query;|;EKTcard;|;idcard;|;buycard;|;makecard;|;MEMprint;|;hl;|;Wrist;|;crmreg;|;updateBirth;|;clear;|;close;|

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif

EKTcard.Type=TMenuItem
EKTcard.Text=医疗卡
EKTcard.Tip=医疗卡
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

buycard.Type=TMenuItem
buycard.Text=购卡
buycard.Tip=购卡
buycard.M=R
buycard.key=F5
buycard.Action=onBuy
buycard.pic=038.gif

makecard.Type=TMenuItem
makecard.Text=制卡
makecard.Tip=制卡
makecard.M=M
makecard.key=F5
makecard.Action=onEKTBuy
makecard.pic=sta.gif

idcard.Type=TMenuItem
idcard.Text=身份证
idcard.Tip=身份证
idcard.M=R
idcard.key=F5
idcard.Action=onIdCard
idcard.pic=idcard.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

new.Type=TMenuItem
new.Text=新增
new.zhText=新增
new.enText=New
new.Tip=新增
new.zhTip=新增
new.enTip=New
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

MEMprint.Type=TMenuItem
MEMprint.Text=补印
MEMprint.Tip=补印
MEMprint.M=P
MEMprint.key=Ctrl+P
MEMprint.Action=onRePrint
MEMprint.pic=print_red.gif

hl.Type=TMenuItem
hl.Text=历史交易
hl.zhText=历史交易
hl.enText=历史交易
hl.Tip=历史交易
hl.zhTip=历史交易
hl.enTip=历史交易
hl.M=S
hl.Action=onHisInfo
hl.pic=048.gif

Wrist.Type=TMenuItem
Wrist.Text=打印
Wrist.Tip=打印
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

crmreg.Type=TMenuItem
crmreg.Text=CRM预约信息
crmreg.Tip=CRM预约信息
crmreg.M=C
crmreg.Action=onCRM
crmreg.pic=032.gif


updateBirth.Type=TMenuItem
updateBirth.Text=新生儿出生日期
updateBirth.Tip=新生儿出生日期
updateBirth.M=U
updateBirth.key=Ctrl+U
updateBirth.Action=onUpdateBirthDate
updateBirth.pic=time.gif