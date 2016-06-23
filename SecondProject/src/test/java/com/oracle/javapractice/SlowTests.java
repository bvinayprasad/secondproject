package com.oracle.javapractice;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Categories.IncludeCategory(SlowTests.class)
@Suite.SuiteClasses({
AllTests.class
})
public class SlowTests	{
	
}