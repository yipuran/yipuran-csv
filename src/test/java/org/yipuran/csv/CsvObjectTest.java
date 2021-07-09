package org.yipuran.csv;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.yipuran.csv.data.Foo;
import org.yipuran.csv4j.ProcessingException;

public class CsvObjectTest {
	private StringBuilder sb;
	@Before
	public void setUp() throws Exception{
		sb = new StringBuilder();
		sb.append("i,ii,l,ll,d,dd,f,ff,t,tt,flg,flgB,info,date,datetime,time");
		sb.append("\n");
	}
	@Test
	public void testReadConsumer1(){
		sb.append("10,100,2,20,3.14,30.14,0.02,0.021,7,70,true,false,あ,2021-07-09,2021-07-09 08:14:51,17:24:22");
		sb.append("\n");
		sb.append("11,,21,,3.14,30.14,0.02,,7,,False,True,い,2021-07-08,2021-07-06 16:21:06,05:08:47");
		String data = sb.toString();

		CsvObject<Foo> co = new CsvObject<>();
		co.setLocaldateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		try(ByteArrayInputStream bin = new ByteArrayInputStream(data.getBytes());InputStreamReader reader = new InputStreamReader(bin)){
			co.read(reader, (i, f)->{
				if (i==1) {
assertEquals("(i=10,ii=100,l=2,ll=20,d=3.14,dd=30.14,f=0.02,ff=0.021,t=7,tt=70,flg=true,flgB=false,info=あ,date=2021-07-09,datetime=2021-07-09T08:14:51,time=17:24:22)"
, f.toString());
				}else if(i==2){
assertEquals("(i=11,ii=null,l=21,ll=null,d=3.14,dd=30.14,f=0.02,ff=null,t=7,tt=null,flg=false,flgB=true,info=い,date=2021-07-08,datetime=2021-07-06T16:21:06,time=05:08:47)"
, f.toString());
				}
			});
		}catch(IOException e){
			fail("IOException "+e.getMessage());
		}catch(ProcessingException e){
			fail("ProcessingException "+e.getMessage());
		}
	}
	@Test
	public void testReadConsumer2(){
		sb.append("10,100,2,20,3.14,30.14,0.02,0.021,7,70,0,1,あ,2021/07/09,2021/07/09 08:14:51,17:24");
		sb.append("\n");
		sb.append("11,,21,,3.14,30.14,0.02,,7,,1,0,い,2021/07/08,2021/07/06 16:21:06,05:08");
		String data = sb.toString();

		CsvObject<Foo> co = new CsvObject<>();
		co.setBlanknull(true);
		co.setLocaldateFormat(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		co.setLocaldateTimeFormat(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
		co.setLocalTimeFormat(DateTimeFormatter.ofPattern("HH:mm"));

		co.setBooleanReader(s->s.equals("0"));

		try(ByteArrayInputStream bin = new ByteArrayInputStream(data.getBytes());InputStreamReader reader = new InputStreamReader(bin)){
			co.read(reader, (i, f)->{
				if (i==1) {
assertEquals("(i=10,ii=100,l=2,ll=20,d=3.14,dd=30.14,f=0.02,ff=0.021,t=7,tt=70,flg=true,flgB=false,info=あ,date=2021-07-09,datetime=2021-07-09T08:14:51,time=17:24)"
, f.toString());
				}else if(i==2){
assertEquals("(i=11,ii=null,l=21,ll=null,d=3.14,dd=30.14,f=0.02,ff=null,t=7,tt=null,flg=false,flgB=true,info=い,date=2021-07-08,datetime=2021-07-06T16:21:06,time=05:08)"
, f.toString());
				}
			});
		}catch(IOException e){
			fail("IOException "+e.getMessage());
		}catch(ProcessingException e){
			fail("ProcessingException "+e.getMessage());
		}
	}
	@Test
	public void testReadStream(){
		sb.append("10,100,2,20,3.14,30.14,0.02,0.021,7,70,true,false,あ,2021-07-09,2021-07-09 08:14:51,17:24:22");
		sb.append("\n");
		sb.append("11,,21,,3.14,,0.02,,7,,False,True,い,2021-07-08,2021-07-06 16:21:06,05:08:47");
		String data = sb.toString();

		List<Foo> expected = new ArrayList<>();
		Foo f1 = new Foo();
		f1.setI(10);
		f1.setIi(100);
		f1.setL(2);
		f1.setLl(20L);
		f1.setD(3.14);
		f1.setDd(30.14);
		f1.setF(0.02f);
		f1.setFf(0.021f);
		f1.setT((short)7);
		f1.setTt((short)70);
		f1.setFlg(true);
		f1.setFlgB(false);
		f1.setInfo("あ");
		f1.setDate(LocalDate.of(2021, 7, 9));
		f1.setDatetime(LocalDateTime.of(2021, 7, 9, 8, 14, 51));
		f1.setTime(LocalTime.of(17, 24, 22));
		expected.add(f1);
		Foo f2 = new Foo();
		f2.setI(11);
		f2.setL(21);
		f2.setD(3.14);
		f2.setF(0.02f);
		f2.setT((short)7);
		f2.setFlg(false);
		f2.setFlgB(true);
		f2.setInfo("い");
		f2.setDate(LocalDate.of(2021, 7, 8));
		f2.setDatetime(LocalDateTime.of(2021, 7, 6, 16, 21, 06));
		f2.setTime(LocalTime.of(5, 8, 47));
		expected.add(f2);

		CsvObject<Foo> co = new CsvObject<>();
		co.setLocaldateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		try(ByteArrayInputStream bin = new ByteArrayInputStream(data.getBytes());InputStreamReader reader = new InputStreamReader(bin)){
			List<Foo> results = co.read(reader).collect(Collectors.toList());
			assertEquals(expected.toString(), results.toString());
		}catch(IOException e){
			fail("IOException "+e.getMessage());
		}catch(ProcessingException e){
			fail("ProcessingException "+e.getMessage());
		}
	}
	Function<List<String>, Foo> converter = list->{
		Foo f = new Foo();
		f.setI(Optional.ofNullable(list.get(0)).filter(e->!e.equals("")).map(Integer::valueOf).orElse(null));
		f.setIi(Optional.ofNullable(list.get(1)).filter(e->!e.equals("")).map(Integer::valueOf).orElse(null));
		f.setL(Optional.ofNullable(list.get(2)).filter(e->!e.equals("")).map(Long::valueOf).orElse(null));
		f.setLl(Optional.ofNullable(list.get(3)).filter(e->!e.equals("")).map(Long::valueOf).orElse(null));
		f.setD(Optional.ofNullable(list.get(4)).filter(e->!e.equals("")).map(Double::valueOf).orElse(null));
		f.setDd(Optional.ofNullable(list.get(5)).filter(e->!e.equals("")).map(Double::valueOf).orElse(null));
		f.setF(Optional.ofNullable(list.get(6)).filter(e->!e.equals("")).map(Float::valueOf).orElse(null));
		f.setFf(Optional.ofNullable(list.get(7)).filter(e->!e.equals("")).map(Float::valueOf).orElse(null));
		f.setT(Optional.ofNullable(list.get(8)).filter(e->!e.equals("")).map(Short::valueOf).orElse(null));
		f.setTt(Optional.ofNullable(list.get(9)).filter(e->!e.equals("")).map(Short::valueOf).orElse(null));
		f.setFlg(Optional.ofNullable(list.get(10)).filter(e->!e.equals("")).map(Boolean::valueOf).orElse(false));
		f.setFlgB(Optional.ofNullable(list.get(11)).filter(e->!e.equals("")).map(Boolean::valueOf).orElse(null));
		f.setInfo(list.get(12));
		f.setDate(Optional.ofNullable(list.get(13)).filter(e->!e.equals("")).map(LocalDate::parse).orElse(null));
		f.setDatetime(Optional.ofNullable(list.get(14)).filter(e->!e.equals("")).map(LocalDateTime::parse).orElse(null));
		f.setTime(Optional.ofNullable(list.get(15)).filter(e->!e.equals("")).map(LocalTime::parse).orElse(null));
		return f;
	};
	@Test
	public void testReadConsumer3(){
		sb.append("10,100,2,20,3.14,30.14,0.02,0.021,7,70,true,false,あ,2021-07-09,2021-07-09T08:14:51,17:24:22");
		sb.append("\n");
		sb.append("11,,21,,3.14,30.14,0.02,,7,,False,True,い,2021-07-08,2021-07-06T16:21:06,05:08:47");
		String data = sb.toString();

		CsvObject<Foo> co = new CsvObject<>();
		try(ByteArrayInputStream bin = new ByteArrayInputStream(data.getBytes());InputStreamReader reader = new InputStreamReader(bin)){
			co.read(reader, converter, (i, f)->{
				if (i==1) {
assertEquals("(i=10,ii=100,l=2,ll=20,d=3.14,dd=30.14,f=0.02,ff=0.021,t=7,tt=70,flg=true,flgB=false,info=あ,date=2021-07-09,datetime=2021-07-09T08:14:51,time=17:24:22)"
, f.toString());
				}else if(i==2){
assertEquals("(i=11,ii=null,l=21,ll=null,d=3.14,dd=30.14,f=0.02,ff=null,t=7,tt=null,flg=false,flgB=true,info=い,date=2021-07-08,datetime=2021-07-06T16:21:06,time=05:08:47)"
, f.toString());
				}
			});
		}catch(IOException e){
			fail("IOException "+e.getMessage());
		}catch(ProcessingException e){
			fail("ProcessingException "+e.getMessage());
		}
	}
	@Test
	public void testReadStream2(){
		sb.append("10,100,2,20,3.14,30.14,0.02,0.021,7,70,true,false,あ,2021-07-09,2021-07-09T08:14:51,17:24:22");
		sb.append("\n");
		sb.append("11,,21,,3.14,,0.02,,7,,False,True,い,2021-07-08,2021-07-06T16:21:06,05:08:47");
		String data = sb.toString();

		List<Foo> expected = new ArrayList<>();
		Foo f1 = new Foo();
		f1.setI(10);
		f1.setIi(100);
		f1.setL(2);
		f1.setLl(20L);
		f1.setD(3.14);
		f1.setDd(30.14);
		f1.setF(0.02f);
		f1.setFf(0.021f);
		f1.setT((short)7);
		f1.setTt((short)70);
		f1.setFlg(true);
		f1.setFlgB(false);
		f1.setInfo("あ");
		f1.setDate(LocalDate.of(2021, 7, 9));
		f1.setDatetime(LocalDateTime.of(2021, 7, 9, 8, 14, 51));
		f1.setTime(LocalTime.of(17, 24, 22));
		expected.add(f1);
		Foo f2 = new Foo();
		f2.setI(11);
		f2.setL(21);
		f2.setD(3.14);
		f2.setF(0.02f);
		f2.setT((short)7);
		f2.setFlg(false);
		f2.setFlgB(true);
		f2.setInfo("い");
		f2.setDate(LocalDate.of(2021, 7, 8));
		f2.setDatetime(LocalDateTime.of(2021, 7, 6, 16, 21, 06));
		f2.setTime(LocalTime.of(5, 8, 47));
		expected.add(f2);

		CsvObject<Foo> co = new CsvObject<>();
		try(ByteArrayInputStream bin = new ByteArrayInputStream(data.getBytes());InputStreamReader reader = new InputStreamReader(bin)){
			List<Foo> results = co.read(reader, converter).collect(Collectors.toList());
			assertEquals(expected.toString(), results.toString());
		}catch(IOException e){
			fail("IOException "+e.getMessage());
		}catch(ProcessingException e){
			fail("ProcessingException "+e.getMessage());
		}
	}

}
