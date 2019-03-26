package hw3;


import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
	
	private Node root;
	private int pInner;
	private int pLeaf;
	
    
    public BPlusTree(int pInner, int pLeaf) {
    	this.pInner = pInner;
    	this.pLeaf = pLeaf;
    	//pLeaf: maximum number of data pointers in a leaf node.
    }
    
    public LeafNode search(Field f) {
    	//your code here
    	
    	if(f==null|root==null) {	
    		return null;
    	}else {
    		return searchLeaf(f,root);
    	}
    }
    
    public LeafNode searchLeaf(Field f, Node n) {
    	System.out.println("f"+f.toString());
    	System.out.println("Node"+n.toString());
    	
    	if(n.isLeafNode()) {
    		System.out.println("Node is leaf"+n.toString());
    		return (LeafNode) n;
    	}
    	else{
    		InnerNode inner =  (InnerNode) n;
    		ArrayList<Field> keys = inner.getKeys();
    		ArrayList<Node> children = inner.getChildren();
    		if(f.compare(RelationalOperator.LT, keys.get(0))) {
    			return searchLeaf(f,children.get(0));
    		}else if(f.compare(RelationalOperator.GT, keys.get(keys.size()))){
    			// check the index of the key
    			return searchLeaf(f,children.get(children.size()));
    		}else 
    		{
    			for(int i = 0; i < keys.size();++i) {
    				if(f.compare(RelationalOperator.GT,keys.get(i))&f.compare(RelationalOperator.LT, keys.get(i+1))) {
    					return searchLeaf(f,children.get(i+1));
    				}
    			}
    			return null;
    		}
    	}
    }
    
    public void insert(Entry e) {
    	//your code here
    	
    }
    
    public void delete(Entry e) {
    	//your code here
    }
    
    public Node getRoot() {
    	//your code here
    	return root;
    }
    


	
}
