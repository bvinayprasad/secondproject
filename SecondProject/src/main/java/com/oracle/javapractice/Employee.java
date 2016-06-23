package com.oracle.javapractice;

public class Employee {
	
	private int empId;
	private String fname;
	private String lname;
	
	private JDBCHelper helper;
	
	
	
	public JDBCHelper getHelper() {
		return helper;
	}
	public void setHelper(JDBCHelper helper) {
		this.helper = helper;
	}
	
	public int getEmpId() {
		return empId;
	}
	public void setEmpId(int empId) {
		this.empId = empId;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	
	public boolean Insert(){
		boolean result;
		try {
			result = helper.InsertEmployee(this.empId, this.fname, this.lname);
			if(result)
				System.out.println("Emp inserted");
			else
				System.out.println("insertion failed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("printing inside exception block");
			result = false;
		}
		
		
		return result;
		
	}
	public Employee(int empId, String fname, String lname) {
		super();
		this.empId = empId;
		this.fname = fname;
		this.lname = lname;
	}
	
	

}
