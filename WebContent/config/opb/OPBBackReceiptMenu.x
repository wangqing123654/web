<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;fill;chnEnfill;|;print;|;save;|;top;|;backReceipt;|;close;|;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=fill;chnEnfill;|;query;|;print;|;save;|;top;|;close

save.Type=TMenuItem
save.Text=退票
save.Tip=退票
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Undo.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

backReceipt.Type=TMenuItem
backReceipt.Text=费用明细查询
backReceipt.Tip=费用明细查询
backReceipt.M=
backReceipt.key=
backReceipt.Action=onBackReceipt
backReceipt.pic=detail-1.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

fill.Type=TMenuItem
fill.Text=费用结算清单
fill.Tip=费用结算清单
fill.M=F
fill.key=Ctrl+H
fill.Action=onFill
fill.pic=detail.gif

chnEnfill.Type=TMenuItem
chnEnfill.Text=中英文费用清单
chnEnfill.Tip=中英文费用清单
chnEnfill.M=E
chnEnfill.key=Ctrl+E
chnEnfill.Action=onChnEnFill
chnEnfill.pic=patlist.gif

print.Type=TMenuItem
print.Text=补打
print.Tip=票据补打
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=print_red.gif

top.Type=TMenuItem
top.Text=医疗卡充值
top.Tip=医疗卡充值
top.M=T
top.key=Ctrl+T
top.Action=onTop
top.pic=bill.gif


