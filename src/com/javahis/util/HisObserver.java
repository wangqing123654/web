package com.javahis.util;

import java.util.Vector;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not WangM
 * @version 1.0
 */
public class HisObserver {
    /**
     * ״̬λ
     */
    private boolean changed = false;
    /**
     * �����б�
     */
    private Vector obs;

    /**
     * ������
     */
    public HisObserver() {
        obs = new Vector();
    }

    /**
     * ��ӹ۲��߶���
     * @param o IodiOrder
     */
    public synchronized void addObserver(IHisObserver o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }
    /**
     * ɾ��һ���۲��߶���
     * @param o OdiObserver
     */
    public synchronized void deleteObserver(HisObserver o) {
        obs.removeElement(o);
    }
    /**
     * ֪ͨ�۲��߶���
     */
    public void notifyObservers() {
        notifyObservers(null);
    }
    /**
     * ֪ͨ�۲��߶���
     * @param arg Object
     */
    public void notifyObservers(Object arg) {
        /**
         * �����б�
         */
        Object[] arrLocal;

        synchronized (this) {
            /**
             * �鿴�����Ƿ�ı�
             */
            if (!changed)
                return;
            /**
             * �õ���ǰ��Ҫ�۲�Ķ����б�
             */
            arrLocal = obs.toArray();
            /**
             * ���״̬λ״̬
             */
            clearChanged();
        }
        /**
         * ���ù۲����ĸ��·���
         */
        for (int i = arrLocal.length-1; i>=0; i--)
            ((IHisObserver)arrLocal[i]).update(this, arg);
    }

    /**
     * ɾ��ȫ���۲��߶���
     */
    public synchronized void deleteObservers() {
        obs.removeAllElements();
    }

    /**
     * ����״̬λΪ�ı�
     */
    protected synchronized void setChanged() {
        changed = true;
    }

    /**
     * ���״̬λ
     */
    protected synchronized void clearChanged() {
        changed = false;
    }

    /**
     * �õ���ǰ״̬λ
     * @return boolean
     */
    public synchronized boolean hasChanged() {
        return changed;
    }
    /**
     * �õ��۲��ߵ�����
     * @return int
     */
    public synchronized int countObservers() {
        return obs.size();
    }

}
