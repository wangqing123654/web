package jdo.emr;

import com.dongyang.tui.text.MFile;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.MTDSave;
import com.dongyang.tui.text.MTable;
import com.dongyang.tui.text.MTextSave;
import com.dongyang.tui.text.MTRSave;
import com.dongyang.tui.text.MPanel;
import com.dongyang.ui.TWord;

/**
 * <p>Title: ��word</p>
 *
 * <p>Description:��WORD </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */

public class OpenWord {

    /**
     * word
     */
    TWord word;

    /**
     * ʵ��
     */
    private static OpenWord instanceObject;

    /**
     * ȡ��ʵ��
     * @return OpenWord
     */
    public static OpenWord getInstance() {
        if (instanceObject == null)
            instanceObject = new OpenWord();
        return instanceObject;
    }

    /**
     * ������
     */
    public OpenWord() {
    }

    /**
     * �ĵ��ļ�������
     * @return MFile
     */
    private MFile getFileManager() {
        return word.getFileManager();
    }

    /**
     * ��word
     * @param path String
     * @param fileName String
     * @param type int
     * @param state boolean
     * @param word TWord
     * @return boolean
     */
    public boolean onOpen(String path, String fileName, int type, boolean state,TWord word) {
        this.word = word;
        getFileManager().clear();
        String serverFileName = getFileManager().getServerFileName(path,
                fileName);
        if (serverFileName == null || serverFileName.length() == 0)
            return false;
        getFileManager().setTempFileName(getFileManager().getLocalTempFileName("jhw"));
        switch (type) {
        case 0:
            getFileManager().setTempFileName(serverFileName);
            break;
        case 1:
            if (!TIOM_AppServer.readFileToLocal(serverFileName,
                                                getFileManager().getTempFileName())) {
                return false;
            }
            break;
        case 2:
            serverFileName = getFileManager().getEmrTempletDir() +
                             serverFileName;
            if (!TIOM_FileServer.readFileToLocal(TIOM_FileServer.getSocket(),
                                                 serverFileName,
                                                 getFileManager().getTempFileName())) {
                return false;
            }
            break;
        case 3:
            serverFileName = getFileManager().getEmrDataDir() + serverFileName;
            if (!TIOM_FileServer.readFileToLocal(TIOM_FileServer.getSocket(),
                                                 serverFileName,
                                                 getFileManager().getTempFileName())) {
                return false;
            }
            break;
        }
        getFileManager().setOpenServerType(type);
        getFileManager().setPath(path);
        getFileManager().setFileName(fileName);
        word.setName(path + fileName);
        return  openTempFile(state);
    }

    /**
     * ����ʱ�ļ�
     * @param state boolean
     * @return boolean
     */
    private boolean openTempFile(boolean state) {
        getFileManager().getStringManager().removeAll();
        getFileManager().getPageManager().removeAll();
        getFileManager().getCTableManager().removeAll();
        getFileManager().getMacroroutineManager().removeAll();
        getFileManager().getSyntaxManager().clear();
        getFileManager().openLocalTempFile(getFileManager().getTempFileName());
        readData();
        getFileManager().closeInStream();
        if (state)
            getFileManager().getFocusManager().onPreviewWord();
        else
            getFileManager().getFocusManager().onEditWord();
        getFileManager().getSyntaxManager().onOpen();
        getFileManager().update();
        return true;
    }

    /**
     * ������������
     * @return boolean
     */
    private boolean readData() {
        try {
            getFileManager().setPanelManager(new MPanel());
            getFileManager().setTDSaveManager(new MTDSave());
            getFileManager().setTDSaveUniteManager(new MTDSave());
            getFileManager().setTRSaveManager(new MTRSave());
            getFileManager().setTextSaveManager(new MTextSave());
            getFileManager().setTableManager(new MTable());
            getFileManager().getImageManager().removeAll();
            getFileManager().readObject(getFileManager().getInStream());
            getFileManager().setPanelManager(null);
            getFileManager().setTDSaveManager(null);
            getFileManager().setTDSaveUniteManager(null);
            getFileManager().setTRSaveManager(null);
            getFileManager().setTextSaveManager(null);
            getFileManager().setTableManager(null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
