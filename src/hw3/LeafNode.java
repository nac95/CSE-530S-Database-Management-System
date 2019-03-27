package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw1.StringField;
import hw1.Type;

public class LeafNode implements Node {
	
	private int degree;
	private ArrayList<Entry> entries = new ArrayList<>();
	private Node parent;
	private boolean root;
	
	public LeafNode(int degree) {
		//your code here
		this.degree = degree;
		this.root = false;
	}
	
	public ArrayList<Entry> getEntries() {
		//your code here
		return entries;
	}

	public int getDegree() {
		//your code here
		return degree;
	}
	
	public boolean isLeafNode() {
		return true;
	}
	
	public boolean isRoot() {
		return this.root;
	}
	
	public boolean isExceedOne() {
		// may not be right
		if(this.degree < this.entries.size()) {
			return true;
		}else {
			return false;
		}
	}
	
	public Node getParent() {
		return this.parent;
	}
	
	public void setRoot(boolean b) {
		this.root = b;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public ArrayList<Node> getChildren(){
		return null;
	}
	
	public Field getMaxKey() {
		Field max = null;
		System.out.println("default max"+max);
		for(Entry ne: this.entries) {
			Field f = ne.getField();
			if(max == null || f.compare(RelationalOperator.GTE, max)) {
				max = f;
			}
		}
		System.out.println("max"+max);
		return max;
	}
	
	public void addKeys(Entry entry) {
		if (entries.size() == 0) {
			entries.add(entry);
			return; 
		}
		boolean add = false;
		if (entry.getField().getType() == Type.INT) {
//			byte[] newEntryByte = entry.getField().toByteArray();
//			int newEntry = new IntField(newEntryByte).getValue();
			for (int i = 0; i < entries.size(); i++) {
//				byte[] oldEntryByte = entries.get(i).getField().toByteArray();
//				int oldEntry = new IntField(oldEntryByte).getValue();
				if (entry.getField().compare(RelationalOperator.LT, entries.get(i).getField())) {
					entries.add(i, entry);
					add = true;
					break;
				}
			}
		}
		if (entry.getField().getType() == Type.STRING) {
			byte[] newEntryByte = entry.getField().toByteArray();
			String newEntry = new StringField(newEntryByte).getValue();
			for (int i = 0; i < entries.size(); i++) {
				byte[] oldEntryByte = entries.get(i).getField().toByteArray();
				String oldEntry = new StringField(oldEntryByte).getValue();
				if (newEntry.compareTo(oldEntry) < 0) {
					entries.add(i, entry);
					add = true;
					break;
				}
			}
		}
		if (!add) {
			entries.add(entry);
		}
	}
	
	public void removeEntry(Entry en) {
		
		try{
			this.entries.remove(en);
		}catch(Exception e) {
			
		}
		
	}

}