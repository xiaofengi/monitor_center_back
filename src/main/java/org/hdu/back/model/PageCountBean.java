package org.hdu.back.model;


public class PageCountBean {
	private int count;
	private int offset;
	private int limit;
	public static final int DEFAULTPAGEINDEX = 0;
	public static final int DEFAULTPAGESIZE = 10;

	/**
	 * 页码，每页显示条数
	 * 
	 * @param pageIndex
	 * @param pageSize
	 */
	public PageCountBean(Integer pageIndex, Integer pageSize) {
		offset=pageIndex == null ? DEFAULTPAGEINDEX : pageIndex - 1;
		limit=pageSize == null ? DEFAULTPAGESIZE : pageSize;
				
	}

	public PageCountBean() {
		offset=DEFAULTPAGEINDEX;
		limit=DEFAULTPAGESIZE;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	@Override
	public String toString() {
		return "pagecountbeand@o"+offset+"l"+limit;
	}
}
