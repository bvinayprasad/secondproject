package com.oracle.javapractice;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class MathUtilsTest {

	private int numberA;
	private int numberB;
	private int expected;

	//parameters pass via this constructor
	public MathUtilsTest(int numberA, int numberB, int expected) {
		this.numberA = numberA;
		this.numberB = numberB;
		this.expected = expected;
	}

	//Declares parameters here
	@Parameters//(name = "{index}: add({0}+{1})={2}")
	public static Collection data1() {
		return Arrays.asList(new Object[][] { 
			{ 1, 1, 2 }, 
			{ 2, 2, 4 }, 
			{ 8, 2, 10 }, 
			{ 4, 5, 9 } 
		});
	}

	@Test
	@Category(com.oracle.javapractice.SlowTests.class)
	public void test_add() {	
		assertEquals(expected,MathUtils.add(numberA, numberB));
	}

}