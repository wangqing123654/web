UI.Title=TBuilder 1.0
UI.MenuConfig=%ROOT%\config\tbuilder\main\TBuilderMenu.x
UI.ToolBar=Y
UI.Width=1024
UI.Height=740
UI.centerWindow=N
UI.ControlClassName=com.tbuilder.TBuilderControl
UI.Item=WorkBack
UI.Layout=BorderLayout

WorkBack.type=TSplitPane
WorkBack.dividerLocation=<i>200
WorkBack.item=WorkLeft|Left;WorkRight|Right

WorkLeft.type=TSplitPane
WorkLeft.orientation=0
WorkLeft.dividerLocation=<i>200
WorkLeft.item=ProjectTree|Left;StructureTree|Right


ProjectTree.type=TTree
ProjectTree.Name=Tree
ProjectTree.pics=Root:sys.gif;Path:dir1.gif:dir1.gif;UI:refurbish.gif;Module:table.gif
ProjectTree.showID=false
ProjectTree.showName=false
ProjectTree.showValue=false
ProjectTree.outdropPopupMenuTag=Popup1
ProjectTree.typePopupMenuTags=Path:PopupPathMenu;File:PathFile
ProjectTree.rootPopupMenuTags=PopupRootMenu
ProjectTree.itemPopupMenuTags=PopupItem

StructureTree.type=TTree

WorkRight.type=TTabbedPane
//WorkRight.bkcolor=��

NewPojectDialogConfig=%ROOT%\config\tbuilder\dialog\NewPojectDialog.x
//�½�Ŀ¼�Ի���
NewDirDialogConfig=%ROOT%\config\tbuilder\dialog\NewDirDialog.x
//�½�UI�Ի���
NewUIDialogConfig=%ROOT%\config\tbuilder\dialog\NewUIDialog.x
UIPanelConfig=%ROOT%\config\tbuilder\work\UIPanel.x
ModulePanelConfig=%ROOT%\config\tbuilder\work\ModulePanel.x
DataWindowPanelConfig=%ROOT%\config\tbuilder\work\DWPanel.x
UIObjectConfig=%ROOT%\config\tbuilder\work\OPanel.x

Title=TBuilder 1.0

PopupRootMenu.item=ProjectTreeRefurbishMenu
PopupPathMenu.item=createUIMenu;createDirMenu;|;ProjectTreeRefurbishMenu

ProjectTreeRefurbishMenu.type=TMenuItem
ProjectTreeRefurbishMenu.text=ˢ��
ProjectTreeRefurbishMenu.tip=ˢ��
ProjectTreeRefurbishMenu.M=R
ProjectTreeRefurbishMenu.Action=onProjectTreeRefurbish
//ProjectTreeRefurbishMenu.pic=Refresh.gif

createDirMenu.type=TMenuItem
createDirMenu.text=�½�Ŀ¼
createDirMenu.tip=�½�Ŀ¼
createDirMenu.M=C
createDirMenu.Action=onCreateDir

createUIMenu.type=TMenuItem
createUIMenu.text=�½�UI
createUIMenu.tip=�½�UI
createUIMenu.M=U
createUIMenu.Action=onCreateUI