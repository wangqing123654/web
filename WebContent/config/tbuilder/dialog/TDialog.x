<Type=TFrame>
UI.Title=TDialog
UI.Width=500
UI.Height=420
UI.Layout=BL
UI.Modal=true
UI.Item=PWork|��;PButton|��

BL.Type=BorderLayout

PWork.Type=TPanel
PWork.Layout=BL
PWork.Item=PicPanel|��;WorkPanel|��;Line|��

PicPanel.Type=TPanel
PicPanel.Layout=BFL
PicPanel.Item=LPic

LPic.Type=TLabel
LPic.Pic=p1.JPG

WorkPanel.Type=TPanel
WorkPanel.Layout=null

Line.Type=TLine

PButton.Type=TPanel
PButton.Layout=BFL
PButton.Item=OK;Cancel;Refurbish

BFL.Type=FlowLayout|<i>2|<i>10|<i>10

OK.Type=TButton
OK.Text=ȷ��
OK.Action=onOK

Cancel.Type=TButton
Cancel.Text=ȡ��
Cancel.Key=ESC
Cancel.Action=onClose

Refurbish.Type=TButton
Refurbish.Text=ˢ��
Refurbish.Action=onReset