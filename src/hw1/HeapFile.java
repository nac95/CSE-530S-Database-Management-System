package hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A heap file stores a collection of tuples. It is also responsible for managing pages.
 * It needs to be able to manage page creation as well as correctly manipulating pages
 * when tuples are added or deleted.
 * @author Sam Madden modified by Doug Shook
 *
 */
public class HeapFile {
	
	public static final int PAGE_SIZE = 4096;
	private File f;
	private TupleDesc type;
	/**
	 * Creates a new heap file in the given location that can accept tuples of the given type
	 * @param f location of the heap file
	 * @param types type of tuples contained in the file
	 */
	public HeapFile(File f, TupleDesc type) {
		//your code here
		this.f = f;
		this.type = type;
	}
	
	public File getFile() {
		//your code here
		return f;
	}
	
	public TupleDesc getTupleDesc() {
		//your code here
		return type;
	}
	
	/**
	 * Creates a HeapPage object representing the page at the given page number.
	 * Because it will be necessary to arbitrarily move around the file, a RandomAccessFile object
	 * should be used here.
	 * @param id the page number to be retrieved
	 * @return a HeapPage at the given page number
	 */
	public HeapPage readPage(int id) {
		//your code here
		try {
			RandomAccessFile file = new RandomAccessFile(getFile(), "r");
			file.seek(id * PAGE_SIZE);
			byte[] data = new byte[PAGE_SIZE];
			file.read(data);
			file.close();
			HeapPage page = new HeapPage(id, data, getId());
			return page;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns a unique id number for this heap file. Consider using
	 * the hash of the File itself.
	 * @return
	 */
	public int getId() {
		//your code here
		return f.hashCode();
	}
	
	/**
	 * Writes the given HeapPage to disk. Because of the need to seek through the file,
	 * a RandomAccessFile object should be used in this method.
	 * @param p the page to write to disk
	 */
	public void writePage(HeapPage p) {
		//your code here
		try {
			RandomAccessFile file;
			file = new RandomAccessFile(getFile(), "rw");
			file.seek(p.getId() * PAGE_SIZE);
			file.write(p.getPageData());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a tuple. This method must first find a page with an open slot, creating a new page
	 * if all others are full. It then passes the tuple to this page to be stored. It then writes
	 * the page to disk (see writePage)
	 * @param t The tuple to be stored
	 * @return The HeapPage that contains the tuple
	 */
	public HeapPage addTuple(Tuple t) {
		//your code here
		int id = 0;
		try {
			while (id <= getNumPages()) {
				HeapPage p = readPage(id);
				for (int s = 0; s < p.getNumSlots(); s++) {
					if (!p.slotOccupied(s)) {
						p.addTuple(t);
						writePage(p);
						return p;
					}
				}
			}
			HeapPage page;
			page = new HeapPage(getNumPages() + 1, new byte[PAGE_SIZE], getId());
			page.addTuple(t);
			writePage(page);
			return page;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method will examine the tuple to find out where it is stored, then delete it
	 * from the proper HeapPage. It then writes the modified page to disk.
	 * @param t the Tuple to be deleted
	 */
	public void deleteTuple(Tuple t){
		//your code here
		try {
			int pId = t.getPid();
			HeapPage p = readPage(pId);
			p.deleteTuple(t);
			writePage(p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns an ArrayList containing all of the tuples in this HeapFile. It must
	 * access each HeapPage to do this (see iterator() in HeapPage)
	 * @return
	 */
	public ArrayList<Tuple> getAllTuples() {
		//your code here
		ArrayList<Tuple> allTuple = new ArrayList<Tuple>();
		for (int i = 0; i < getNumPages(); i++) {
			Iterator<Tuple> iter = readPage(i).iterator();
			while (iter.hasNext()) {
				allTuple.add(iter.next());
			}
		}
		return allTuple;
	}
	
	/**
	 * Computes and returns the total number of pages contained in this HeapFile
	 * @return the number of pages
	 */
	public int getNumPages() {
		//your code here
		int numPages = 0;
		numPages = (int)(getFile().length() / HeapFile.PAGE_SIZE);
		return numPages;
	}
}
