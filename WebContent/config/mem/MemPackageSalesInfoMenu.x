 #
  # Title: Ì×²Í¹ºÂò²éÑ¯
  #
  # Description: Ì×²Í¹ºÂò²éÑ¯
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author duzhw 2014.02.21
 # @version 4.5
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;read;|;reprint;|;clear;|;close

Window.Type=TMenu
Window.Text=´°¿Ú
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=ÎÄ¼þ
File.M=F
File.Item=query;|;read;|;reprint;|;clear;|;close


query.Type=TMenuItem
query.Text=²éÑ¯
query.Tip=²éÑ¯(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

read.Type=TMenuItem
read.Text=Ò½ÁÆ¿¨
read.Tip=Ò½ÁÆ¿¨(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

reprint.Type=TMenuItem
reprint.Text=²¹Ó¡
reprint.Tip=²¹Ó¡(Ctrl+P)
reprint.M=P
reprint.key=Ctrl+P
reprint.Action=onRePrint
reprint.pic=print_red.gif

clear.Type=TMenuItem
clear.Text=Çå¿Õ
clear.Tip=Çå¿Õ(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=ÍË³ö
close.Tip=ÍË³ö(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


