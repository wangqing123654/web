 #
  # Title: 健检团体报到
  #
  # Description:健检团体报到
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;|;openOrder;copyOrder;closeOrder;|;reportSheet;barCode;|;batchAdd;batchDelete;|;excel;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;|;openOrder;copyOrder;closeOrder;|;reportSheet;barCode;|;batchAdd;batchDelete;|;excel;|;clear;close

save.Type=TMenuItem
save.Text=报到
save.Tip=报到
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=取消报到
delete.Tip=取消报到
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=Undo.gif

reportSheet.Type=TMenuItem
reportSheet.Text=导览单
reportSheet.Tip=导览单
reportSheet.M=C
reportSheet.Action=onReportPrint
reportSheet.pic=print.gif


barCode.Type=TMenuItem
barCode.Text=条码
barCode.Tip=条码
barCode.M=C
barCode.Action=onBarCode
barCode.pic=barcode.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

batchAdd.Type=TMenuItem
batchAdd.Text=批量新增
batchAdd.Tip=批量新增
batchAdd.M=
batchAdd.Action=batchAdd
batchAdd.pic=sta-1.gif

batchDelete.Type=TMenuItem
batchDelete.Text=批量删除
batchDelete.Tip=批量删除
batchDelete.M=
batchDelete.Action=batchDelete
batchDelete.pic=delete.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

openOrder.Type=TMenuItem
openOrder.Text=展开体检项目
openOrder.Tip=展开体检项目
openOrder.M=
openOrder.key=
openOrder.Action=onOpenOrder
openOrder.pic=046.gif

copyOrder.Type=TMenuItem
copyOrder.Text=复制体检项目
copyOrder.Tip=复制体检项目
copyOrder.M=
copyOrder.key=
copyOrder.Action=onCopyOrder
copyOrder.pic=exportword.gif

closeOrder.Type=TMenuItem
closeOrder.Text=取消展开
closeOrder.Tip=取消展开
closeOrder.Action=onCloseOrder
closeOrder.pic=032.gif

excel.Type=TMenuItem
excel.Text=汇出Excel
excel.Tip=汇出Excel
excel.M=S
excel.key=Ctrl+S
excel.Action=onExcel
excel.pic=export.gif
