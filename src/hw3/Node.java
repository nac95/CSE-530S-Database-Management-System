package hw3;

import java.util.ArrayList;

public interface Node {
	
	
	public int getDegree();
	public boolean isLeafNode();
	public boolean isFull();
	public void setParent(Node parent);
	public ArrayList<Node> getChildren();
}
