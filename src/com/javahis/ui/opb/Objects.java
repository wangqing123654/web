package com.javahis.ui.opb;
import java.io.Serializable;

/**
 * ËÕ¿­×¨ÓÃ
 * @author sukai
 */
public  class Objects implements Serializable{
	
   
	private static final long serialVersionUID = -6228472271263229031L;

	public static String toString(Object o, String nullDefault) {
        return (o != null) ? o.toString() : nullDefault;
    }

	public static String toString(Object o) {
        return String.valueOf(o);
    }

}
