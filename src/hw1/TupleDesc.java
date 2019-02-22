package hw1;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc {

	private Type[] types;
	private String[] fields;
	
    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
    	if(typeAr.length != fieldAr.length ){
    		System.out.println("Data type length does not match data field length");
    		return;
    	}
    	this.types = typeAr;
    	this.fields = fieldAr;
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
           	return this.fields.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
    		try{
    			if(i < this.fields.length) {
    				return this.fields[i];
    			}
    			else {
    				throw new NoSuchElementException();
    			}
    		}
    		catch(NoSuchElementException e) {
    			throw e;
    		}
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int nameToId(String name) throws NoSuchElementException {
    		for(int i = 0; i < this.fields.length; ++i) {
    			if(this.fields[i].equals(name)) {
        			return i;
        		}
        	}
    		throw new NoSuchElementException();
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getType(int i) throws NoSuchElementException {
        //your code here
    	try{
    		return this.types[i];
    	}catch(NoSuchElementException e) {
			throw e;
		}
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     * Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
    	//your code here
    	int result = 0;
    	for(int i = 0; i < this.types.length; i++) {
    		if(this.types[i]==Type.INT) {
    			result = result + 4;
    		}
    		else if(this.types[i]==Type.STRING) {
    			result = result + 128 + 1;
    		}
    	}
    	return result;
    }

    /**
    -     * Compares the specified object with this TupleDesc for equality.
    -     * Two TupleDescs are considered equal if they are the same size and if the
    -     * n-th type in this TupleDesc is equal to the n-th type in td.
    -     *
    -     * @param o the Object to be compared for equality with this TupleDesc.
    -     * @return true if the object is equal to this TupleDesc.
    -     */
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TupleDesc other = (TupleDesc) obj;
		if (!Arrays.equals(fields, other.fields))
			return false;
		if (!Arrays.equals(types, other.types))
			return false;
		return true;
	}
    

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fields);
		result = prime * result + Arrays.hashCode(types);
		return result;
	}

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * @return String describing this descriptor.
     */
    public String toString() {
        //your code here
    	String result = ""; 
    	for(int i = 0; i < this.types.length;++i) {
    		result = result+""+this.types[i]+" ("+this.fields[i]+") ";
    	}
    	return result;
    }
}
