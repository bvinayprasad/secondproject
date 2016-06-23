package com.oracle.javapractice;

import org.easymock.EasyMock;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

/**
 * @author mkyong
 *
 */
public class JunitExampleTest {

    private Collection collection;

    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code   
    	System.out.println("@BeforeClass - oneTimeSetUp");
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    	System.out.println("@AfterClass - oneTimeTearDown");
    }

    @Before
    public void setUp() {
        collection = new ArrayList();
        System.out.println("@Before - setUp");
    }

    @After
    public void tearDown() {
        collection.clear();
        System.out.println("@After - tearDown");
    }
    
    @Ignore("Not Ready to Run")  
    @Test
    public void nameTest() {
    	MyExample myExample=new MyExample();
    	String name=myExample.myName("Vinay", "B");
    	assertEquals("Vinay,B", name);
    }
    
    @Test
    public void nameEasyMockTest() {
    	Name name=EasyMock.createNiceMock(Name.class);
    	MyExample example=EasyMock.createNiceMock(MyExample.class);
    	name.setFname("Nalini");
    	name.setLname("Kanta");
    	String result="Nalini,K";

    	EasyMock.expect(example.myName(name)).andReturn(result);
    	EasyMock.replay(example);
    	assertEquals("Nalini,K", result);
    }

    @Test
    public void testEmptyCollection() {
        assertTrue(collection.isEmpty());
        System.out.println("@Test - testEmptyCollection");
    }

    @Test
    public void testOneItemCollection() {
        collection.add("itemA");
        assertEquals(1, collection.size());
        System.out.println("@Test - testOneItemCollection");
    }
}
