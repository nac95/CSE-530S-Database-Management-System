package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw3.BPlusTree;
import hw3.Entry;
import hw3.InnerNode;
import hw3.LeafNode;
import hw3.Node;

public class YourHW3Tests {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	public void testComplexInsert() {

		//create a tree, insert a bunch of values
		BPlusTree bt = new BPlusTree(3, 4);
		bt.insert(new Entry(new IntField(9), 0));
		bt.insert(new Entry(new IntField(4), 0));
		bt.insert(new Entry(new IntField(12), 0));
		bt.insert(new Entry(new IntField(7), 0));
		bt.insert(new Entry(new IntField(2), 0));
		bt.insert(new Entry(new IntField(6), 0));
		bt.insert(new Entry(new IntField(1), 0));
		bt.insert(new Entry(new IntField(3), 0));
		bt.insert(new Entry(new IntField(10), 0));
		bt.insert(new Entry(new IntField(11), 0));

		//verify root properties
		Node root = bt.getRoot();

		assertTrue(root.isLeafNode() == false);

		InnerNode in = (InnerNode)root;

		ArrayList<Field> k = in.getKeys();
		ArrayList<Node> c = in.getChildren();

		assertTrue(k.get(0).compare(RelationalOperator.EQ, new IntField(4)));

		//grab left and right children from root
		InnerNode l = (InnerNode)c.get(0);
		InnerNode r = (InnerNode)c.get(1);

		assertTrue(l.isLeafNode() == false);
		assertTrue(r.isLeafNode() == false);

		//check values in left node
		ArrayList<Field> kl = l.getKeys();
		ArrayList<Node> cl = l.getChildren();

		assertTrue(kl.get(0).compare(RelationalOperator.EQ, new IntField(2)));

		//get left node's children, verify
		Node l0 = cl.get(0);
		Node l1 = cl.get(1);

		assertTrue(l0.isLeafNode());
		assertTrue(l1.isLeafNode());

		LeafNode lll = (LeafNode)l0;
		LeafNode lml = (LeafNode)l1;

		ArrayList<Entry> ell = lll.getEntries();

		assertTrue(ell.get(0).getField().equals(new IntField(1)));
		assertTrue(ell.get(1).getField().equals(new IntField(2)));

		ArrayList<Entry> elm = lml.getEntries();

		assertTrue(elm.get(0).getField().equals(new IntField(3)));
		assertTrue(elm.get(1).getField().equals(new IntField(4)));

		//verify right node
		ArrayList<Field> kr = r.getKeys();
		ArrayList<Node> cr = r.getChildren();

		assertTrue(kr.get(0).compare(RelationalOperator.EQ, new IntField(7)));
		assertTrue(kr.get(0).compare(RelationalOperator.EQ, new IntField(10)));

		//get right node's children, verify
		Node r0 = cr.get(0);
		Node r1 = cr.get(1);
		Node r2 = cr.get(2);

		assertTrue(r0.isLeafNode());
		assertTrue(r1.isLeafNode());
		assertTrue(r2.isLeafNode());

		LeafNode rll = (LeafNode)r0;
		LeafNode rrm = (LeafNode)r1;
		LeafNode rrl = (LeafNode)r1;

		ArrayList<Entry> erl = rll.getEntries();

		assertTrue(erl.get(0).getField().equals(new IntField(6)));
		assertTrue(erl.get(0).getField().equals(new IntField(7)));

		ArrayList<Entry> erm = rrm.getEntries();
		
		assertTrue(erm.get(0).getField().equals(new IntField(9)));
		assertTrue(erm.get(1).getField().equals(new IntField(10)));
		
		ArrayList<Entry> err = rrl.getEntries();

		assertTrue(err.get(0).getField().equals(new IntField(11)));
		assertTrue(err.get(1).getField().equals(new IntField(12)));
	}

}
