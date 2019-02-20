package hw2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import hw1.IntField;
import hw1.StringField;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Doug Shook
 *
 */
public class Aggregator {

	private AggregateOperator o;
	private boolean groupBy;
	private TupleDesc td;
	private Tuple tuple = new Tuple(td);
	private boolean firstTime = true;
	private int sum;
	private int count;
	private String minStr;
	private String maxStr;
	
	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		//your code here
		this.o = o;
		this.groupBy = groupBy;
		this.td = td;
	}

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */
	public void merge(Tuple t) {
		//your code here
		if (firstTime) {
			tuple = t;
			firstTime = false;
		} else {
			if (groupBy) {
				if (t.getDesc().getType(1) == Type.STRING && td.getType(1) == Type.STRING) {
					Type[] typeAr = {t.getDesc().getType(0), t.getDesc().getType(1)};
					String[] fieldAr = {t.getDesc().getFieldName(0), t.getDesc().getFieldName(1)};
					Tuple tuple2 = new Tuple(new TupleDesc(typeAr, fieldAr));
					if (o == AggregateOperator.MIN) {
						
					} else if (o == AggregateOperator.MAX) {
						
					} else if (o == AggregateOperator.COUNT) {
						count++;
						tuple2.setField(0, new IntField(count));
						tuple = tuple2;
					}
				} else if (t.getDesc().getType(1) == Type.INT && td.getType(1) == Type.INT) {
					Type[] typeAr = {t.getDesc().getType(0), t.getDesc().getType(1)};
					String[] fieldAr = {t.getDesc().getFieldName(0), t.getDesc().getFieldName(1)};
					Tuple tuple2 = new Tuple(new TupleDesc(typeAr, fieldAr));
					if (o == AggregateOperator.SUM) {
						
						byte[] data1 = t.getField(0).toByteArray();
						byte[] data2 = tuple.getField(0).toByteArray();
						tuple2.setField(0, new IntField(new IntField(data1).getValue() + new IntField(data2).getValue()));
						tuple = tuple2;
					} else if (o == AggregateOperator.MIN) {
						byte[] data1 = t.getField(0).toByteArray();
						byte[] data2 = tuple.getField(0).toByteArray();
						int d1 = new IntField(data1).getValue();
						int d2 = new IntField(data2).getValue();
						tuple2.setField(0, new IntField(d1 < d2 ? d1 : d2));
						tuple = tuple2;
					} else if (o == AggregateOperator.MAX) {
						byte[] data1 = t.getField(0).toByteArray();
						byte[] data2 = tuple.getField(0).toByteArray();
						int d1 = new IntField(data1).getValue();
						int d2 = new IntField(data2).getValue();
						tuple2.setField(0, new IntField(d1 > d2 ? d1 : d2));
						tuple = tuple2;
					} else if (o == AggregateOperator.AVG) {
						count++;
						sum += new IntField(t.getField(0).toByteArray()).getValue();
						tuple2.setField(0, new IntField(sum / count));
						tuple = tuple2;
					} else if (o == AggregateOperator.COUNT) {
						count++;
						tuple2.setField(0, new IntField(count));
						tuple = tuple2;
					}
				}

				
			} else {
				if (t.getDesc().getType(0) == Type.STRING && td.getType(0) == Type.STRING) {
					Type[] typeAr = {Type.STRING};
					String[] fieldAr = {t.getDesc().getFieldName(1)};
					Tuple tuple2 = new Tuple(new TupleDesc(typeAr, fieldAr));
					if (o == AggregateOperator.MIN) {
						byte[] data1 = t.getField(0).toByteArray();
						byte[] data2 = tuple.getField(0).toByteArray();
						String d1 = new StringField(data1).getValue();
						String d2 = new StringField(data2).getValue();
						tuple2.setField(0, new StringField(d1.compareToIgnoreCase(d2) < 0 ? d1 : d2));
						tuple = tuple2;
					} else if (o == AggregateOperator.MAX) {
						byte[] data1 = t.getField(0).toByteArray();
						byte[] data2 = tuple.getField(0).toByteArray();
						String d1 = new StringField(data1).getValue();
						String d2 = new StringField(data2).getValue();
						tuple2.setField(0, new StringField(d1.compareToIgnoreCase(d2) > 0 ? d1 : d2));
						tuple = tuple2;
					} else if (o == AggregateOperator.COUNT) {
						count++;
						tuple2.setField(0, new IntField(count));
						tuple = tuple2;
					}
				} else if (t.getDesc().getType(0) == Type.INT && td.getType(0) == Type.INT) {
					Type[] typeAr = {Type.INT};
					String[] fieldAr = {t.getDesc().getFieldName(0)};
					Tuple tuple2 = new Tuple(new TupleDesc(typeAr, fieldAr));
					if (o == AggregateOperator.SUM) {
						byte[] data1 = t.getField(0).toByteArray();
						byte[] data2 = tuple.getField(0).toByteArray();
						tuple2.setField(0, new IntField(new IntField(data1).getValue() + new IntField(data2).getValue()));
						tuple = tuple2;
					} else if (o == AggregateOperator.MIN) {
						byte[] data1 = t.getField(0).toByteArray();
						byte[] data2 = tuple.getField(0).toByteArray();
						int d1 = new IntField(data1).getValue();
						int d2 = new IntField(data2).getValue();
						tuple2.setField(0, new IntField(d1 < d2 ? d1 : d2));
						tuple = tuple2;
					} else if (o == AggregateOperator.MAX) {
						byte[] data1 = t.getField(0).toByteArray();
						byte[] data2 = tuple.getField(0).toByteArray();
						int d1 = new IntField(data1).getValue();
						int d2 = new IntField(data2).getValue();
						tuple2.setField(0, new IntField(d1 > d2 ? d1 : d2));
						tuple = tuple2;
					} else if (o == AggregateOperator.AVG) {
						count++;
						sum += new IntField(t.getField(0).toByteArray()).getValue();
						tuple2.setField(0, new IntField(sum / count));
						tuple = tuple2;
					} else if (o == AggregateOperator.COUNT) {
						count++;
						tuple2.setField(0, new IntField(count));
						tuple = tuple2;
					}
				}

			}
		}
	}

	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {
		//your code here
		ArrayList<Tuple> tuples = new ArrayList<>();
		tuples.add(tuple);
		return tuples;
	}

}
