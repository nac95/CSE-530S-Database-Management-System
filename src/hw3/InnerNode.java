package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.IntField;
import hw1.StringField;
import hw1.Type;

public class InnerNode implements Node {
	
	private int degree;
	private ArrayList<Field> keys = new ArrayList<>();
	private ArrayList<Node> children = new ArrayList<>();
	private Node parent;
	
	
	public InnerNode(int degree) {
		//your code here
		this.degree = degree;
	}
	
	public ArrayList<Field> getKeys() {
		//your code here
		return keys;
	}
	
	public ArrayList<Node> getChildren() {
		//your code here
		for(Node c: this.children) {
			c.setParent(this);
		}
		return children;
	}

	public int getDegree() {
		//your code here
		return degree;
	}
	
	public boolean isLeafNode() {
		return false;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public void addKeys(Field key) {
		if (keys.size() == 0) {
			keys.add(key);
		}
		boolean add = false;
		if (key.getType() == Type.INT) {
			byte[] newKeyByte = key.toByteArray();
			int newKey = new IntField(newKeyByte).getValue();
			for (int i = 0; i < keys.size(); i++) {
				byte[] oldKeyByte = keys.get(i).toByteArray();
				int oldKey = new IntField(oldKeyByte).getValue();
				if (newKey < oldKey) {
					keys.add(i, key);
					add = true;
				}
			}
		}
		if (key.getType() == Type.STRING) {
			byte[] newKeyByte = key.toByteArray();
			String newKey = new StringField(newKeyByte).getValue();
			for (int i = 0; i < keys.size(); i++) {
				byte[] oldKeyByte = keys.get(i).toByteArray();
				String oldKey = new StringField(oldKeyByte).getValue();
				if (newKey.compareTo(oldKey) < 0) {
					keys.add(i, key);
					add = true;
				}
			}
		}
		if (!add) {
			keys.add(key);
		}
	}

}