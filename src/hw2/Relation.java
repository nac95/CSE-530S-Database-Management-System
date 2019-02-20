package hw2;

import java.util.ArrayList;
import java.util.List;

import hw1.Field;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * This class provides methods to perform relational algebra operations. It will be used
 * to implement SQL queries.
 * @author Doug Shook
 *
 */
public class Relation {

	private ArrayList<Tuple> tuples;
	private TupleDesc td;
	
	public Relation(ArrayList<Tuple> l, TupleDesc td) {
		//your code here
		tuples = l;
		this.td = td;
	}
	
	/**
	 * This method performs a select operation on a relation
	 * @param field number (refer to TupleDesc) of the field to be compared, left side of comparison
	 * @param op the comparison operator
	 * @param operand a constant to be compared against the given column
	 * @return
	 */
	public Relation select(int field, RelationalOperator op, Field operand) {
		//your code here
//		Relation result = new Relation();
		String fieldName = td.getFieldName(field);
		ArrayList<Tuple> newTuple = new ArrayList<Tuple>();
		
		for (Tuple tuple : tuples) {
			if (tuple.getField(field).compare(op, operand)) {
				newTuple.add(tuple);
			}
		}
		Relation result = new Relation(newTuple, td);
		return result;
	}
	
	/**
	 * This method performs a rename operation on a relation
	 * @param fields the field numbers (refer to TupleDesc) of the fields to be renamed
	 * @param names a list of new names. The order of these names is the same as the order of field numbers in the field list
	 * @return
	 */
//	????NPE
	public Relation rename(ArrayList<Integer> fields, ArrayList<String> names) {
		//your code here
		String[] fieldAr = new String[td.numFields()];
		Type[] typeAr = new Type[td.numFields()];
		for (int i = 0; i < fieldAr.length; i++) {
			fieldAr[i] = td.getFieldName(i);
			typeAr[i] = td.getType(i);
		}
		for (Integer fieldNumber : fields) {
			fieldAr[fieldNumber] = names.get(fieldNumber);
			typeAr[fieldNumber] = td.getType(fieldNumber);
		}
		TupleDesc newtd = new TupleDesc(typeAr, fieldAr);
		ArrayList<Tuple> newTuple = tuples;
		for (Tuple tuple : newTuple) {
			tuple.setDesc(newtd);
		}
		Relation result = new Relation(newTuple, newtd);
		return result;
	}
	
	/**
	 * This method performs a project operation on a relation
	 * @param fields a list of field numbers (refer to TupleDesc) that should be in the result
	 * @return
	 */
	public Relation project(ArrayList<Integer> fields) {
		//your code here
		ArrayList<Tuple> newTuple = new ArrayList<Tuple>();
		String[] fieldAr = new String[fields.size()];
		Type[] typeAr = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldAr[i] = td.getFieldName(fields.get(i));
			typeAr[i] = td.getType(fields.get(i));
		}
		TupleDesc newtd = new TupleDesc(typeAr, fieldAr);
		for (int i = 0; i < tuples.size(); i++) {
			Tuple tuple = new Tuple(newtd);
			for (int j = 0; j < fields.size(); j++) {
				tuple.setField(j, tuples.get(i).getField(fields.get(j)));
			}
			newTuple.add(tuple);
		}
		Relation result = new Relation(newTuple, newtd);
		return result;
	}
	
	/**
	 * This method performs a join between this relation and a second relation.
	 * The resulting relation will contain all of the columns from both of the given relations,
	 * joined using the equality operator (=)
	 * @param other the relation to be joined
	 * @param field1 the field number (refer to TupleDesc) from this relation to be used in the join condition
	 * @param field2 the field number (refer to TupleDesc) from other to be used in the join condition
	 * @return
	 */
	public Relation join(Relation other, int field1, int field2) {
		//your code here
		ArrayList<Tuple> newTuple = new ArrayList<Tuple>();
		String[] fieldAr = new String[td.numFields() + other.td.numFields()];
		Type[] typeAr = new Type[td.numFields() + other.td.numFields()];
		for (int i = 0; i < td.numFields(); i++) {
			fieldAr[i] = td.getFieldName(i);
			typeAr[i] = td.getType(i);
		}
		for (int j = 0; j < other.td.numFields(); j++) {
			fieldAr[td.numFields() + j] = other.td.getFieldName(j);
			typeAr[td.numFields() + j] = other.td.getType(j);
		}
		TupleDesc newtd = new TupleDesc(typeAr, fieldAr);
		for (Tuple tuple1 : tuples) {
			for (Tuple tuple2 : other.tuples) {
				if (tuple1.getField(field1).equals(tuple2.getField(field2))) {
					Tuple tuple = new Tuple(newtd);
					for (int i = 0; i < td.numFields(); i++) {
						tuple.setField(i, tuple1.getField(i));
					}
					for (int j = 0; j < other.td.numFields(); j++) {
						tuple.setField(td.numFields() + j, tuple2.getField(j));
					}
					newTuple.add(tuple);
				}
			}
		}
		Relation result = new Relation(newTuple, newtd);
		return result;
	}
	
	/**
	 * Performs an aggregation operation on a relation. See the lab write up for details.
	 * @param op the aggregation operation to be performed
	 * @param groupBy whether or not a grouping should be performed
	 * @return
	 */
	public Relation aggregate(AggregateOperator op, boolean groupBy) {
		//your code here
		
		return null;
	}
	
	public TupleDesc getDesc() {
		//your code here
		return td;
	}
	
	public ArrayList<Tuple> getTuples() {
		//your code here
		return tuples;
	}
	
	/**
	 * Returns a string representation of this relation. The string representation should
	 * first contain the TupleDesc, followed by each of the tuples in this relation
	 */
	public String toString() {
		//your code here
		String strOfDesc = td.toString();
		String result = strOfDesc;
		for (Tuple tuple : tuples) {
			result += tuple.toString();
		}
		return result;
	}
}
