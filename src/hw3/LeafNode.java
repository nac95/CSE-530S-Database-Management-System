package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.IntField;
import hw1.StringField;
import hw1.Type;

public class LeafNode implements Node {
	
	private int degree;
	private ArrayList<Entry> entries = new ArrayList<>();
	private Node parent;
	
	
	public LeafNode(int degree) {
		//your code here
		this.degree = degree;
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
	
	public boolean isFull() {
		// may not be right
		if(this.degree > this.entries.size()) {
			return true;
		}else {
			return false;
		}
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public void addKeys(Entry entry) {
		if (entries.size() == 0) {
			entries.add(entry);
		}
		boolean add = false;
		if (entry.getField().getType() == Type.INT) {
			byte[] newEntryByte = entry.getField().toByteArray();
			int newEntry = new IntField(newEntryByte).getValue();
			for (int i = 0; i < entries.size(); i++) {
				byte[] oldEntryByte = entries.get(i).getField().toByteArray();
				int oldEntry = new IntField(oldEntryByte).getValue();
				if (newEntry < oldEntry) {
					entries.add(i, entry);
					add = true;
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
				}
			}
		}
		if (!add) {
			entries.add(entry);
		}
	}

}