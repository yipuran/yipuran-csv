package org.yipuran.csv;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * AllTests.java
 */
@RunWith(Suite.class)
@SuiteClasses({
	CsvCreatorTest.class,
	CsvwriteListStreamTest.class,
	CsvwriteArrayStreamTest.class,
	CsvprocessTest.class,
	CsvUtilTest.class,
})
public class AllTests{

}
