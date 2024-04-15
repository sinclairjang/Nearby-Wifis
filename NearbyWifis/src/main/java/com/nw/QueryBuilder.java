package com.nw;

import com.api.db.QueryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


public class QueryBuilder extends com.api.db.QuerySpec {

	public QueryBuilder(String table, String... fields) {
		super(table, fields);
	}

	public String buildQuery(QueryType queryType, Object...args) {
		return buildQuery(queryType, false, args);
	}
	
	public String buildQuery(QueryType queryType, boolean verbose, Object... args) {
		reportNullValues(verbose);
		
		if (queryType == QueryType.INSERT) return buildInsertQuery();
		else if (queryType == QueryType.SELECT_LIMIT) {
			boolean startSet = false;
			Integer start = 0;
			Integer end = 0;
			List<String> queries = new ArrayList<>();
			for (Object arg : args) {
				if (arg instanceof Integer) {
					if (!startSet) {
						start = (Integer) arg;
					}
					if (startSet) {
						end = (Integer) arg;
					}
					startSet = true;
				} else if (arg instanceof String) {
					queries.add((String) arg);
				}
			}
			
			if (end == 0) {
				Integer temp = end;
				end = start;
				start = temp;
			}
			return buildSelectLimitQuery(start, end, queries);
		}
		else if (queryType == QueryType.SELECT_WHERE) {
			List<String> fromFields = new ArrayList<>();
			WhereClause whereClause = null;
			for (Object arg : args) {
				if (arg instanceof String) {
					fromFields.add((String) arg);
				} else if (arg instanceof WhereClause) {
					whereClause = (WhereClause) arg;
				}
			}
			return buildSelectWhereQuery(fromFields, whereClause);
		}
		else if (queryType == QueryType.SELECT_NATURAL_JOIN) {
			List<String> selectFields = new ArrayList<>();
			TableClause tableClause = null;
			OrderClause orderClause = null;
			for (Object arg : args) {
				if (arg instanceof String) {
					selectFields.add((String) arg);
				} else if (arg instanceof TableClause) {
					tableClause = (TableClause) arg;
				} else if (arg instanceof OrderClause) {
					orderClause = (OrderClause) arg;
				}
			}
			return buildSelectNaturalJoin(selectFields, tableClause, orderClause);
		}
		else if (queryType == QueryType.UPDATE) return buildUpdateQuery();
		else if (queryType == QueryType.DELETE) return buildDeleteQuery();
		else throw new RuntimeException(String.format("Invalid query type: (%s)", queryType));
	}
	
	private String buildSelectNaturalJoin(List<String> selectFields, TableClause tableClause, OrderClause orderClause) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		if (selectFields.size() == 0) {
			throw new RuntimeException(("No field(s) to query for!"));
		}
		Iterator<String> keyIter = selectFields.iterator();
		while (keyIter.hasNext()) {
			String field = keyIter.next();
			if (!nameMap.containsKey(field)) {
				if (!field.equals(orderClause.getOrderByField()))
					throw new RuntimeException(String.format("Invalid field: %s", field));
			}
			sb.append(field);
			if (keyIter.hasNext()) {
				sb.append(",");
			} else {
				sb.append(" ");
			}
		}
		
		sb.append("FROM ");
		sb.append(table);
		sb.append(" NATURAL JOIN ");
		sb.append(tableClause.getTableName());
		sb.append(" ORDER BY ");
		sb.append(orderClause.getOrderByField());
		
