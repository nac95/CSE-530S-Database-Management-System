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
    		LeafNode leafN = (LeafNode) n;
    		for(Entry e:leafN.getEntries()) {
    			if(f.compare(RelationalOperator.EQ, e.getField())) {
    				return (LeafNode) n;
    			}
    		}
    		return null;
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
    		LeafNode leafN = (LeafNode) n;
    		for(Entry e:leafN.getEntries()) {
    			if(f.compare(RelationalOperator.EQ, e.getField())) {
    				return null;
    			}
    		}
    		return (LeafNode) n;
    	}
    	else{
    		InnerNode inner =  (InnerNode) n;
    		ArrayList<Field> keys = inner.getKeys();
    		ArrayList<Node> children = inner.getChildren();
    		if(f.compare(RelationalOperator.LTE, keys.get(0))) {
    			return searchTargetLeaf(f,children.get(0));
    		}else if(f.compare(RelationalOperator.GT, keys.get(keys.size()-1))){
    			// check the index of the key
    			return searchTargetLeaf(f,children.get(children.size()-1));
    		}else 
    		{
    			for(int i = 0; i < keys.size()-1;++i) {
    				if(f.compare(RelationalOperator.GT,keys.get(i))&&f.compare(RelationalOperator.LTE, keys.get(i+1))) {
    					return searchTargetLeaf(f,children.get(i+1));
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
    	if(root == null) {
    		LeafNode n = new LeafNode(pLeaf);
    		n.addKeys(e);
    		root = n;
    		//n.setRoot(true);
    		System.out.println("root is leaf"+root.isLeafNode());
    	}
    	// if not empty, identify the target node, and make heavy operations
    	else {
    		//target identified
    		LeafNode target = this.searchTargetLeaf(e.getField(),this.root);
    		if (target == null) {
    			return;
    		}
    		//add entry whatever the node
    		target.addKeys(e);
    		System.out.println("the size of target" + target.getEntries().size());
       		//check if exceed one, if not exceed, do nothing
    		if(target.isExceedOne()) {
    			// split the leaf node
    			System.out.println("split the node in 144");
    			splitLeafNode(target);
    		}
    			
    	}	
    }
    
    public void splitLeafNode(LeafNode n) {
    	ArrayList<Entry> entries = n.getEntries();
    	//ArrayList<LeafNode> lns = new ArrayList<>();
    	// create nodes
    	LeafNode ln1 = new LeafNode(n.getDegree());
    	LeafNode ln2 = new LeafNode(n.getDegree());
    	//set entries
    	System.out.println("entries 192" + entries.size()%2);
    	if(entries.size()%2 == 0) {
    		for(int i = 0; i < entries.size()/2;i++) {
    			ln1.addKeys(entries.get(i));
    		}
    		for(int i = entries.size()/2; i < entries.size();i++) {
    			ln2.addKeys(entries.get(i));
    		}
    	}
    	else {
    		System.out.println("entries 202" + entries.size()%2);
    		int half = entries.size()/2 + 1;
    		for(int i = 0; i < half;i++) {
    			ln1.addKeys(entries.get(i));
    		}
    		for(int i = half; i < entries.size();i++) {
    			ln2.addKeys(entries.get(i));
    		}
    	}
    	if (n.getParent() == null) {
    		InnerNode parent = new InnerNode(pInner);
    		if (entries.size() % 2 == 0) {
    			parent.addKeys(entries.get(entries.size() / 2 - 1).getField());
    		} else {
    			parent.addKeys(entries.get(entries.size() / 2).getField());
    		}
    		parent.setChildren(ln1);
    		parent.setChildren(ln2);
    		ln1.setParent(parent);
    		ln2.setParent(parent);
    		if (root == n) {
    			root = parent;
    		}
    	} else {
    		InnerNode parent = (InnerNode)n.getParent();
    		ArrayList<Node> children = parent.getChildren();
    		int change = children.indexOf(n);
    		children.remove(change);
    		if (entries.size() % 2 == 0) {
    			parent.addKeys(entries.get(entries.size() / 2 - 1).getField());
    		} else {
    			parent.addKeys(entries.get(entries.size() / 2).getField());
    		}
    		children.add(change, ln1);
    		children.add(change + 1, ln2);
    		ln1.setParent(parent);
    		ln2.setParent(parent);
    		if (parent.isExceedOne()) {
    			splitParentNode(parent);
    			//splitParentNode(ln1, ln2, parent);
    			
    		}
    	}
    	
    }
    
    public void splitParentNode(Node n) {
    	//Nana start here
    	//original innernode
    	InnerNode original = (InnerNode) n;
    	if (original.getParent() == null) {
    		//split the original into two parts
        	InnerNode split1 = new InnerNode(pInner);
        	InnerNode split2 = new InnerNode(pInner);
        	InnerNode parent = new InnerNode(pInner);
        	int size = original.getKeys().size();
        	ArrayList<Node> children = original.getChildren();
        	int secStart = 0;
        	// find each part has how many nodes after split
        	if (size % 2 == 0) {
        		for (int i = 0; i < size / 2; i++) {
        			split1.addKeys(original.getKeys().get(i));
      
        		}
        		for (int j = 0; j < children.size(); j++) {
    				Node child = children.get(j);
    				if (child.getMaxKey().compare(RelationalOperator.LTE, split1.getMaxKey())) {
    					split1.setChildren(child);
    					child.setParent(split1);
    					secStart++;
    				}
    			}
        		for (int i = size / 2; i < size; i++) {
        			split2.addKeys(original.getKeys().get(i));
        			
        		}
        		for (int j = secStart; j < children.size(); j++) {
    				Node child = children.get(j);
    			
    				split2.setChildren(child);
    				child.setParent(split2);
    				
    			}
        		split1.getKeys().remove(original.getKeys().get(size / 2 - 1));
        		parent.addKeys(original.getKeys().get(size / 2 - 1));
        	} else {
        		for (int i = 0; i < size / 2 + 1; i++) {
        			split1.addKeys(original.getKeys().get(i));
        			
        		}
        		for (int j = 0; j < children.size(); j++) {
    				Node child = children.get(j);
    				if (child.getMaxKey().compare(RelationalOperator.LTE, split1.getMaxKey())) {
    					split1.setChildren(child);
    					child.setParent(split1);
    					secStart++;
    				}
    			}
        		for (int i = size / 2 + 1; i < size; i++) {
        			split2.addKeys(original.getKeys().get(i));
        			
        		}
        		for (int j = secStart; j < children.size(); j++) {
    				Node child = children.get(j);
    			
    				split2.setChildren(child);
    				child.setParent(split2);
    				
    			}
        		split1.getKeys().remove(original.getKeys().get(size / 2));
        		parent.addKeys(original.getKeys().get(size / 2));
        	}
        	split1.setParent(parent);
        	split2.setParent(parent);
        	parent.setChildren(split1);
        	parent.setChildren(split2);
        
        	//check whether the current innernode is root or not
        	if (root == original) {
        		root = parent;
        	}
    		
    	} else {
    		//split the original into two parts
        	InnerNode split1 = new InnerNode(pInner);
        	InnerNode split2 = new InnerNode(pInner);
        	InnerNode parent = original.getParent();
        	int size = original.getKeys().size();
        	ArrayList<Node> children = original.getChildren();
       //	System.out.println("!!!!!!!!!!!!size in 326" + children.size());
        	int secStart = 0;
        	// find each part has how many nodes after split
        	if (size % 2 == 0) {
        		for (int i = 0; i < size / 2; i++) {
        			split1.addKeys(original.getKeys().get(i));
        			
        		}
        		for (int j = 0; j < children.size(); j++) {
    				Node child = children.get(j);
    				if (child.getMaxKey().compare(RelationalOperator.LTE, split1.getMaxKey())) {
    					split1.setChildren(child);
    					child.setParent(split1);
    					secStart++;
    				}
    			}
        		for (int i = size / 2; i < size; i++) {
        			split2.addKeys(original.getKeys().get(i));
        			
        		}
        		for (int j = secStart; j < children.size(); j++) {
    				Node child = children.get(j);
    			
    				split2.setChildren(child);
    				child.setParent(split2);
    				
    			}
        		split1.getKeys().remove(original.getKeys().get(size / 2 - 1));
        		parent.addKeys(original.getKeys().get(size / 2 - 1));
        		if (parent.isExceedOne()) {
        			splitParentNode(parent);
        		}
        	} else {
        		for (int i = 0; i < size / 2 + 1; i++) {
        			split1.addKeys(original.getKeys().get(i));
        			
        		}
        		for (int j = 0; j < children.size(); j++) {
    				Node child = children.get(j);
    				if (child.getMaxKey().compare(RelationalOperator.LTE, split1.getMaxKey())) {
    					split1.setChildren(child);
    					child.setParent(split1);
    					secStart++;
    				}
    			}
        		for (int i = size / 2 + 1; i < size; i++) {
        			split2.addKeys(original.getKeys().get(i));
        			
        		}
        		for (int j = secStart; j < children.size(); j++) {
    				Node child = children.get(j);
    				split2.setChildren(child);
    				child.setParent(split2);
    				
    			}
        		split1.getKeys().remove(original.getKeys().get(size / 2));
        		parent.addKeys(original.getKeys().get(size / 2));
        		if (parent.isExceedOne()) {
        			splitParentNode(parent);
        		}
        	}
        	split1.setParent(parent);
        	split2.setParent(parent);
        	parent.setChildren(split1);
        	parent.setChildren(split2);
    		
    	}
    	
    }
    
    public void delete(Entry e) {
    	//your code here
    	//if not in the tree, stay still
    	if(search(e.getField())==null) {
    		return;
    	}
    	LeafNode target = search(e.getField());
    	/*int size = target.getEntries().size();
    	if(size > target.getDegree()/2) {
    		target.removeEntry(e);
    	}*/
    	ArrayList<Entry> entries = target.getEntries();
    	ArrayList<Node> children = target.getParent().getChildren();
    	int index = children.indexOf(target);
    	for (int i = 0; i < entries.size(); i++) {
    		if (e.getField().compare(RelationalOperator.EQ, entries.get(i).getField())) {
    			entries.remove(i);
    		}
    	}
    	
    	if(entries.size() < target.getDegree() / 2) {
    		//check left node first
    		if (index == 0) {
    			if (look((LeafNode)children.get(index + 1))) {
    				//lend one from right
    				Entry lend = ((LeafNode)children.get(index + 1)).getEntries().get(0);
    				entries.add(lend);
    				((LeafNode)children.get(index + 1)).getEntries().remove(0);
    				if (lend.getField().compare(RelationalOperator.GT, ((InnerNode)target.getParent()).getKeys().get(0))) {
    					((InnerNode)target.getParent()).getKeys().set(0, lend.getField());
    				}
    			} else {
    				//ortherwise merge
    				
    				
    			}
    			
    		}
    		if (index == children.size() - 1) {
    			if (look((LeafNode)children.get(index - 1))) {
    				//lend one from left
    				int size = ((LeafNode)children.get(index - 1)).getEntries().size();
    				Entry lend = ((LeafNode)children.get(index - 1)).getEntries().get(size - 1);
    				entries.add(lend);
    				((LeafNode)children.get(index - 1)).getEntries().remove(size - 1);
    				int sizePar = ((InnerNode)target.getParent()).getKeys().size();
    				if (lend.getField().compare(RelationalOperator.LT, ((InnerNode)target.getParent()).getKeys().get(sizePar - 1))) {
    					((InnerNode)target.getParent()).getKeys().set(sizePar - 1, lend.getField());
    				}
    				if (lend.getField().compare(RelationalOperator.EQ, ((InnerNode)target.getParent()).getKeys().get(sizePar - 1))) {
    					((InnerNode)target.getParent()).getKeys().set(sizePar - 1, ((LeafNode)children.get(index - 1)).getMaxKey());
    				}
    				
    			} else {
    				//ortherwise merge
    				
    			}
    		}
    		if (index > 0 && index < children.size() - 1) {
    			if (look((LeafNode)children.get(index - 1))) {
        			//lend one from left
    				int size = ((LeafNode)children.get(index - 1)).getEntries().size();
    				Entry lend = ((LeafNode)children.get(index - 1)).getEntries().get(size - 1);
    				entries.add(lend);
    				((LeafNode)children.get(index - 1)).getEntries().remove(size - 1);
    				int sizePar = ((InnerNode)target.getParent()).getKeys().size();
    				if (lend.getField().compare(RelationalOperator.LT, ((InnerNode)target.getParent()).getKeys().get(index - 1))) {
    					((InnerNode)target.getParent()).getKeys().set(index - 1, lend.getField());
    				}
    				if (lend.getField().compare(RelationalOperator.EQ, ((InnerNode)target.getParent()).getKeys().get(index - 1))) {
    					((InnerNode)target.getParent()).getKeys().set(index - 1, ((LeafNode)children.get(index - 1)).getMaxKey());
    				}
        		} else if (look((LeafNode)children.get(index + 1))) {
        				//look right and lend from right
        			Entry lend = ((LeafNode)children.get(index + 1)).getEntries().get(0);
    				entries.add(lend);
    				((LeafNode)children.get(index + 1)).getEntries().remove(0);
    				int sizePar = ((InnerNode)target.getParent()).getKeys().size();
    				if (lend.getField().compare(RelationalOperator.GT, ((InnerNode)target.getParent()).getKeys().get(index))) {
    					((InnerNode)target.getParent()).getKeys().set(index, lend.getField());
    				}
    				
        		} else {
        			//merge to left
        			
        		}
    		}
    		
    	}
    }
    private boolean look(LeafNode n) {
    	
    	if ((n.getEntries().size() - 1) < n.getDegree() / 2) {
    		return false;
    	}
    	return true;
    }
    
    public Node getRoot() {
    	//your code here
    	return root;
    }
    


	
}
