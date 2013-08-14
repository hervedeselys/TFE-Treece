package com.ulg.game.with.a.purpose;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents an instance of the database. Each line of the
 * database contains a list of attribute and belongs to a certain class (output).
 * We identify each line by an id.
 * 
 * @author Hervé de Sélys
 *
 */

@SuppressWarnings("serial")
public class Instance implements Serializable{
	
	private List<Attribute> attributeList;
	private String className;
	private int instanceId;
	private int outpoutId;
	
	public Instance(int id, List<Attribute> l, String o){
		attributeList = l;
		className = o;
		instanceId = id;
	}
	
	public List<Attribute> getAttributeList(){
		return attributeList;
	}
	
	public String getClassName(){
		return className;
	}
	
	public int getInstanceId(){
		return instanceId;
	}
	
	public int getOutputId(){
		return outpoutId;
	}
	
	public void setOutputId(int o){
		outpoutId = o;
	}
	
}

@SuppressWarnings("serial")
class Attribute implements Serializable{
	
	private String attributeName;
	private Float value;
	
	public Attribute(String n, Float v){
		attributeName = n;
		value = v;
	}
	
	public String getField(){
		return attributeName;
	}
	
	public Float getValue(){
		return value;
	}
	
} 
