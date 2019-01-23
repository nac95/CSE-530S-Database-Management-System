package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

public class YourUnitTests {
	
	private HeapFile hf;
	private TupleDesc td;
	private Catalog c;
	private HeapPage hp;

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
		hp = hf.readPage(0);
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void testIDAccess() {
		Type[] t = new Type[] {Type.INT, Type.STRING};
		String[] c = new String[] {"a", "bs"};
		TupleDesc td = new TupleDesc(t, c);
		TupleDesc td1 = new TupleDesc(t, c);
		Tuple tup = new Tuple(td);
		Tuple tup1 = new Tuple(td1);
		tup.setId(0);
		tup1.setId(1);
		assertTrue(tup.getId()== 0);
		assertTrue(tup1.getId() == 1);
	}
	@Test
	public void testPidAccess() {
		Type[] t = new Type[] {Type.INT, Type.STRING};
		String[] c = new String[] {"a", "bs"};
		TupleDesc td = new TupleDesc(t, c);
		TupleDesc td1 = new TupleDesc(t, c);
		Tuple tup = new Tuple(td);
		Tuple tup1 = new Tuple(td1);
		tup.setPid(0);
		tup1.setPid(1);
		assertTrue(tup.getPid()== 0);
		assertTrue(tup1.getPid() == 1);
	}
}
