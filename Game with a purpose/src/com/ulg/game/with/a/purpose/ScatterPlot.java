package com.ulg.game.with.a.purpose;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Hervé de Sélys
 *
 */
@SuppressWarnings("serial")
public class ScatterPlot implements Serializable {
	
	private int id;
	private List<Instance> instanceSet;
	private Pair pairAttribute;
	private float minX;
    private float maxX; 
    private float minY;
    private float maxY; 
	
	
	public ScatterPlot(int id, List<Instance> set, Pair pair){
		
		instanceSet = set;
		pairAttribute = pair;
		this.id = id;
		minX = findMinX();
		maxX = findMaxX();
		minY = findMinY();
		maxY = findMaxY();
	}
	public int getId(){
		return id;
	}
	
	public List<Instance> getInstanceSet(){
		return instanceSet;
	}
	
	public Pair getPairAttribute(){
		return pairAttribute;
	}
	
	public float getMinX(){
		return minX;
	}
	
	public float getMaxX(){
		return maxX;
	}
	
	public float getMinY(){
		return minY;
	}
	
	public float getMaxY(){
		return maxY;
	}
	
	private float findMinX(){
		int firstAttribute = pairAttribute.getFirst();
		List<Attribute> firstRecord = instanceSet.get(0).getAttributeList();
		float min = firstRecord.get(firstAttribute).getValue();
		
		for (int i=0; i < instanceSet.size(); i++){
			List<Attribute> record = instanceSet.get(i).getAttributeList();
			float current = record.get(firstAttribute).getValue();
			
			if (current < min)
				min = current;
		}		
		return min;
	}
	
	private float findMaxX(){
		int firstAttribute = pairAttribute.getFirst();
		List<Attribute> firstRecord = instanceSet.get(0).getAttributeList();
		float max = firstRecord.get(firstAttribute).getValue();
		
		for (int i=0; i < instanceSet.size(); i++){
			List<Attribute> record = instanceSet.get(i).getAttributeList();
			float current = record.get(firstAttribute).getValue();
			
			if (current > max)
				max = current;
		}		
		return max;
	}
	
	private float findMinY(){
		int secondAttribute = pairAttribute.getSecond();
		List<Attribute> firstRecord = instanceSet.get(0).getAttributeList();
		float min = firstRecord.get(secondAttribute).getValue();
		
		for (int i=0; i < instanceSet.size(); i++){
			List<Attribute> record = instanceSet.get(i).getAttributeList();
			float current = record.get(secondAttribute).getValue();
			
			if (current < min)
				min = current;
		}		
		return min;
	}
	
	private float findMaxY(){
		int secondAttribute = pairAttribute.getSecond();
		List<Attribute> firstRecord = instanceSet.get(0).getAttributeList();
		float max = firstRecord.get(secondAttribute).getValue();
		
		for (int i=0; i < instanceSet.size(); i++){
			List<Attribute> record = instanceSet.get(i).getAttributeList();
			float current = record.get(secondAttribute).getValue();
			
			if (current > max)
				max = current;
		}		
		return max;
	}
}