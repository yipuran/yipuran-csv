package org.yipuran.csv;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

/**
 * CsvwriteArrayStreamTest.java
 * ＣＳＶ生成インターフェース（配列 Stream）.
 * <PRE>
 * List&lt;String&gt; list = new ArrayList&lt;&gt;();
 *    // list に格納
 * CsvwriteListStream c = ()->()->list.stream();
 *    // 宣言
 *
 * try(OutputStream out = new FileOutputStream("sample.csv")){
 *    c.create(out, "MS932");
 * }catch(Exception e){
 *    e.printStackTrace();
 * }
 *
 * BOM付きUTF-8 の場合、
 * try(OutputStream out = new FileOutputStream("sample.csv")){
 *    c.createBomUTF8(out);
 * }catch(Exception e){
 *    e.printStackTrace();
 * }
 *
 * BOM付きUTF-8ダブルクォート括り の場合、
 * try(OutputStream out = new FileOutputStream("sample.csv")){
 *    c.createBomUTF8WithDblQuot(out);
 * }catch(Exception e){
 *    e.printStackTrace();
 * }
 * </PRE>
 */
public class CsvwriteArrayStreamTest{
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
	public void testGetSupplier(){
		CsvwriteArrayStream c = ()->()->list.stream();
		AtomicInteger i = new AtomicInteger(0);
		c.getSupplier().get().forEach(t->{
			String[] origins = list.get(i.getAndIncrement());
			for(int n=0;n < t.length; n++) {
				assertEquals(t[n], origins[n]);
			}
		});
	}

	@Test
	public void testCreate(){
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
			CsvwriteArrayStream c = ()->()->list.stream();
			c.create(out, "UTF-8");
			assertEquals(out.toString(), "A,B,C\r\nあ,い,う\r\n1,,3\r\n");
		}catch(Exception e){
			fail("Exception");
		}
	}

	@Test
	public void testCreateWithDblQuot(){
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
			CsvwriteArrayStream c = ()->()->list.stream();
			c.createWithDblQuot(out, "UTF-8");
			assertEquals(out.toString(), "\"A\",\"B\",\"C\"\r\n\"あ\",\"い\",\"う\"\r\n\"1\",\"\",\"3\"\r\n");
		}catch(Exception e){
			fail("Exception");
		}
	}

	@Test
	public void testCreateBomUTF8(){
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
			CsvwriteArrayStream c = ()->()->list.stream();
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
			CsvwriteArrayStream c = ()->()->list.stream();
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

}
