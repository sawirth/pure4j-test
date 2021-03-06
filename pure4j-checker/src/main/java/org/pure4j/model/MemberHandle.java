package org.pure4j.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;


/**
 * Handle to a class member (method or field).
 * Also contains static utility functions for converting from string name of class back into
 * Class object, and doing the same with methods and fields.
 * 
 * @author moffatr
 * 
 */
public abstract class MemberHandle extends AbstractHandle<AccessibleObject> implements AnnotatedElementHandle<AccessibleObject> {

	protected String className;
	protected String name;
	protected String desc;
	protected int line;
	
	public MemberHandle(String className, String name, String desc, int line) {
		super();
		this.className = className;
		this.name = name;
		this.desc = desc;
		this.line = line;
	}

	public String getClassName() {
		return className;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getLine() {
		return line;
	}

	@Override
	public String toString() {
		return className + "." + name + (desc == null ? "" : desc)+(line != 0 ? " @line="+line : "");
	}

	public int compareTo(AnnotatedElementHandle<?> oo) {
	    if (oo instanceof MemberHandle) {
	    	MemberHandle o = (MemberHandle) oo;
	    	return (className+desc+name).compareTo(o.className+o.desc+o.name);
	    } else {
	    	return 0;
	    }
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((className == null) ? 0 : className.hashCode());
	    result = prime * result + ((desc == null) ? 0 : desc.hashCode());
	    result = prime * result + ((name == null) ? 0 : name.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (!(obj instanceof MemberHandle)) 
		return false;
	    MemberHandle other = (MemberHandle) obj;
	    if (className == null) {
		if (other.className != null)
		    return false;
	    } else if (!className.equals(other.className))
		return false;
	    if (desc == null) {
		if (other.desc != null)
		    return false;
	    } else if (!desc.equals(other.desc))
		return false;
	    if (name == null) {
		if (other.name != null)
		    return false;
	    } else if (!name.equals(other.name))
		return false;
	    return true;
	}
	
	public abstract AccessibleObject hydrate(ClassLoader cl);
	
	public abstract <T extends Annotation> T getAnnotation(ClassLoader cl, Class<T> c);
	
	public abstract java.lang.reflect.Type[] getGenericTypes(ClassLoader cl);
	
	public abstract Class<?>[] getRawTypes(ClassLoader cl);
	
	public String getSignature() {
		return name+(desc != null ? desc.substring(0, desc.lastIndexOf(")")+1) : "");
	}

	public abstract int getModifiers(ClassLoader cl);
}
