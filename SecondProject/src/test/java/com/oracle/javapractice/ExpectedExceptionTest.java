package com.oracle.javapractice;

import org.junit.*;

/**
 * JUnit Expected Exception Test
 * @author mkyong
 *
 */
public class ExpectedExceptionTest {
   
	@Test(expected = ArithmeticException.class)  
	public void divisionWithException() {  
	  int i = 1/0;
	}  
	
	@Test(timeout = 1000)  
	public void infinity() {  
		while (true);  
	}  
    
}
