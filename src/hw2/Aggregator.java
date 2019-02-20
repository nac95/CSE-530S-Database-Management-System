package hw2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	private ArrayList<Tuple> tuples= new ArrayList<>();
	private boolean firstTime = true;
	private int sum;
	private int count;
	private Map<Integer, Integer> sumGroup = new HashMap<>();
	private Map<Integer, Integer> countGroup = new HashMap<>();
	
	
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
			if (!groupBy) {
				tuple = t;
				count++;
				sum = new IntField(t.getField(0).toByteArray()).getValue();
				if (o == AggregateOperator.COUNT) {
					tuple.setField(1, new IntField(count));
				}
				firstTime = false;
			} else {
				tuples.add(t);
				countGroup.put(new IntField(t.getField(0).toByteArray()).getValue(), 1);
				sumGroup.put(new IntField(t.getField(0).toByteArray()).getValue(), new IntField(t.getField(1).toByteArray()).getValue());
				if (o == AggregateOperator.COUNT) {
					tuples.get(0).setField(1, new IntField(1));
				}
				firstTime = false;
			}
			
		} else {
			if (groupBy) {
				boolean insert = false;
				if (t.getDesc().getType(1) == Type.STRING && td.getType(1) == Type.STRING) {
					Type[] typeAr = {t.getDesc().getType(0), t.getDesc().getType(1)};
					String[] fieldAr = {t.getDesc().getFieldName(0), t.getDesc().getFieldName(1)};
					Tuple tuple2 = new Tuple(new TupleDesc(typeAr, fieldAr));
					byte[] data1 = t.getField(1).toByteArray();
					String d1 = new StringField(data1).getValue();
					if (o == AggregateOperator.MIN || o == AggregateOperator.MAX) {
						for (int i = 0; i < tuples.size(); i++) {
							Tuple tuple = tuples.get(i);
							byte[] data2 = tuple.getField(1).toByteArray();
							String d2 = new StringField(data2).getValue();
							if (t.getField(0).equals(tuple.getField(0))) {
								if (o == AggregateOperator.MIN) {
									tuple.setField(1, new StringField(d1.compareToIgnoreCase(d2) < 0 ? d1 : d2));
								} else {
									tuple.setField(1, new StringField(d1.compareToIgnoreCase(d2) > 0 ? d1 : d2));
								}
								tuples.add(tuple);
								insert = true;
								tuples.remove(i);
								break;
							}
						}
					} else if (o == AggregateOperator.COUNT) {
						for (int i = 0; i < tuples.size(); i++) {
							Tuple tuple = tuples.get(i);
							byte[] data2 = tuple.getField(1).toByteArray();
							int d2 = new IntField(data2).getValue();
							if (t.getField(0).equals(tuple.getField(0))) {
								tuple.setField(1, new IntField(d2 + 1));
								tuples.add(tuple);
								insert = true;
								tuples.remove(i);
								break;
							}
						}
					}
					if (!insert) {
						tuple2 = t;
						tuples.add(tuple2);
					}
				} else if (t.getDesc().getType(1) == Type.INT && td.getType(1) == Type.INT) {
					Type[] typeAr = {t.getDesc().getType(0), t.getDesc().getType(1)};
					String[] fieldAr = {t.getDesc().getFieldName(0), t.getDesc().getFieldName(1)};
					Tuple tuple2 = new Tuple(new TupleDesc(typeAr, fieldAr));
					byte[] data1 = t.getField(1).toByteArray();
					int d1 = new IntField(data1).getValue();
					if (o == AggregateOperator.SUM || o == AggregateOperator.MIN || o == AggregateOperator.MAX) {
						for (int i = 0; i < tuples.size(); i++) {
							Tuple tuple = tuples.get(i);
							byte[] data2 = tuple.getField(1).toByteArray();
							int d2 = new IntField(data2).getValue();
							if (t.getField(0).equals(tuple.getField(0))) {
								if (o == AggregateOperator.SUM) {
									tuple.setField(1, new IntField(d1 + d2));
								} else if (o == AggregateOperator.MIN) {
									tuple.setField(1, new IntField(d1 < d2 ? d1 : d2));
								} else {
									tuple.setField(1, new IntField(d1 > d2 ? d1 : d2));
								}
								tuples.add(tuple);
								insert = true;
								tuples.remove(i);
								break;
							}
						}
					} else if (o == AggregateOperator.AVG || o == AggregateOperator.COUNT) {
						for (int i = 0; i < tuples.size(); i++) {
							Tuple tuple = tuples.get(i);
							if (t.getField(0).equals(tuple.getField(0))) {
								Integer key = new IntField(t.getField(0).toByteArray()).getValue();
								Integer value = sumGroup.get(key) + d1;
								sumGroup.remove(key);
								sumGroup.put(key, value);
								Integer newCount = countGroup.get(key) + 1;
								countGroup.remove(key);
								countGroup.put(key, newCount);
								if (o == AggregateOperator.AVG) {
									tuple.setField(1, new IntField(sumGroup.get(key) / countGroup.get(key)));
								} else {
									tuple.setField(1, new IntField(countGroup.get(key)));
								}
								
								tuples.add(tuple);
								insert = true;
								tuples.remove(i);
								break;
							}
						}
					} 
					if (!insert) {
						countGroup.put(new IntField(t.getField(0).toByteArray()).getValue(), 1);
						sumGroup.put(new IntField(t.getField(0).toByteArray()).getValue(), new IntField(t.getField(1).toByteArray()).getValue());
						tuple2 = t;
						tuples.add(tuple2);
					}

				}

				
			} else {
				if (t.getDesc().getType(0) == Type.STRING && td.getType(0) == Type.STRING) {
					Type[] typeAr = {Type.STRING};
					String[] fieldAr = {t.getDesc().getFieldName(0)};
					Tuple tuple2 = new Tuple(new TupleDesc(typeAr, fieldAr));
					byte[] data1 = t.getField(0).toByteArray();
					byte[] data2 = tuple.getField(0).toByteArray();
					String d1 = new StringField(data1).getValue();
					String d2 = new StringField(data2).getValue();
					if (o == AggregateOperator.MIN) {
						tuple2.setField(0, new StringField(d1.compareToIgnoreCase(d2) < 0 ? d1 : d2));
					} else if (o == AggregateOperator.MAX) {
						tuple2.setField(0, new StringField(d1.compareToIgnoreCase(d2) > 0 ? d1 : d2));
					} else if (o == AggregateOperator.COUNT) {
						count++;
						tuple2.setField(0, new IntField(count));
					}
					tuple = tuple2;
				} else if (t.getDesc().getType(0) == Type.INT && td.getType(0) == Type.INT) {
					Type[] typeAr = {Type.INT};
					String[] fieldAr = {t.getDesc().getFieldName(0)};
					Tuple tuple2 = new Tuple(new TupleDesc(typeAr, fieldAr));
					byte[] data1 = t.getField(0).toByteArray();
					byte[] data2 = tuple.getField(0).toByteArray();
					int d1 = new IntField(data1).getValue();
					int d2 = new IntField(data2).getValue();
					if (o == AggregateOperator.SUM) {
						tuple2.setField(0, new IntField(new IntField(data1).getValue() + new IntField(data2).getValue()));
					} else if (o == AggregateOperator.MIN) {
						tuple2.setField(0, new IntField(d1 < d2 ? d1 : d2));
					} else if (o == AggregateOperator.MAX) {
						tuple2.setField(0, new IntField(d1 > d2 ? d1 : d2));
					} else if (o == AggregateOperator.AVG || o == AggregateOperator.COUNT) {
						count++;
						sum += new IntField(t.getField(0).toByteArray()).getValue();
						if (o == AggregateOperator.COUNT) {
							tuple2.setField(0, new IntField(count));
						} else {
							tuple2.setField(0, new IntField(sum / count));
						}
					}
					tuple = tuple2;
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
		if (!groupBy) {
			tuples.add(tuple);
		}
		return tuples;
	}

}
