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
    		else {
    			// split the leaf node
    			ArrayList<LeafNode> splitNodes = splitLeafNode(e, target);
    			// check the parent node before adding the splitted node to the parent
    			// if the target node does not have a parent
    			if(target.getParent()==null) {
    				System.out.println(target.getParent());
    				// transfer the target node to parent node 
	    			InnerNode parent = new InnerNode(target.getDegree());
		    			//copy the parent and children of target
		    			parent.setParent(target.getParent());
		    			// children might be unnecessary as the target is a leaf node
		    			for(Node n: target.getChildren()) {
		    				parent.setChildren(n);
		    			}
	    			// try to add the node to this newly created parent
		    		for(LeafNode n : splitNodes) {
		    			if(parent.isFull()) {
		    				//split the parent
		    				splitParentNode(parent);
		    				// add nodes to the parents
		    				// code needed here!!!!!!!!!
		    			}else {
		    				parent.setChildren(n);
		    				// set keys here
		    				//code needed!!!!!!
		    			}
		    		}
    			}// bracket for target with no parent
    			// if the target node has a parent
    			else {
    				InnerNode parent = (InnerNode) target.getParent();
    				for(LeafNode n : splitNodes) {
    					if(parent.isFull()) {
		    				//split the parent
		    				splitParentNode(parent);
		    				// add nodes to the parents
		    				// code needed here!!!!!!!!!
		    			}else {
		    				parent.setChildren(n);
		    				// set keys here
		    				//code needed!!!!!!
		    			}
    				}// for loop end
    			}// bracket for target has a parent		
    	    }	
    	}	
    }
    
    public ArrayList<LeafNode> splitLeafNode(Entry e, LeafNode n) {
    	ArrayList<Entry> oldEntires = n.getEntries();
    	return null;
    }
    
    public void splitParentNode(Node n) {
    	
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
