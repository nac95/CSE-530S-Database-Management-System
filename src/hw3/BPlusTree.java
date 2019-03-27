package hw3;


import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
	
	private Node root;
	//pInner: maximum number of children in an inner node.
	private int pInner;
	//pLeaf: maximum number of data pointers in a leaf node.
	private int pLeaf;
    
    public BPlusTree(int pInner, int pLeaf) {
    	this.pInner = pInner;
    	this.pLeaf = pLeaf;
    	
    }
    
    public LeafNode search(Field f) {
    	//your code here
    	if(f==null||root==null) {	
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
    		//correct the search
    		return (LeafNode) n;
    	}
    	else{
    		InnerNode inner =  (InnerNode) n;
    		ArrayList<Field> keys = inner.getKeys();
    		ArrayList<Node> children = inner.getChildren();
    		if(f.compare(RelationalOperator.LTE, keys.get(0))) {
    			return searchLeaf(f,children.get(0));
    		}else if(f.compare(RelationalOperator.GT, keys.get(keys.size()-1))){
    			// check the index of the key
    			return searchLeaf(f,children.get(children.size()-1));
    		}else 
    		{
    			for(int i = 0; i < keys.size()-1;++i) {
    				if(f.compare(RelationalOperator.GT,keys.get(i))&&f.compare(RelationalOperator.LTE, keys.get(i+1))) {
    					return searchLeaf(f,children.get(i+1));
    				}
    			}
    			return null;
    		}
    	}
    }
    
    
    public LeafNode searchTargetLeaf(Field f, Node n) {
    	System.out.println("f"+f.toString());
    	System.out.println("Node"+n.toString());
    	if(f==null||n==null) {	
    		return null;
    	}
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
    		}else if(f.compare(RelationalOperator.GT, keys.get(keys.size()-1))){
    			// check the index of the key
    			return searchLeaf(f,children.get(children.size()-1));
    		}else 
    		{
    			for(int i = 0; i < keys.size()-1;++i) {
    				if(f.compare(RelationalOperator.GT,keys.get(i))&&f.compare(RelationalOperator.LTE, keys.get(i+1))) {
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
    		LeafNode target = this.searchTargetLeaf(e.getField(),this.root);
    		//add entry whatever the node
    		target.addKeys(e);
    		
    		//check if exceed one, if not exceed, do nothing
    		if(target.isExceedOne()) {
    			// split the leaf node
    			ArrayList<LeafNode> splitNodes = splitLeafNode(target);
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
	    			// try to add the keys of the new node
		    		//Nana start here
		    		
		    		this.root = parent;
    			}// bracket for target with no parent
    			// if the target node has a parent
    			else {
    				InnerNode parent = (InnerNode) target.getParent();
    				for(LeafNode n : splitNodes) {
    					parent.setChildren(n);
    					if(parent.isExceedOne()) {
		    				//split the parent
		    				splitParentNode(parent);
		    				// add child nodes to the parents
		    				// ????
		    				n.setParent(parent);
		    				// code needed here!!!!!!!!!
		    			}else {
		    				parent.setChildren(n);
		    				Field max = n.getMaxKey();
		    				parent.addKeys(max);
		    			}
    				}// for loop end
    			}// bracket for target has a parent		
    		}// if exceed one bracket
    	}	
    }
    
    public ArrayList<LeafNode> splitLeafNode(LeafNode n) {
    	ArrayList<Entry> entries = n.getEntries();
    	ArrayList<LeafNode> lns = new ArrayList<>();
    	// create nodes
    	LeafNode ln1 = new LeafNode(n.getDegree());
    	LeafNode ln2 = new LeafNode(n.getDegree());
    	//set Parent
    	ln1.setParent(n.getParent());
    	ln2.setParent(n.getParent());
    	//set entries
    	if(entries.size()%2 == 0) {
    		for(int i = 0; i < entries.size()/2;i++) {
    			ln1.addKeys(entries.get(i));
    		}
    	}
    	return null;
    }
    
    public void splitParentNode(Node n) {
    	//Nana start here
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
