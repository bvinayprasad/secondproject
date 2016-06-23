package com.oracle.javapractice;

/**
 * Hello world!
 *
 */
public class MyExample 
{

	private String fname;
	private String lname;
	
	public String myName(String fn,String ln) {
		return fn+","+ln;
	}
	
	public String myName(Name name) {
		return name.getFname()+","+name.getLname();
	}
}
