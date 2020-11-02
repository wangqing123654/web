 #
  # Title: 静点室执行
  #
  # Description: 静点室执行
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.05.07
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ekt;|;skiResult;|;clear;|;card;|;print;|;bedout;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;skiResult;|;clear;|;card;|;print;|;bedout;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

print.Type=TMenuItem
print.Text=打印执行单
print.Tip=打印执行单
print.M=P
print.Action=onPrint
print.pic=print.gif

card.Type=TMenuItem
card.Text=床头卡
card.Tip=床头卡
card.M=P
card.Action=onCard
card.pic=card.gif


bedout.Type=TMenuItem
bedout.Text=离院
bedout.Tip=离院
bedout.M=P
bedout.Action=onBedOut
bedout.pic=export.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

ekt.Type=TMenuItem
ekt.Text=医疗卡
ekt.Tip=医疗卡
ekt.M=S
ekt.key=
ekt.Action=onEkt
ekt.pic=042.gif

skiResult.Type=TMenuItem
skiResult.Text=皮试结果
skiResult.Tip=皮试结果
skiResult.M=P
skiResult.key=Ctrl+P
skiResult.Action=onSkiResult
skiResult.pic=032.gif

skiResult.Type=TMenuItem
skiResult.Text=取医嘱
skiResult.Tip=取医嘱
skiResult.M=P
skiResult.key=
skiResult.Action=onFetch
skiResult.pic=054.gif

