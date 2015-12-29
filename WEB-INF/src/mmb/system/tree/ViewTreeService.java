package mmb.system.tree;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class ViewTreeService extends BaseServiceImpl {
    public ViewTreeService(int useConnType, DbOperation dbOp) {
        this.useConnType = useConnType;
        this.dbOp = dbOp;
    }    
    
//    public ViewTreeService() {
//        this.useConnType = CONN_IN_SERVICE;
//    }

    public boolean hasViewTree(String cound) {
    	if (cound == null) {
    		return false;
    	}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		ResultSet rs = dbOp.executeQuery("select * from view_tree where " + cound);
		try {
			if (rs != null &&rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return false;
    }
    
	public List getViewTreeList() {
		List list = new ArrayList();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		String query = "select * from view_tree order by seq,id";
		ResultSet rs = dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				ViewTree node = new ViewTree();
				node.setId(rs.getInt("id"));
				node.setParentId(rs.getInt("parent_id"));
				node.setName(rs.getString("name"));
				node.setTarget(rs.getString("target"));
				node.setLimits(rs.getString("limits"));
				node.setUrl(rs.getString("url"));
				node.setSeq(rs.getInt("seq"));
				node.setNodeUrl(rs.getString("node_url"));
				list.add(node);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return list;
	}
	
	public List getViewTreeList(int parentId) {
		List list = new ArrayList();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		String query = "select * from view_tree where parent_id=" + parentId + " order by seq,id";
		ResultSet rs = dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				ViewTree node = new ViewTree();
				node.setId(rs.getInt("id"));
				node.setParentId(rs.getInt("parent_id"));
				node.setName(rs.getString("name"));
				node.setTarget(rs.getString("target"));
				node.setLimits(rs.getString("limits"));
				node.setUrl(rs.getString("url"));
				node.setSeq(rs.getInt("seq"));
				node.setNodeUrl(rs.getString("node_url"));
				list.add(node);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return list;
	}

	public boolean addViewTree(ViewTree viewTree) {
		StringBuilder query = new StringBuilder();
		query.append("insert into view_tree (name,parent_id,target,limits,url,node_url,seq)values('");
		query.append(viewTree.getName());
		query.append("',");
		query.append(viewTree.getParentId());
		query.append(",'");
		query.append(viewTree.getTarget());
		query.append("','");
		query.append(viewTree.getLimits());
		query.append("','");
		query.append(viewTree.getUrl());
		query.append("','");
		query.append(viewTree.getNodeUrl());
		query.append("',");
		query.append(viewTree.getSeq());
		query.append(")");
		DbOperation dbOp = new DbOperation();
		dbOp.init();
		boolean suc = false;
		try {
			dbOp.executeUpdate(query.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return suc;
	}

	public boolean deleteViewTree(String condition) {
		DbOperation dbOp = new DbOperation();
		dbOp.init();
		boolean suc = false;
		try {
			dbOp.executeUpdate(condition);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return suc;
	}

	public boolean updateViewTree(String set, String condition) {
		if (set == null || condition == null) {
			return false;
		}

		// 数据库操作类
		DbOperation dbOp = new DbOperation();
		if (!dbOp.init()) {
			return false;
		}

		String query = "update view_tree set " + set + " where " + condition;

		boolean result = false;
		try {
			result = dbOp.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
		} finally {
			dbOp.release();
		}

		return result;
	}

}
