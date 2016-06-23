package com.oracle.javapractice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * JUnit Suite Test
 * @author mkyong
 *
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        JunitExampleTest.class,
        ExpectedExceptionTest.class        
})
public class SuiteTest {
}
