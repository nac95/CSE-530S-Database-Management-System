package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.IntField;
import hw1.StringField;
import hw1.Tuple;
import hw1.TupleDesc;
import hw4.BufferPool;
import hw4.Permissions;

public class YourHW4Tests {
	
	private Catalog c;
	private BufferPool bp;
	private HeapFile hf;
	private TupleDesc td;
	private int tid;
	
	@Before
	public void setup() {
		
		try {
			Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("unable to copy files");
			e.printStackTrace();
		}
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/test.txt");
		
		int tableId = c.getTableId("test");
		td = c.getTupleDesc(tableId);
		hf = c.getDbFile(tableId);
		
		bp = Database.getBufferPool();
		
		tid = c.getTableId("test");
	}

	@Test
	public void testUpdateReadToWrite() throws Exception {
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] {0, 0, 0, (byte)131}));
		byte[] s = new byte[129];
		s[0] = 6;
		s[1] = 88;
		s[2] = 121;
		t.setField(1, new StringField(s));
		
		//start with read access only
		bp.getPage(0, tid, 0, Permissions.READ_ONLY); //acquire lock for the page
		bp.insertTuple(0, tid, t); //insert the tuple into the page
		bp.transactionComplete(0, false); //should not flush the modified page
		HeapPage hp0 = bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		Iterator<Tuple> it0 = hp0.iterator();
		assertFalse(it0.hasNext());
		
		//update to read and write
		bp.getPage(0, tid, 0, Permissions.READ_WRITE); //acquire lock for the page
		bp.insertTuple(0, tid, t); //insert the tuple into the page
		bp.transactionComplete(0, true); //should flush the modified page
		
		//reset the buffer pool, get the page again, make sure data is there
		Database.resetBufferPool(BufferPool.DEFAULT_PAGES);
		HeapPage hp1 = bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		Iterator<Tuple> it1 = hp1.iterator();
		assertTrue(it1.hasNext());
		it1.next();
		assertTrue(it1.hasNext());
		it1.next();
		assertFalse(it1.hasNext());
	}
	
	@Test
	public void testDeleteTuple() throws Exception {
		//initialize the tuple that i want to insert
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] {0, 0, 0, (byte)131}));
		byte[] s = new byte[129];
		s[0] = 6;
		s[1] = 88;
		s[2] = 121;
		t.setField(1, new StringField(s));
		
		bp.getPage(0, tid, 0, Permissions.READ_WRITE); //acquire lock for the page
		bp.insertTuple(0, tid, t); //insert the tuple into the page
		bp.transactionComplete(0, true); //should flush the modified page
		
		//reset the buffer pool, get the page again, make sure data is there
		Database.resetBufferPool(BufferPool.DEFAULT_PAGES);
		HeapPage hp = bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		Iterator<Tuple> it = hp.iterator();
		assertTrue(it.hasNext());
		it.next();
		assertTrue(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
		//until here, successfully insert
		
		bp.getPage(0, tid, 0, Permissions.READ_WRITE); //acquire lock for the page
		bp.deleteTuple(0, tid, t); //delete the tuple into the page
		bp.transactionComplete(0, true); //should flush the modified page
		
		//reset the buffer pool, get the page again, make sure data has been deleted successfully
		Database.resetBufferPool(BufferPool.DEFAULT_PAGES);
		HeapPage hp2 = bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		Iterator<Tuple> it2 = hp2.iterator();
		assertTrue(it2.hasNext());
		it2.next();
		assertFalse(it2.hasNext());
		//until here, delete successfully
		
	}
	
	
	
	
	
}
