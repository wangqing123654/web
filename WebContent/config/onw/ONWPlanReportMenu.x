#############################################
# <p>Title:门诊护士站检验检查报告进度Menu </p>
#
# <p>Description:门诊护士站检验检查报告进度Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2010.02.02
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File
UI.button=query;|;preview;|;print;|;clear;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;close

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

preview.Type=TMenuItem
preview.Text=阅读报告
preview.Tip=阅读报告
preview.M=R
preview.key=Ctrl+R
preview.Action=onTableDoubleCliecked
preview.pic=preview.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=D
clear.key=Ctrl+D
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif