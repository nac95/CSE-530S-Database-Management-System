package hw3;


import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
	
	private Node root;
	//pInner: maximum number of keys in an inner node.
	private int pInner;
	//pLeaf: maximum number of data pointers in a leaf node.
	private int pLeaf;
    
    public BPlusTree(int pInner, int pLeaf) {
    	this.pInner = pInner;
    	this.pLeaf = pLeaf;
    	
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
    		if(f.compare(RelationalOperator.LTE, keys.get(0))) {
    			return searchLeaf(f,children.get(0));
    		}else if(f.compare(RelationalOperator.GT, keys.get(keys.size()))){
    			// check the index of the key
    			return searchLeaf(f,children.get(children.size()));
    		}else 
    		{
    			for(int i = 0; i < keys.size();++i) {
    				if(f.compare(RelationalOperator.GT,keys.get(i))&f.compare(RelationalOperator.LTE, keys.get(i+1))) {
    					return searchLeaf(f,children.get(i+1));
    				}
    			}
    			return null;
    		}
    	}
    }
    
    
   /**
    * Search for the node where the new record should go
		If the target node is not full, add the record
		else:
		Make a new node that contains half the values of the
		old one
		Insert the largest key of the new node into the parent
		If the parent is full:
		Split the parent and add the middle key to its
		parent
		Repeat until a split is not needed
		If the root needs to split:
		Create a new root with one key and two pointers
		
		  //Make new node
		   * LeafNode n
		   * n.add(half of old )
		   * for (i< half ){
		   *   n1.addKeys(e)
		   *   n1.setParent
		   * }
		   * for(half<i){
		   *   n2.addKeys(e)
		   *   n2.setParent
		   * }
		   * if parent.is not full{
		   *  pa.add(n1)
		   *  pa.add(n2)
		   * }
		   * if parent node is full{
		   *  // split the parent
		   *  node par -- par1 par2
		   *  par.entri.size/2 -- par 1
		   *  other par 2
		   *  
		   *  
		   * }
		   * 
		   * 
		}
    * @param e
    */
    public void insert(Entry e) {
    	//your code here
    	// if a new try, add something to it and make it a leafNode. 
    	if(this.root == null) {
    		LeafNode n = new LeafNode(pLeaf);
    		n.addKeys(e);
    		this.root = n;
    		System.out.println("root is leaf"+this.root.isLeafNode());
    	}
    	// if not empty, identify the target node, and make heavy operations
    	else {
    		//target identified
    		LeafNode target = this.search(e.getField());
    		// if target is not full, add it directly
    		if(!target.isFull()) {
    	    	target.addKeys(e);
    	    }
    		// if target is full, split the node 
    		
    		//add it to the parent node 
    		// if don't have one, then create one? use the previous node?		
    		else {
    	    	LeafNode insertNode = new LeafNode(pLeaf);		
    	    	//if an insertion is requested for a value that is already in the tree, the tree should not change.
			    for(int i=0; i<target.getEntries().size(); i++) {
					if(e.getField().compare(RelationalOperator.EQ, target.getEntries().get(i).getField())){
						return;
					}
				}// end of for loop	
			    
			    // if insert a value that is not in the tree
			    ArrayList<Entry> targetEntries = target.getEntries();
			    int half = (targetEntries.size()+1)/2;
			    for(int i = 0; i < targetEntries.size(); i++) {
			    	e.getField().compare(RelationalOperator.GT, targetEntries.get(i).getField());
			    	e.getField().compare(RelationalOperator.LT, targetEntries.get(i).getField());
			
			    }
    	    }
    			

    	}	
    }
    
    public void splitLeafNode() {
    	
    }
    
    public void splitParentNode() {
    	
    }
    
    public void splitRootNode() {
    	
    }
    
    public void delete(Entry e) {
    	//your code here
    }
    
    public Node getRoot() {
    	//your code here
    	return root;
    }
    


	
}
