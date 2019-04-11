package hw4;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import hw1.HeapPage;
import hw1.Tuple;
import hw1.Catalog.Table;
import hw1.Database;
import hw1.HeapFile;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool which check that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;
    
    private int maxPage;
    //for page cache
    //private ConcurrentHashMap<HeapPage, TableAndPage> cache;
    private Map<HeapPage, TableAndPage> cache;
    //record if one page is dirty(modified)
    private Map<TableAndPage, Boolean> dirtyRecord;
    //what locks currently exist
    private LinkedList<Lock> lockQueue;
    //help to find each transaction contains which page in which table(key is tid)
    private Map<Integer, List<TableAndPage>> tran;
    
    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // your code here
    	maxPage = numPages;
    	cache = new HashMap<>();
    	dirtyRecord = new HashMap<>();
    	lockQueue = new LinkedList<Lock>();
    	tran = new HashMap<>();
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param tableId the ID of the table with the requested page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public HeapPage getPage(int tid, int tableId, int pid, Permissions perm)
        throws Exception {
        // your code here
    	TableAndPage single = new TableAndPage(tableId, pid);
    	
    	HeapPage hp = Database.getCatalog().getDbFile(tableId).readPage(pid);
    	if (cache.size() >= maxPage) {
    		System.out.println("!!!!!!!!!!detect exceed amx page");
    		evictPage();
    		/*try {
    			evictPage();	
    		}catch(Exception e) {
    			//wait till the page is successfully evict
    		}*/
    	} else {
    		// if not contained
            if (lockQueue.size() == 0) {
            	/*if (perm == Permissions.READ_WRITE) {
            		hp.setDirty();
            	}*/
            	cache.put(hp, single);
            	List<TableAndPage> list = new ArrayList<>();
            	list.add(single);
    	        tran.put(tid, list);
    	        Lock lock = new Lock(tid, tableId, pid, perm);
            	lockQueue.add(lock);
            } else {
            	boolean update = false;
            	//check whether i can add lock or not
                for (Lock lock : lockQueue) {
            		if (lock.pid == single.pid && lock.tableId == single.tableId) {
            			//if the page has already has a write lock, block
                    	//if the page has already has a read lock, and new perm is also read, no problem;
                    	
            			if (lock.perm == Permissions.READ_ONLY) {
                    		if (perm == Permissions.READ_ONLY) {
                    			//allow
                    			cache.put(hp, single);
                    			List<TableAndPage> list = tran.get(tid);
                    			if (list == null) {
                            		list = new ArrayList<>();
                            		list.add(single);
                            		Lock l = new Lock(tid, tableId, pid, perm);
                            		lockQueue.add(l);
                            	} else if (!list.contains(single)) {
                            		list.add(single);
                            	}
                    	        tran.put(tid, list);
                    	        update = true;
                    	        break;
                    		} else {
                    			//new permission is read_write
                    			if (lock.tid != tid) {
                    				//abort the new one
                    				transactionComplete(tid, false);
                    				update = true;
                    			}
                    		}
                    	} else {
                    		//if original permission is read_write, abort everything
                    		if (lock.tid != tid) {
                    			transactionComplete(tid, false);
                    			update = true;
                    		}
                    		
                    	}
            		} 
            		
            	}
                if (!update) {
                	/*if (perm == Permissions.READ_WRITE) {
                		hp.setDirty();
                	}*/
                	cache.put(hp, single);
                    List<TableAndPage> list = tran.get(tid);
                    if (list == null) {
                    	list = new ArrayList<>();
                    }
                    list.add(single);
                    tran.put(tid, list);
                    Lock l = new Lock(tid, tableId, pid, perm);
                    lockQueue.add(l);
                }
                
            }
    	}
    	return hp;

    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param tableID the ID of the table containing the page to unlock
     * @param pid the ID of the page to unlock
     */
    public void releasePage(int tid, int tableId, int pid) {
        // your code here
    	for(Lock l: lockQueue) {
    		if(l.tid == tid && l.tableId == tableId && l.pid == pid) {
    			lockQueue.remove(l);
    			// set this page clean
    		}
    	}
    	
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(int tid, int tableId, int pid) {
        // your code here
    	for(Lock c: lockQueue) {
    		if(c.pid==pid && c.tableId == tableId && c.tid == tid) {
    			return true;
    		}
    	}
        return false;
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction. If the transaction wishes to commit, write
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(int tid, boolean commit)
        throws IOException {
        // your code here
    	//release all locks associated to the transaction no matter commit or abort
		List<TableAndPage> tableAndPages = tran.get(tid);
		for (TableAndPage each : tableAndPages) {
			int tableId = each.tableId;
			int pid = each.pid;
			releasePage(tid, tableId, pid);
			if (commit) {
				flushPage(tableId, pid);
			}
		}
    }

    /**
     * Add a tuple to the specified table behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to. May block if the lock cannot 
     * be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public void insertTuple(int tid, int tableId, Tuple t)
        throws Exception {
    	 // your code here
    	HeapFile hf = Database.getCatalog().getDbFile(tableId);
    	HeapPage hp = hf.addTuple(t);
    	int pid = hp.getId();
    	TableAndPage single = new TableAndPage(tableId, pid);
    	boolean update = false;
    	Map<HeapPage, TableAndPage> copyCache = copyCache(cache);
    	
    	if (copyCache.containsKey(hp)) {
        		//TODO
    		for (Lock lock : lockQueue) {
    			if (lock.tableId == tableId && lock.tid == tid && lock.pid == pid) {
    				if (lock.perm != Permissions.READ_WRITE) {
    					lockQueue.remove(lock);
    					Lock newLock = new Lock(tid, tableId, pid, Permissions.READ_WRITE);
    					lockQueue.add(newLock);
    					cache.remove(hp);
    					hp.setDirty();
    					cache.put(hp, single);
    					break;
    				} else {
    					cache.remove(hp);
    					hp.setDirty();
    					cache.put(hp, single);
    					break;
    				}
    			}
    		}
    		update = true;
    	} 
    	if (!update) {
    		hp.setDirty();
    		cache.put(hp, single);
    		Lock writeLock = new Lock(tid, tableId, pid, Permissions.READ_WRITE);
    		List<TableAndPage> list = tran.get(tid);
			if (list == null) {
        		list = new ArrayList<>();
        	}
        	list.add(single);
        	lockQueue.add(writeLock);
	        tran.put(tid, list);
	        dirtyRecord.put(single, true);
	        hp.setDirty();
    	}
    	
    }
    
    private Map<HeapPage, TableAndPage> copyCache(Map<HeapPage, TableAndPage> cache) {
    	Map<HeapPage, TableAndPage> copyCache = new HashMap<>();
    	for (HeapPage hp : cache.keySet()) {
    		copyCache.put(hp, cache.get(hp));
    	}
    	return copyCache;
    	
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from. May block if
     * the lock cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty.
     *
     * @param tid the transaction adding the tuple.
     * @param tableId the ID of the table that contains the tuple to be deleted
     * @param t the tuple to add
     */
    public void deleteTuple(int tid, int tableId, Tuple t)
        throws Exception {
        // your code here
    	HeapFile hf = Database.getCatalog().getDbFile(tableId);
    	HeapPage hp = hf.deleteTupleReturn(t);
    	int pid = hp.getId();
    	TableAndPage single = new TableAndPage(tableId, pid);
    	
    	boolean update = false;
    	Map<HeapPage, TableAndPage> copyCache = copyCache(cache);
    	if (copyCache.containsKey(hp)) {
    		//TODO
    		for (Lock lock : lockQueue) {
    			if (lock.tableId == tableId && lock.tid == tid && lock.pid == pid) {
    				if (lock.perm != Permissions.READ_WRITE) {
    					lockQueue.remove(lock);
    					Lock newLock = new Lock(tid, tableId, pid, Permissions.READ_WRITE);
    					lockQueue.add(newLock);
    					cache.remove(hp);
    					hp.setDirty();
    					cache.put(hp, single);
    					break;
    				} else {
    					cache.remove(hp);
    					hp.setDirty();
    					cache.put(hp, single);
    					break;
    				}
    			}
    		}
    		update = true;
    	} 
    	if (!update) {
    		hp.setDirty();
    		cache.put(hp, single);
    		Lock writeLock = new Lock(tid, tableId, pid, Permissions.READ_WRITE);
    		List<TableAndPage> list = tran.get(tid);
			if (list == null) {
        		list = new ArrayList<>();
        	}
        	list.add(single);
        	lockQueue.add(writeLock);
	        tran.put(tid, list);
	        dirtyRecord.put(single, true);
	        hp.setDirty();
    	}
    }

    private synchronized void flushPage(int tableId, int pid) throws IOException {
        // your code here
    	HeapFile hf = Database.getCatalog().getDbFile(tableId);
    	for (HeapPage hp : cache.keySet()) {
    		TableAndPage single = cache.get(hp);
    		if (single.tableId == tableId && single.pid == pid) {
    			hf.writePage(hp);
    		}
    	}
    }
    

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws Exception {
        // your code here
    	int cleanCount = 0;
    	for (HeapPage hp : cache.keySet()) {
    		if(!hp.isDirty) {
    			System.out.println("!!!!!detect clean page" + hp.getId());
    			cleanCount++;
    			cache.remove(hp);
    			break;
    		}
    	}
    	
    	if(cleanCount == 0) {
    		System.out.println("!!!!!!no clean page");
    		throw new Exception("No clean Page!");
    	}
    }
    
    
    //define lock here
    private class Lock{
    	public int tid;
    	public int tableId;
    	public int pid;
    	private Permissions perm;
    	private HeapPage hp;
    	
    	public Lock(int tid, int tableId, int pid, Permissions perm) {
    		this.tid = tid;
    		this.tableId = tableId;
    		this.pid = pid;
    		this.perm = perm;
    		this.hp = Database.getCatalog().getDbFile(tableId).readPage(pid);
    	}
    	
    	public void setPermissions(Permissions perm) {
    		this.perm = perm;
    	}
    	
    	public Permissions getPermissions() {
    		return this.perm;
    	}
    }

}
class TableAndPage {
	int pid;
	int tableId;
	
	public TableAndPage(int tableId, int pid) {
		this.tableId = tableId;
		this.pid = pid;
	}
}

