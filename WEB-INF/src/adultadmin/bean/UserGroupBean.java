/*
 * Created on 2005-11-15
 *
 */
package adultadmin.bean;

import java.io.Serializable;

import mmb.util.BinaryFlag;

/**
 * @author bomb
 * 
 */
public class UserGroupBean implements Cloneable, Serializable{
	
	public static String deptNames[] = {"无", "无", "物流库存部", "客服部", "运营中心", "推广部", "商品采购部", "销售部", "平台部"};
	public static String securityLevelNames[] = {"普通用户", "普通用户", "普通用户", "普通用户", "普通用户", "普通管理员", 
		"普通用户", "普通用户", "普通用户", "高级管理员", "超级管理员"};
	
	int id;
	String name;
	String bak;
	
	long flag;		// 参数=权限设置
	long flag2;
	long flag3;
	long flag4;
	long flag5;
	long flag6;
	long flag7;
	long flag8;
	long flag9;
	long flag10;
	int seq;		// 显示排序

	int dept;		// 部门;
	public String getDeptName() {
		return deptNames[dept];
	}

	int catalog;	// 分类（备用）
	
	
	BinaryFlag flags = new BinaryFlag(); // 保存所有的flag1 - 10等权限
	
	public BinaryFlag getFlags() {
		return flags;
	}
	public void setFlags(BinaryFlag flags) {
		this.flags = flags;
		// 加入旧权限
//		flags.or(new BinaryFlag(new long[]{flag, flag2, flag3, flag4, flag5, flag6, flag7, flag8, flag9, flag10}, 60));
	}
	
	public void binaryFlagOr(BinaryFlag mg) {
		if (mg == null) {
			return;
		}
		flags.or(mg);
	}
	
	public void setFlags(long[] flags) {
//		flag = flags[0];
//		flag2 = flags[1];
//		flag3 = flags[2];
//		flag4 = flags[3];
//		flag5 = flags[4];
//		flag6 = flags[5];
//		flag7 = flags[6];
//		flag8 = flags[7];
//		flag9 = flags[8];
//		flag10 = flags[9];
	}
	public void mergeFlags(UserGroupBean mg) {
//		flag |= mg.getFlag();
//		flag2 |= mg.getFlag2();
//		flag3 |= mg.getFlag3();
//		flag4 |= mg.getFlag4();
//		flag5 |= mg.getFlag5();
//		flag6 |= mg.getFlag6();
//		flag7 |= mg.getFlag7();
//		flag8 |= mg.getFlag8();
//		flag9 |= mg.getFlag9();
//		flag10 |= mg.getFlag10();
		flags.or(mg.getFlags());
	}
	public void maskFlags(UserGroupBean mg) {
//		flag &= mg.getFlag();
//		flag2 &= mg.getFlag2();
//		flag3 &= mg.getFlag3();
//		flag4 &= mg.getFlag4();
//		flag5 &= mg.getFlag5();
//		flag6 &= mg.getFlag6();
//		flag7 &= mg.getFlag7();
//		flag8 &= mg.getFlag8();
//		flag9 &= mg.getFlag9();
//		flag10 &= mg.getFlag10();
		flags.and(mg.getFlags());
	}
	
