package hw2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hw1.Catalog;
import hw1.Database;
import hw1.Field;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
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
			for(SelectItem s: selects) {
				s.accept(cv);
				//agg.add( cv.isAggregate());
				
				//check if aggregate
				if(cv.isAggregate()) {
					op = cv.getOp();
	                String nameCol = cv.getColumn();
	                try {
	                	col.add(rdc.nameToId(nameCol));
	                }catch(Exception e) {
	                // in case select other table's column
	                }
	                isAgg = true;
	                // may have problem
	                r = r.project(col).aggregate(op, groupbys != null);
	                afterSelectRelation.add(r);
				}
				//check if select all
				else if(cv.getColumn()=="*") {
					afterSelectRelation.add(r);
				}
				//normal situation
				else{
					String cvName = cv.getColumn();
					try {
						int idCol = rdc.nameToId(cvName);
	                    col.add(idCol);
					}catch(Exception e) {
						// in case select other table's column
					} 
				}	
			}//end of selected columns loop

			if(isAgg) {
				
			}else {
				r = r.project(col);
				afterSelectRelation.add(r);
			}
			 
		}//end of relation loop
		
//		return afterSelectRelation.get(0);

		
		//JOIN
		List<Join> join = sb.getJoins();
		List<Relation> joinStack = new LinkedList<>();
		
		if(join == null) {
			// offer select value
			joinStack.add(afterSelectRelation.get(0));
		}else {
			joinStack.add(afterSelectRelation.get(0));
			// join the relation one by one
			for(int i = 1; i < targetTables.size();++i) {
				//Reference source:https://github.com/zhanghao940203/wustl_CSE530S/blob/master/CSE530S/src/hw1/Query.java
				for (Join j : join) {
					//parse the SQL
	                String[] exp = j.getOnExpression().toString().split("=");
	                //String firstTable = exp[0].split("\\.")[0].trim();
	                String firstCol = exp[0].split("\\.")[1].trim();
	                //String secondTable = exp[1].split("\\.")[0].trim();
	                String secondCol = exp[1].split("\\.")[1].trim();
	                // get the two relations
	                Relation one = joinStack.get(0);
	               // int twoTableId = c.getTableId(secondTable);
	                int twoTableId = c.getTableId(targetTables.get(i));
	                
	                ArrayList<Tuple> twoTupleList = c.getDbFile(twoTableId).getAllTuples();
	                TupleDesc twoTDC = c.getTupleDesc(twoTableId);
	                Relation two = new Relation(twoTupleList,twoTDC);  
	                int fieldA = one.getDesc().nameToId(firstCol);
	                try {
	                	int fieldB = two.getDesc().nameToId(secondCol);
		                joinStack.set(0, one.join(two, fieldA, fieldB));
	                }catch(Exception e) {
//	                	joinStack.set(0, );
	                }
	                
	            }
			}
		
		}// else return
		
		
		// return Relation(ArrayList<Tuple> l, TupleDesc td)
		return joinStack.get(0);
//		return afterJoin;
		
		
	}
}
