package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw1.StringField;
import hw1.Type;

public class InnerNode implements Node {
	
	private int degree;
	private ArrayList<Field> keys = new ArrayList<>();
	private ArrayList<Node> children = new ArrayList<>();
	private Node parent;
	private boolean isRoot;
	
	public InnerNode(int degree) {
		//your code here
		this.degree = degree;
		this.isRoot = false;
	}
	
	public void setRoot(boolean b) {
		this.isRoot = b;
	}
	
	public boolean isRoot(){
		return this.isRoot;
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

	public void setChildren(Node n) {
		this.children.add(n);
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
	
	public InnerNode getParent() {
		return (InnerNode) this.parent;
	}
	
	public boolean isExceedOne() {
		// cannot have more sub tree
		if (this.degree - 1 < this.keys.size()) {
			return true;
		}
		return false;
	}
	
	public void addKeys(Field key) {
		if (keys.size() == 0) {
			keys.add(key);
			return;
		}
		boolean add = false;
		if (key.getType() == Type.INT) {
			//byte[] newKeyByte = key.toByteArray();
			//int newKey = new IntField(newKeyByte).getValue();
			for (int i = 0; i < keys.size(); i++) {
				//byte[] oldKeyByte = keys.get(i).toByteArray();
				//int oldKey = new IntField(oldKeyByte).getValue();
				if (key.compare(RelationalOperator.LT, keys.get(i))) {
					keys.add(i, key);
					add = true;
					break;
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
					break;
				}
			}
		}
		if (!add) {
			keys.add(key);
		}
	}
	
	public void removeChild(Node n) {
		try{
			this.children.remove(n);
		}catch(Exception e) {
			
		}
	}
	
	public void removeKey(Field f) {
		try{
			this.keys.remove(f);
		}catch(Exception e) {
			
		}
	}
	
	public Field getMaxKey() {
		Field max = null;
		System.out.println("default max"+max);
		for(Field f: this.keys) {
			
			if(max == null || f.compare(RelationalOperator.GTE, max)) {
				max = f;
			}
		}
		System.out.println("max"+max);
		return max;
	}

}