	public boolean isFlag(int f) {
//		if(f < 0)
//			return false;
//		if(f < 60)
//			return (flag & (1l << f)) != 0;
//		if(f < 120)
//			return (flag2 & (1l << (f - 60))) != 0;
//		if(f < 180)
//			return (flag3 & (1l << (f - 120))) != 0;
//		if(f < 240)
//			return (flag4 & (1l << (f - 180))) != 0;
//		if(f < 300)
//			return (flag5 & (1l << (f - 240))) != 0;
//		if(f < 360)
//			return (flag6 & (1l << (f - 300))) != 0;
//		if(f < 420)
//			return (flag7 & (1l << (f - 360))) != 0;
//		if(f < 480)
//			return (flag8 & (1l << (f - 420))) != 0;
//		if(f < 540)
//			return (flag9 & (1l << (f - 480))) != 0;
//		if(f < 600)
//			return (flag10 & (1l << (f - 540))) != 0;
//		return false;
		return flags.get(f);
	}
	
//	public static void main(String[] args) {
//		Random r = new Random();
//		UserGroupBean g = new UserGroupBean();
//		g.flag = r.nextLong();
//		g.flag2 = r.nextLong();
//		g.flag3 = r.nextLong();
//		g.flag4 = r.nextLong();
//		g.flag5 = r.nextLong();
//		g.flag6 = r.nextLong();
//		g.flag8 = r.nextLong();
//		g.flag9 = r.nextLong();
//		g.flag10 = r.nextLong();
//		
//		System.out.println(g.flag);
//		
//		UserGroupBean g2 = new UserGroupBean();
//		g2.flags = new BinaryFlag(new long[]{g.flag, g.flag2, g.flag3, g.flag4, g.flag5, g.flag6, g.flag7, g.flag8, g.flag9, g.flag10}, 60);
//		int sum = 0;
//		for(int i = 0;i < 600;i++) {
//			if(g.isFlagOld(i) ^ g2.isFlag(i)) {
//				System.out.println(i + " : " + g.isFlagOld(i) +" != "+ g2.isFlag(i));
//				sum++;
//			}
//		}
//		System.out.println("different = " + sum);
//	}
	
	public boolean isFlagOld(int f) {
		if(f < 0)
			return false;
		if(f < 60)
			return (flag & (1l << f)) != 0;
		if(f < 120)
			return (flag2 & (1l << (f - 60))) != 0;
		if(f < 180)
			return (flag3 & (1l << (f - 120))) != 0;
		if(f < 240)
			return (flag4 & (1l << (f - 180))) != 0;
		if(f < 300)
			return (flag5 & (1l << (f - 240))) != 0;
		if(f < 360)
			return (flag6 & (1l << (f - 300))) != 0;
		if(f < 420)
			return (flag7 & (1l << (f - 360))) != 0;
		if(f < 480)
			return (flag8 & (1l << (f - 420))) != 0;
		if(f < 540)
			return (flag9 & (1l << (f - 480))) != 0;
		if(f < 600)
			return (flag10 & (1l << (f - 540))) != 0;
		return false;
	}
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public int getCatalog() {
		return catalog;
	}

	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}

	public int getDept() {
		return dept;
	}

	public void setDept(int dept) {
		this.dept = dept;
	}

	public long getFlag() {
		return flag;
	}

	public void setFlag(long flag) {
		this.flag = flag;
	}

	public long getFlag2() {
		return flag2;
	}

	public void setFlag2(long flag2) {
		this.flag2 = flag2;
	}



	public long getFlag3() {
		return flag3;
	}



	public void setFlag3(long flag3) {
		this.flag3 = flag3;
	}



	public long getFlag4() {
		return flag4;
	}



	public void setFlag4(long flag4) {
		this.flag4 = flag4;
	}



	public long getFlag5() {
		return flag5;
	}



	public void setFlag5(long flag5) {
		this.flag5 = flag5;
	}

	public long getFlag6() {
		return flag6;
	}
	public void setFlag6(long flag6) {
		this.flag6 = flag6;
	}
	public long getFlag7() {
		return flag7;
	}
	public void setFlag7(long flag7) {
		this.flag7 = flag7;
	}
	public long getFlag8() {
		return flag8;
	}
	public void setFlag8(long flag8) {
		this.flag8 = flag8;
	}
	public long getFlag9() {
		return flag9;
	}
	public void setFlag9(long flag9) {
		this.flag9 = flag9;
	}
	public long getFlag10() {
		return flag10;
	}
	public void setFlag10(long flag10) {
		this.flag10 = flag10;
	}
	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

}
