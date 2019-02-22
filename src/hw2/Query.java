package hw2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hw1.Catalog;
import hw1.Database;
import hw1.Field;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Query {

	private String q;
	
	public Query(String q) {
		this.q = q;
	}
	
	public Relation execute()  {
		Statement statement = null;
		try {
			statement = CCJSqlParserUtil.parse(q);
		} catch (JSQLParserException e) {
			System.out.println("Unable to parse query");
			e.printStackTrace();
		}
		Select selectStatement = (Select) statement;
		PlainSelect sb = (PlainSelect)selectStatement.getSelectBody();
		//System.out.println("statement: "+selectStatement);
		System.out.println("plainselect: "+sb);
		//your code here
		
		 TablesNamesFinder tnf = new TablesNamesFinder();
		
		// FROM 
		List<String> targetTables = tnf.getTableList(selectStatement);
		Catalog c = Database.getCatalog();
		List<Integer> tablesID = new ArrayList<Integer>();
		List<TupleDesc> tablesTupleDesc = new ArrayList<>();
		List<ArrayList<Tuple>> tablesTuples = new ArrayList<>();
		List<Relation> tableRelations = new ArrayList<>();
		//Relation tableRelation;
		
		for(String s: targetTables) {
			int tableid = c.getTableId(s);
			TupleDesc td = c.getTupleDesc(tableid);
			tablesID.add(c.getTableId(s));
			tablesTupleDesc.add(td);
			tablesTuples.add(c.getDbFile(tableid).getAllTuples());
			tableRelations.add(new Relation(c.getDbFile(tableid).getAllTuples(),td));
			
		}
		
		// WHERE
		 WhereExpressionVisitor wVis = new WhereExpressionVisitor();
		 Expression where = sb.getWhere();
		 String whereLeft;
		 Field whereRight;
		 RelationalOperator whereOp;
		 List<Relation> afterWhereRelations = new ArrayList<>();
		 //Relation afterWhereRelation;
		 
		 if(where==null) {
			 //System.out.println("No WHERE clause detected");
			 afterWhereRelations = tableRelations;
		 }else {
			 where.accept(wVis);
			 whereLeft =  wVis.getLeft();
			 whereOp = wVis.getOp();
			 whereRight = wVis.getRight();
			 
			 //if it is list of relation
			 for(int i = 0; i < tableRelations.size();++i){
				 Relation r = tableRelations.get(i);
				 try {
					 Relation res = r.select(r.getDesc().nameToId(whereLeft), whereOp, whereRight);
					 System.out.println("where process relation: "+res.toString());
					 afterWhereRelations.add(res);
				 }catch(Exception e) {
					 //in case one relation does not have this column
				 }
			 }// end of the relation loop
			 
		 }// end of if where not null
		 		 
		 //SELECT
		List<SelectItem> selects = sb.getSelectItems();
		Map<Integer, SelectItem> selectsMap = new HashMap<Integer, SelectItem>();
		int index = 0;
		for(SelectItem s:selects) {
			selectsMap.put(index, s);
			index++;
		}
		Iterator<SelectItem> it1 = selectsMap.values().iterator();
		ColumnVisitor cv = new ColumnVisitor();
		List<Expression> groupbys = sb.getGroupByColumnReferences();//group by
		
		ArrayList<Integer> col = new ArrayList<>();
		List<Relation> afterSelectRelation = new ArrayList<>();
		
		boolean isAgg = false;
		//List<Boolean> agg = new ArrayList<>();
		
		for(Relation r:afterWhereRelations) {
			TupleDesc rdc = r.getDesc();
			AggregateOperator op;
			
			//loop through all the selected columns
			while(it1.hasNext()) {
				SelectItem s = it1.next();
				s.accept(cv);
				//check if aggregate
				// it is aggregate
				if(cv.isAggregate()) {
					op = cv.getOp();
					if(cv.getColumn()=="*") {
						r = r.aggregate(op, groupbys != null);
						afterSelectRelation.add(r);
						
					}else {
						
		                String nameCol = cv.getColumn();
		                try {
		                	col.add(rdc.nameToId(nameCol));
		                	selects.remove(s);
		                }catch(Exception e) {
		                // in case select other table's column
		                }
		                isAgg = true;
		                r = r.project(col).aggregate(op, groupbys != null);
		                afterSelectRelation.add(r);
					}
				}
				// if it is not aggregate
				else {
					//check if select all
					if(cv.getColumn()=="*") {
						afterSelectRelation.add(r);
					}
					//normal situation
					else{
						String cvName = cv.getColumn();
						try {
							int idCol = rdc.nameToId(cvName);
		                    col.add(idCol);
		                    selects.remove(s);
						}catch(Exception e) {
							// in case select other table's column
						} 
					}// else end
				}//else for not aggr end

			}//end of selected columns loop
			if(!isAgg) {
				r = r.project(col);
				afterSelectRelation.add(r);
			}
		}//end of relation loop
		
		//JOIN
		List<Join> join = sb.getJoins();
		List<Relation> joinStorage = new LinkedList<>();
		System.out.println("before Join: "+afterSelectRelation.get(0).toString());
		if(join == null) {
			// offer select value
			joinStorage.add(afterSelectRelation.get(0));
		}else {
			joinStorage.add(afterSelectRelation.get(0));
			// join the relation one by one
//			for(int i = 1; i < afterSelectRelation.size();++i) {
				//Reference source:https://github.com/zhanghao940203/wustl_CSE530S/blob/master/CSE530S/src/hw1/Query.java
			int i = 1;	
			for (Join j : join) {
					System.out.println(j.toString());
					System.out.println(join.size());
					//parse the SQL
	                String[] exp = j.getOnExpression().toString().split("=");
	                String firstCol = exp[0].split("\\.")[1].trim();
	                String secondCol = exp[1].split("\\.")[1].trim();
	                // get the two relations
	                Relation one = joinStorage.get(0);
	                Relation two = afterSelectRelation.get(i);
	                System.out.println("rel 1: "+one.toString());
	                System.out.println("rel 2: "+two.toString());
	                try {
	                	int fieldA = one.getDesc().nameToId(firstCol);
	                	int fieldB = two.getDesc().nameToId(secondCol);
	                	System.out.println("get b1 b2?? "+fieldA+" "+fieldB);
	                	System.out.println("During join: "+one.join(two, fieldA, fieldB).toString());
		                joinStorage.set(0, one.join(two, fieldA, fieldB));
	                }catch(Exception e) {
	                	String[] fieldAr = new String[one.getDesc().numFields() + two.getDesc().numFields()];
	            		Type[] typeAr = new Type[one.getDesc().numFields() + two.getDesc().numFields()];
	            		for (int n = 0; n < one.getDesc().numFields(); ++n) {
	            			fieldAr[n] = one.getDesc().getFieldName(i);
	            			typeAr[n] = one.getDesc().getType(i);
	            		}
	            		for (int m = 0; m < two.getDesc().numFields(); ++m) {
	            			fieldAr[one.getDesc().numFields() + m] = two.getDesc().getFieldName(m);
	            			typeAr[one.getDesc().numFields() + m] = two.getDesc().getType(m);
	            		}
	            		TupleDesc newtd = new TupleDesc(typeAr, fieldAr);
	            		ArrayList<Tuple> emptyT = new ArrayList<Tuple>();
	            		Relation emptyR = new Relation(emptyT, newtd);
	            		System.out.println("empty rel: "+emptyR.toString());
	                	joinStorage.set(0, emptyR);
	                }// catch exception end
	                i++;
	                
//	                // get the two relations
//	                Relation one = joinStorage.get(0);
//	               // int twoTableId = c.getTableId(secondTable);
//	                
//	                int twoTableId = c.getTableId(targetTables.get(i));
//	                ArrayList<Tuple> twoTupleList = c.getDbFile(twoTableId).getAllTuples();
//	                TupleDesc twoTDC = c.getTupleDesc(twoTableId);
//	                Relation two = new Relation(twoTupleList,twoTDC);  
//	                System.out.println("get col1 cols: "+firstCol+" "+secondCol);
////	                int fieldA = one.getDesc().nameToId(firstCol);
//	                try {
//	                	int fieldA = one.getDesc().nameToId(firstCol);
//	                	int fieldB = two.getDesc().nameToId(secondCol);
//	                	System.out.println("get b1 b2?? "+fieldA+" "+fieldB);
//	                	System.out.println("During join: "+one.join(two, fieldA, fieldB).toString());
//		                joinStorage.set(0, one.join(two, fieldA, fieldB));
//	                }catch(Exception e) {
//	                	String[] fieldAr = new String[one.getDesc().numFields() + two.getDesc().numFields()];
//	            		Type[] typeAr = new Type[one.getDesc().numFields() + two.getDesc().numFields()];
//	            		for (int n = 0; n < one.getDesc().numFields(); ++n) {
//	            			fieldAr[n] = one.getDesc().getFieldName(i);
//	            			typeAr[n] = one.getDesc().getType(i);
//	            		}
//	            		for (int m = 0; m < two.getDesc().numFields(); ++m) {
//	            			fieldAr[one.getDesc().numFields() + m] = two.getDesc().getFieldName(m);
//	            			typeAr[one.getDesc().numFields() + m] = two.getDesc().getType(m);
//	            		}
//	            		TupleDesc newtd = new TupleDesc(typeAr, fieldAr);
//	            		ArrayList<Tuple> emptyT = new ArrayList<Tuple>();
//	            		Relation emptyR = new Relation(emptyT, newtd);
//	            		System.out.println("empty rel: "+emptyR.toString());
//	                	joinStorage.set(0, emptyR);
	               // }// catch exception end
	
	            }// join loop end
//			}
				
		
		}
		return joinStorage.get(0);
//		return afterJoin;
		
		
	}
}
