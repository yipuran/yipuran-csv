package org.yipuran.csv;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
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
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.yipuran.csv4j.ParseException;
import org.yipuran.csv4j.ProcessingException;

/**
 * CsvprocessTest.java
 */
public class CsvprocessTest{
	Csvprocess  process;
	String testDirectoryPath;

	@Before
	public void setUp() throws Exception{
		process = new Csvprocess();
		File file = new File(getClass().getClassLoader().getResource("").toURI());
		testDirectoryPath = file.getAbsolutePath();
	}

	@Test
	public void testReadNoheader(){
		String str = "A,B,C\r\nあ,い,う\r\n1,,3";
		try(ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
			InputStreamReader reader = new InputStreamReader(bin)){
			process.readNoheader(reader, (n, l)->{
				if (n==0) {
					assertEquals(l.toString(), "[A, B, C]");
				}
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
			fail("ParseException");
		}catch(ProcessingException e){
			fail("ParseException");
		}
	}
	@Test
	public void testReadNoheaderDblQuot(){
		String str = "\"A\",\"B\",\"C\"\r\n\"あ\",い,\"う\"\r\n\"1\",,\"3\"";
		try(ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
			InputStreamReader reader = new InputStreamReader(bin)){
			process.readNoheader(reader, (n, l)->{
				if (n==0) {
					assertEquals(l.toString(), "[A, B, C]");
				}
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
	@Test
	public void testReadHeadAndList(){
		String str = "A,B,C\r\nあ,い,う\r\n";
		try(ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
			InputStreamReader reader = new InputStreamReader(bin)){
			process.read(reader, h->{
				assertEquals(h.toString(), "[A, B, C]");
			},(n, l)->{
				if (n==0) {
					assertEquals(l.toString(), "[あ, い, う]");
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

	@Test
	public void testReadHeadAndMap(){
		String str = "A,B,C\r\nあ,い,う\r\n";
		try(ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
			InputStreamReader reader = new InputStreamReader(bin)){
			process.read(reader, (n, m)->{
				assertEquals(n.toString(), "1");
				assertEquals(m.entrySet().stream().map(e->e.getKey()).collect(Collectors.joining(",")), "A,B,C");
				assertEquals(m.entrySet().stream().map(e->e.getValue()).collect(Collectors.joining(",")), "あ,い,う");
			});
		}catch(ParseException e){
			fail("ParseException");
		}catch(IOException e){
			fail("IOException");
		}catch(ProcessingException e){
			fail("ProcessingException");
		}
	}

	@Test
	public void testBomReadHeadAndList(){
		List<String[]> list;
		list = new ArrayList<>();
		list.add(new String[]{"A", "B", "C"});
		list.add(new String[]{"あ", "い", "う"});

		CsvCreator c = ()->()->list;
		try(OutputStream out = new FileOutputStream(testDirectoryPath + "/test8bom.csv")){
			c.createBomUTF8WithDblQuot(out);
		}catch(Exception e){
			fail("Exception");
		}

		try(InputStream in = new FileInputStream(testDirectoryPath + "/test8bom.csv")){
			process.read(new InputStreamReader(in, StandardCharsets.UTF_8), h->{
				assertEquals(h.toString(), "[A, B, C]");
			},(n, l)->{
				assertEquals(n.toString(), "1");
				assertEquals(l.toString(), "[あ, い, う]");
			});
		}catch(ParseException e){
			fail("ParseException");
		}catch(IOException e){
			fail("IOException");
		}catch(ProcessingException e){
			fail("ProcessingException");
		}
	}
	@Test
	public void testBomReadMap(){
		List<String[]> list;
		list = new ArrayList<>();
		list.add(new String[]{"A", "B", "C"});
		list.add(new String[]{"あ", "い", "う"});

		CsvCreator c = ()->()->list;
		try(OutputStream out = new FileOutputStream(testDirectoryPath + "/test8bom.csv")){
			c.createBomUTF8WithDblQuot(out);
		}catch(Exception e){
			fail("Exception");
		}
		try(InputStream in = new FileInputStream(testDirectoryPath + "/test8bom.csv")){
			process.read(new InputStreamReader(in, StandardCharsets.UTF_8), (n, m)->{
				assertEquals(n.toString(), "1");
				String v = m.entrySet().stream().sorted((a, b)->a.getKey().compareTo(b.getKey()))
								.map(e->"{" + e.getKey() + ":" + e.getValue() + "}").collect(Collectors.joining());
				assertEquals(v, "{A:あ}{B:い}{C:う}");
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
