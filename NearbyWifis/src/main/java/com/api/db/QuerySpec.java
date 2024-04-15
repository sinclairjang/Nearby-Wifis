package com.api.db;

import java.util.LinkedHashMap;

public abstract class QuerySpec {
	protected String table;
	protected LinkedHashMap<String, Object> nameMap;
	
	public QuerySpec(String table, String... fields) {
		this.table = table;
		this.nameMap = new LinkedHashMap<>();
		for (String field : fields) {
			this.nameMap.put(field, null);
		}
	}
	
	public void setQueryField(String field, Object value) {
		if (nameMap.containsKey(field)) {
			nameMap.put(field, value);
		} else {
			throw new RuntimeException(String.format("Invalid field name: (%s)", field));
		}
		
	}
	
	public Object getQueryField(String field) {
		if (nameMap.containsKey(field)) {
			return nameMap.get(field);
			
		} else {
			throw new RuntimeException(String.format("Invalid field name: (%s)", field));
		}
	}
	
	public abstract String buildQuery(QueryType queryType, Object... args);
		

	@Override
	public String toString() {
		return nameMap.toString();
	}


}
