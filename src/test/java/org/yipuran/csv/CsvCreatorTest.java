package org.yipuran.csv;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.yipuran.csv4j.ParseException;
import org.yipuran.csv4j.ProcessingException;

/**
 * CsvCreatorTest.java
 */
public class CsvCreatorTest{
	List<String[]> list;
	String testDirectoryPath;

	@Before
	public void setUp() throws Exception{
		list = new ArrayList<>();
		list.add(new String[]{"A", "B", "C"});
		list.add(new String[]{"あ", "い", "う"});
		list.add(new String[]{"1", null, "3"});

		File file = new File(getClass().getClassLoader().getResource("").toURI());
		testDirectoryPath = file.getAbsolutePath();
	}

	@Test
	public void testGetSupplier() {
		CsvCreator c = ()->()->list;
		AtomicInteger i = new AtomicInteger(0);
		c.getSupplier().get().stream().forEach(ary->{
			assertArrayEquals(ary, list.get(i.getAndIncrement()));
		});
	}

	@Test
	public void testCreate() {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
			CsvCreator c = ()->()->list;
			c.create(out, "UTF-8");
			assertEquals(out.toString(), "A,B,C\r\nあ,い,う\r\n1,,3\r\n");
		}catch(Exception e){
			fail("Exception");
		}
	}
	@Test
	public void testCreateWithDblQuot() {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
			CsvCreator c = ()->()->list;
			c.createWithDblQuot(out, "UTF-8");
			assertEquals(out.toString(), "\"A\",\"B\",\"C\"\r\n\"あ\",\"い\",\"う\"\r\n\"1\",\"\",\"3\"\r\n");
		}catch(Exception e){
			fail("Exception");
		}
	}
	@Test
	public void testCreateBomUTF8() {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
			CsvCreator c = ()->()->list;
			c.createBomUTF8(out);
			byte[] b = out.toByteArray();
			assertEquals(b[0], (byte)0xef);
			assertEquals(b[1], (byte)0xbb);
			assertEquals(b[2], (byte)0xbf);
			byte[] b2 = new byte[b.length-3];
			for(int n=0;n < b2.length; n++) b2[n] = b[n+3];
			assertEquals(new String(b2, StandardCharsets.UTF_8), "A,B,C\r\nあ,い,う\r\n1,,3\r\n");
		}catch(Exception e){
			fail("Exception");
		}
	}
	@Test
	public void testCreateBomUTF8WithDblQuot(){
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
			CsvCreator c = ()->()->list;
			c.createBomUTF8WithDblQuot(out);
			byte[] b = out.toByteArray();
			assertEquals(b[0], (byte)0xef);
			assertEquals(b[1], (byte)0xbb);
			assertEquals(b[2], (byte)0xbf);
			byte[] b2 = new byte[b.length-3];
			for(int n=0;n < b2.length; n++) b2[n] = b[n+3];
			assertEquals(new String(b2, StandardCharsets.UTF_8), "\"A\",\"B\",\"C\"\r\n\"あ\",\"い\",\"う\"\r\n\"1\",\"\",\"3\"\r\n");
		}catch(Exception e){
			fail("Exception");
		}
	}
	@Test
	public void createSjis() {
		try(OutputStream out = new FileOutputStream(testDirectoryPath + "/test.csv")){
			CsvCreator c = ()->()->list;
			c.createWithDblQuot(out, "MS932");
		}catch(Exception e){
			fail("Exception");
		}

		Csvprocess  process = new Csvprocess();
		try(InputStream in = new FileInputStream(testDirectoryPath + "/test.csv")){
			process.read(new InputStreamReader(in, "MS932"), h->{
				assertEquals(h.toString(), "[A, B, C]");
			},(n, l)->{
				if (n==1) {
					assertEquals(l.toString(), "[あ, い, う]");
				}
				if (n==2) {
					assertEquals(l.toString(), "[1, , 3]");
				}
			});
		}catch(ParseException e){
			fail("ParseException");
		}catch(IOException e){
			fail("IOException");
		}catch(ProcessingException e){
			fail("ProcessingException");
		}
	}

}
