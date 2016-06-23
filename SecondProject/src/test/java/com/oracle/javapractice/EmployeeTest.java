package com.oracle.javapractice;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

public class EmployeeTest {

	@Test
	public void test() throws Exception {
		
		Employee emp = new Employee(1,"Vinay","Prasad");
		JDBCHelper helper = EasyMock.createMock(JDBCHelper.class);
		emp.setHelper(helper);
		
		EasyMock.expect(helper.InsertEmployee(1, "Vinay", "Prasad")).andReturn(true);
		EasyMock.replay(helper);
		
		assertTrue(emp.Insert());
		
	}
	

	@Test
	public void Secondtest() throws Exception {
		
		Employee emp = new Employee(1,"Vinay","Prasad");
		JDBCHelper helper = EasyMock.createMock(JDBCHelper.class);
		emp.setHelper(helper);
		
		EasyMock.expect(helper.InsertEmployee(1, "Vinay", "Prasad")).andThrow(new Exception("throwing"));
		EasyMock.replay(helper);
		
		assertFalse(emp.Insert());
		
	}

}