		return sb.toString();		
	}

	private void reportNullValues(boolean verbose) {	
		StringBuilder sb = new StringBuilder();
		boolean guard = false;
		for (String field : nameMap.keySet()) {
			if (nameMap.get(field) == null) {
				if (!guard) {
					String s = "The following entries are not speicified:\n";
					sb.append(s);
					guard = true;
				}		
				sb.append(field);
				sb.append("\n");
			}
		}
		
		if (verbose)
			System.out.println(sb.toString());
		
	}
	
	private String buildInsertQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(table);
		sb.append(" (");
		
		Set<String> keys = nameMap.keySet();
		Iterator<String> keyIter = keys.iterator();
		while (keyIter.hasNext()) {
			String field = keyIter.next();
			
			sb.append(field);
			if (keyIter.hasNext()) {
				sb.append(",");
			} else {
				sb.append(") ");
			}
		}
		
		sb.append("VALUES (");
		Collection<Object> values = nameMap.values();
		Iterator<Object> valueIter = values.iterator();
		while (valueIter.hasNext()) {
			valueIter.next();
			
			sb.append("?");
			if (valueIter.hasNext()) {
				sb.append(",");
			} else {
				sb.append(") ");
			}
		}
		return sb.toString();
	}
	
	private String buildSelectLimitQuery(int start, int end, List<String> queries) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		if (queries.size() == 0) {
			throw new RuntimeException(("No field(s) to query for!"));
		}
		Iterator<String> keyIter = queries.iterator();
		while (keyIter.hasNext()) {
			String field = keyIter.next();
			if (!nameMap.containsKey(field)) {
				throw new RuntimeException(String.format("Invalid field: %s", field));
			}
			sb.append(field);
			if (keyIter.hasNext()) {
				sb.append(",");
			} else {
				sb.append(" ");
			}
		}
		
		sb.append("FROM ");
		sb.append(table);
		sb.append(" LIMIT ");
		sb.append(start);
		sb.append(", ");
		sb.append(end);
		
		return sb.toString();
	}
	
	private String buildSelectWhereQuery(List<String> fromFields, WhereClause whereClause) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		if (fromFields.size() == 0 || whereClause == null) {
			throw new RuntimeException(("No field(s) to query for!"));
		}
		Iterator<String> fromKeyIter = fromFields.iterator();
		while (fromKeyIter.hasNext()) {
			String field = fromKeyIter.next();
			if (!nameMap.containsKey(field)) {
				throw new RuntimeException(String.format("Invalid field: %s", field));
			}
			sb.append(field);
			if (fromKeyIter.hasNext()) {
				sb.append(",");
			} else {
				sb.append(" ");
			}
		}
		
		sb.append("FROM ");
		sb.append(table);
		sb.append(" WHERE ");
		Iterator<String> whereKeyIter = whereClause.getWhereMap().keySet().iterator();
		while (whereKeyIter.hasNext()) {
			String field = whereKeyIter.next();
			if (!nameMap.containsKey(field)) {
				throw new RuntimeException(String.format("Invalid field: %s", field));
			}
			sb.append(field);
			sb.append(" IN ");
			sb.append("(");
			Iterator<String> valueIter = whereClause.getWhereMap().get(field).iterator();
			while (valueIter.hasNext()) {
				String value = valueIter.next();
				if (value == null) {
					throw new RuntimeException(String.format("No conditional value to query for %s!", field));
				}
				sb.append("'");
				sb.append(value);
				sb.append("'");
				if (valueIter.hasNext()) {
					sb.append(",");
				} else {
					sb.append(")");
				}
			}
			
			if (whereKeyIter.hasNext()) {
				sb.append(" AND ");
			} else {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}
	
	private String buildUpdateQuery() {
		return null;
	}
	private String buildDeleteQuery() {
		return null;
	}

	public WhereClause makeWhereClause(String... fields) {
		return new WhereClause(fields);
	}
	
	public OrderClause makeOrderClause(String field) {
		return new OrderClause(field);
	}
	
	public TableClause makeTableClause(String tableName) {
		return new TableClause(tableName);
	}
	
	public class WhereClause {
		private LinkedHashMap<String, List<String>> whereMap;
		
		public WhereClause(String... fields) {
			whereMap = new LinkedHashMap<>();
			for (String field : fields) {
				if (!nameMap.containsKey(field)) {
					throw new RuntimeException(String.format("Invalid field: %s", field));
				}
				
				whereMap.put(field, new ArrayList<>());
			}
		}
		
		public void setCondition(String field, String... values) {
			if (!nameMap.containsKey(field) || !whereMap.containsKey(field)) {
				throw new RuntimeException(String.format("Invalid field: %s", field));
			}
			
			for (String value : values) {
				whereMap.get(field).add(value);
			}
		}
		
		public LinkedHashMap<String, List<String>> getWhereMap() { return whereMap; }
	}
	
	public class OrderClause {
		private String orderByField;
		
		public OrderClause(String field) {
			this.orderByField = field;
		}
		
		public String getOrderByField() {
			return orderByField;
		}
	}
	
	public class TableClause {
		private String tableName;
		
		public TableClause(String tableName) {
			this.tableName = tableName;
		}
		
		public String getTableName() {
			return tableName;
		}
	}

}
