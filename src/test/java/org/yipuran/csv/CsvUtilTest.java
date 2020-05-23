package org.yipuran.csv;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.yipuran.csv4j.ProcessingException;

/**
 * CsvUtilTest.java
 */
public class CsvUtilTest{
	String testDirectoryPath;


	@Before
	public void setUp() throws Exception{
		File file = new File(getClass().getClassLoader().getResource("").toURI());
		testDirectoryPath = file.getAbsolutePath();
	}

	@Test
	public void testCsvlineListOfString(){
		String s = CsvUtil.csvline("A", "B\"B", "C");
		assertEquals(s, "\"A\",\"B\"\"B\",\"C\"");
	}
	@Test
	public void testCsvlineStringArray(){
		String s = CsvUtil.csvline(Arrays.asList("A", "B\"B", "C"));
		assertEquals(s, "\"A\",\"B\"\"B\",\"C\"");
	}
	@Test
	public void testSplitAry(){
		String[] ary1 = CsvUtil.splitAry(',', "A,B,C");
		assertArrayEquals(ary1, new String[]{ "A", "B", "C" });

		String[] ary2 = CsvUtil.splitAry(',', "A,,C");
		assertArrayEquals(ary2, new String[]{ "A", "", "C" });

		String[] ary3 = CsvUtil.splitAry(',', ",,C");
		assertArrayEquals(ary3, new String[]{ "", "", "C" });

		String[] ary4 = CsvUtil.splitAry(',', ",");
		assertArrayEquals(ary4, new String[]{ "", "" });

		String[] ary5 = CsvUtil.splitAry(',', "");
		assertArrayEquals(ary5, new String[]{ "" });
	}
	@Test
	public void testBOMcheck() {
		try{
			assertFalse("BOM無しでない", CsvUtil.isBOMutf8(new File(getClass().getClassLoader().getResource("test.csv").toURI())));
			assertTrue("BOM有り", CsvUtil.isBOMutf8(new File(getClass().getClassLoader().getResource("test8bom.csv").toURI())));
		}catch(URISyntaxException e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testBOMcheckAndRead() {
		try(InputStream in = new BufferedInputStream(new FileInputStream(new File(testDirectoryPath+"/test.csv")))){
			assertFalse("BOM無しでない", CsvUtil.isBOMutf8(in));
			Csvprocess  process = new Csvprocess();
			process.read(new InputStreamReader(in), h->{
				assertEquals(h.toString(), "[A, B, C]");
			},(n, l)->{
				if (n==0) {
					assertEquals(l.toString(), "[あ, い, う]");
				}
			});
		}catch(IOException e){
			fail(e.getMessage());
		}catch(ProcessingException e){
			fail(e.getMessage());
		}

		try(InputStream in = new BufferedInputStream(new FileInputStream(new File(getClass().getClassLoader().getResource("test8bom.csv").toURI())))){
			assertTrue("BOM有り", CsvUtil.isBOMutf8(in));
			Csvprocess  process = new Csvprocess();
			process.read(new InputStreamReader(in), h->{
				assertEquals(h.toString(), "[A, B, C]");
			},(n, l)->{
				if (n==0) {
					assertEquals(l.toString(), "[あ, い, う]");
				}
			});
		}catch(IOException e){
			fail(e.getMessage());
		}catch(ProcessingException e){
			fail(e.getMessage());
		}catch(URISyntaxException e){
			fail(e.getMessage());
		}
	}


}
