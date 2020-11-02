package com.javahis.ui.testOpb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Column
 * @author zhangp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Column {
	/**
	 * ����
	 * @return
	 */
	String name();
	
	int type();
}
