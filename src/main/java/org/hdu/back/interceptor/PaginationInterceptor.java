package org.hdu.back.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.hdu.back.model.PageCountBean;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

@Component
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class ,Integer.class}) })
public class PaginationInterceptor implements Interceptor {

	/**
	 * 分页拦截，可将结果进行物理分页，并且将总数设置到PageCountBean对象中
	 */
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation
				.getTarget();
		BoundSql bound = statementHandler.getBoundSql();
		Object rb = bound.getParameterObject();
		PageCountBean rowBounds = null;
		if (rb instanceof PageCountBean) {
			rowBounds = (PageCountBean) rb;
		}else if(rb instanceof Map){
			for(Map.Entry<String, Object> entry:((Map<String,Object>)rb).entrySet()){
				if(entry.getValue() instanceof PageCountBean){
					rowBounds = (PageCountBean) entry.getValue();
					break;
				}
			}
		}
		if (rowBounds != null) {
			String sql = bound.getSql();
			Field field = bound.getClass().getDeclaredField("sql");
			field.setAccessible(true);
			field.set(bound, sql + " limit " + rowBounds.getOffset()
					* rowBounds.getLimit() + "," + rowBounds.getLimit());
			Connection connection = (Connection) invocation.getArgs()[0];
			if(sql.toUpperCase().contains("group by".toUpperCase()) || sql.toUpperCase().contains("DISTINCT")) {
				sql = "select count(1) from (" + sql + ") a";
			}else{
				sql = "select count(1) " + sql.substring(sql.toLowerCase().indexOf("from"));
			}
			PreparedStatement ps = connection
					.prepareStatement(sql);
			statementHandler.getParameterHandler().setParameters(ps);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int totalRecord = rs.getInt(1);
				// 给当前的参数page对象设置总记录数
				rowBounds.setCount(totalRecord);
			}
		}
		return invocation.proceed();
	}
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public void setProperties(Properties pro) {

	}

}
