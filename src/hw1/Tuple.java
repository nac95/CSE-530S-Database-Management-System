package hw1;

import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
	
/**
 * This class represents a tuple that will contain a single row's worth of information
 * from a table. It also includes information about where it is stored
 * @author Sam Madden modified by Doug Shook
 *
 */
public class Tuple {
	private int pid;
	private int id;
	private TupleDesc t;
	private List<Field> fields = new LinkedList<Field>();
	
	
	/**
	 * Creates a new tuple with the given description
	 * @param t the schema for this tuple
	 */
	public Tuple(TupleDesc t) {
		this.t = t;
	}
	
	public TupleDesc getDesc() {
		
		return this.t;
	}
	
	/**
	 * retrieves the page id where this tuple is stored
	 * @return the page id of this tuple
	 */
	public int getPid() {
		//your code here
		return this.pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * retrieves the tuple (slot) id of this tuple
	 * @return the slot where this tuple is stored
	 */
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setDesc(TupleDesc td) {
		this.t = td;
	}
	
	/**
	 * Stores the given data at the i-th field
	 * @param i the field number to store the data
	 * @param v the data
	 */
	public void setField(int i, Field v) {
		//your code here
		this.fields.add(i, v);
	}
	
	public Field getField(int i) {
		//your code here
		return this.fields.get(i);
	}
	
	/**
	 * Creates a string representation of this tuple that displays its contents.
	 * You should convert the binary data into a readable format (i.e. display the ints in base-10 and convert
	 * the String columns to readable text).
	 */
	public String toString() {
		//source:https://github.com/thierry1129/cse530/blob/master/hw1/Tuple.java
		StringBuffer sb = new StringBuffer("");
        for (Field a : this.fields) {
            if (a.getType().equals(Type.STRING)) {
                sb.append(a.toString());
		
            } else {
                // handle the printing of int type, could be wrong .
                sb.append(a.toString());
                sb.append(" ");
            }
        }
        return sb.toString();
	}
}
	