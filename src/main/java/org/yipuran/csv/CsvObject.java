/**
 *
 */
package org.yipuran.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.yipuran.csv4j.CSVReader;
import org.yipuran.csv4j.CSVStreamProcessor;
import org.yipuran.csv4j.ProcessingException;

/**
 * CSV→Object読込.
 * <PRE>
 * ヘッダ付きCSVを、ヘッダ行＝生成クラスのsetterフィールド名であることを条件に総称型で指定するクラスで読込みを行う。
 * 総称型で指定するクラスによる、読み出し BiConsumer または、Stream 生成の為に使用する。
 *
 * 総称型で指定するクラスは、以下の条件が必須である。
 * 　・引数なしのコンストラクタでインスタンス生成可能であること。
 * 　・JavaBean 規則による setter を持つこと。
 * 対応するフィールドの型は、byte と char を除くプリミティブ型とそのラッパークラス及び、JSR-310 で提供された
 * LocalDate, LocalDateTime, LocalTime である。
 * CSVの列数 ＞ 読込み対象クラスのフィールドの数、つまりCSVが余計に列が存在してもエラーにはならないが、
 * CSVの列数 ＜ 読込み対象クラスのフィールドの数であることは許されずエラーになる。
 * </PRE>
 * @since 1.2
 */
public class CsvObject<T> extends CSVStreamProcessor{
	private boolean blankIsNull = false;
	private Class<T> cls;
	private List<Class<?>> typelist;
	private List<Method> methodlist;
	private DateTimeFormatter dateFormatter =  DateTimeFormatter.ISO_LOCAL_DATE;//  DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private DateTimeFormatter localdatetimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
	private Function<String, Boolean> booleanReader = s -> Boolean.parseBoolean(s.toLowerCase());

	/**
	 * コンストラクタ.
	 * @param t 可変長引数、指定しても使用はされない
	 */
	@SuppressWarnings("unchecked")
	public CsvObject(T...t) {
		cls = (Class<T>)t.getClass().getComponentType();
	}
	/**
	 * ブランク→null指定.
	 * @param true=ブランク、",," は、null として読み込む。
	 */
	public void setBlanknull(boolean blankIsNull) {
		this.blankIsNull = blankIsNull;
	}
	/**
	 * 日付文字列を LocalDate に変換する場合のフォーマット指定.
	 * <PRE>実行しなければ、DateTimeFormatter.ISO_LOCAL_DATE がフォーマットとして使用される</PRE>
	 * @param formatter
	 */
	public void setLocaldateFormat(DateTimeFormatter formatter) {
		dateFormatter = formatter;
	}
	/**
	 * 日付時刻文字列を LocalDateTime に変換する場合のフォーマット指定.
	 * <PRE>実行しなければ、DateTimeFormatter.ISO_LOCAL_DATE_TIME がフォーマットとして使用される</PRE>
	 * @param formatter
	 */
	public void setLocaldateTimeFormat(DateTimeFormatter formatter) {
		localdatetimeFormatter = formatter;
	}
	/**
	 * 時刻文字列を LocalTime に変換する場合のフォーマット指定.
	 * <PRE>実行しなければ、DateTimeFormatter.ISO_LOCAL_TIME がフォーマットとして使用される</PRE>
	 * @param formatter
	 */
	public void setLocalTimeFormat(DateTimeFormatter formatter) {
		timeFormatter = formatter;
	}
	/**
	 * boolean/Boolean 型読込方法の指定.
	 * @param function
	 */
	public void setBooleanReader(Function<String, Boolean> function) {
		booleanReader = function;
	}

	/**
	 * 総称型指定ＣＳＶ読込み実行.
	 * @param inReader InputStreamReader
	 * @param biconsumer コンテンツ行BiConsumer、CSV行読込みカウント（１始まり）と総称型Ｔのオブジェクト
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public void read(InputStreamReader inReader, BiConsumer<Integer, T> biconsumer) throws IOException, ProcessingException{
		CSVReader reader = new CSVReader(new BufferedReader(inReader), Charset.forName(inReader.getEncoding()), getComment(), blankIsNull);
		try{
			typelist = new ArrayList<>();
			methodlist = new ArrayList<>();
			int lineCount = 0;
			while(true){
				try{
					List<String> fields = reader.readLine();
					if (fields.size()==0) break;
					if (isHasHeader() && lineCount==0){
						String rep = fields.get(0);
						if (BOMfunction.match(rep)) {
							fields.remove(0);
							fields.add(0, BOMfunction.chop(rep));
						}
						for(String f:fields){
							try{
								Class<?> c =  cls.getDeclaredField(f).getType();
								Method m = cls.getDeclaredMethod("set" + f.substring(0, 1).toUpperCase() + f.substring(1), c);
								typelist.add(c);
								methodlist.add(m);
							}catch(NoSuchFieldException | NoSuchMethodException e){
								typelist.add(null);
								methodlist.add(null);
							}
						}
					}else{
						T t = cls.newInstance();
						int i=0;
						for(String f:fields){
							Method m = methodlist.get(i);
							if (m != null){
								setValue(m, t, i, f);
							}
							i++;
						}
						biconsumer.accept(lineCount, t);
					}
				}catch(Exception e){
					throw new ProcessingException(e, reader.getLineNumber());
				}
				lineCount++;
			}
		}finally{
			reader.close();
		}
	}
	/**
	 * 総称型指定ＣＳＶ読込み結果Stream生成.
	 * @param inReader InputStreamReader
	 * @return 総称型 T のStream
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public Stream<T> read(InputStreamReader inReader) throws IOException, ProcessingException{
		Stream.Builder<T> builder = Stream.builder();
		CSVReader reader = new CSVReader(new BufferedReader(inReader), Charset.forName(inReader.getEncoding()), getComment(), blankIsNull);
		try{
			typelist = new ArrayList<>();
			methodlist = new ArrayList<>();
			int lineCount = 0;
			while(true){
				try{
					List<String> fields = reader.readLine();
					if (fields.size()==0) break;
					if (isHasHeader() && lineCount==0){
						String rep = fields.get(0);
						if (BOMfunction.match(rep)) {
							fields.remove(0);
							fields.add(0, BOMfunction.chop(rep));
						}
						for(String f:fields){
							try{
								Class<?> c =  cls.getDeclaredField(f).getType();
								Method m = cls.getDeclaredMethod("set" + f.substring(0, 1).toUpperCase() + f.substring(1), c);
								typelist.add(c);
								methodlist.add(m);
							}catch(NoSuchFieldException | NoSuchMethodException e){
								typelist.add(null);
								methodlist.add(null);
							}
						}
					}else{
						T t = cls.newInstance();
						int i=0;
						for(String f:fields){
							Method m = methodlist.get(i);
							if (m != null){
								setValue(m, t, i, f);
							}
							i++;
						}
						builder.add(t);
					}
				}catch(Exception e){
					throw new ProcessingException(e, reader.getLineNumber());
				}
				lineCount++;
			}
		}finally{
			reader.close();
		}
		return builder.build();
	}
	/**
	 * 総称型指定ＣＳＶ読込み実行（コンバーター指定）.
	 * @param inReader InputStreamReader
	 * @param converter CSV１行分の文字列リストから、総称型Ｔを生成取得するコンバーター
	 * @param biconsumer コンテンツ行BiConsumer、CSV行読込みカウント（１始まり）と総称型Ｔのオブジェクト
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public void read(InputStreamReader inReader, Function<List<String>, T> converter,  BiConsumer<Integer, T> biconsumer) throws IOException, ProcessingException{
		CSVReader reader = new CSVReader(new BufferedReader(inReader), Charset.forName(inReader.getEncoding()), getComment(), blankIsNull);
		try{
			methodlist = new ArrayList<>();
			int lineCount = 0;
			while(true){
				try{
					List<String> fields = reader.readLine();
					if (fields.size()==0) break;
					if (!isHasHeader() || lineCount > 0){
						biconsumer.accept(lineCount, converter.apply(fields));
					}
				}catch(Exception e){
					throw new ProcessingException(e, reader.getLineNumber());
				}
				lineCount++;
			}
		}finally{
			reader.close();
		}
	}
	/**
	 * 総称型指定ＣＳＶ読込み結果Stream生成（コンバーター指定）.
	 * @param inReader InputStreamReader
	 * @param converter CSV１行分の文字列リストから、総称型Ｔを生成取得するコンバーター
	 * @return 総称型 T のStream
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public Stream<T> read(InputStreamReader inReader, Function<List<String>, T> converter) throws IOException, ProcessingException{
		Stream.Builder<T> builder = Stream.builder();
		CSVReader reader = new CSVReader(new BufferedReader(inReader), Charset.forName(inReader.getEncoding()), getComment(), blankIsNull);
		try{
			int lineCount = 0;
			while(true){
				try{
					List<String> fields = reader.readLine();
					if (fields.size()==0) break;
					if (!isHasHeader() || lineCount > 0){
						builder.add(converter.apply(fields));
					}
				}catch(Exception e){
					throw new ProcessingException(e, reader.getLineNumber());
				}
				lineCount++;
			}
		}finally{
			reader.close();
		}
		return builder.build();
	}

	private void setValue(Method m, Object obj, int n, String str) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException {
		try{
			if (typelist.get(n).isPrimitive()) {
				if (blankIsNull && str==null) return;
				Class<?> c = typelist.get(n);
				if (c.equals(int.class)) {
					m.invoke(obj, Integer.parseInt(str));
				}else if(c.equals(long.class)) {
					m.invoke(obj, Long.parseLong(str));
				}else if(c.equals(double.class)) {
					m.invoke(obj, Double.parseDouble(str));
				}else if(c.equals(short.class)) {
					m.invoke(obj, Short.parseShort(str));
				}else if(c.equals(float.class)) {
					m.invoke(obj, Float.parseFloat(str));
				}else if(c.equals(boolean.class)) {
					m.invoke(obj, booleanReader.apply(str));
				}
			}else{
				if (typelist.get(n).equals(String.class)) {
					m.invoke(obj, str);
				}else if(typelist.get(n).equals(Boolean.class)) {
					m.invoke(obj, booleanReader.apply(str));
				}else if(typelist.get(n).equals(LocalDate.class)) {
					m.invoke(obj, LocalDate.parse(str, dateFormatter));
				}else if(typelist.get(n).equals(LocalDateTime.class)) {
					m.invoke(obj, LocalDateTime.parse(str, localdatetimeFormatter));
				}else if(typelist.get(n).equals(LocalTime.class)) {
					m.invoke(obj, LocalTime.parse(str, timeFormatter));
				}else{
					Method getter = typelist.get(n).getDeclaredMethod("valueOf", String.class);
					m.invoke(obj, getter.invoke(null, str));
				}
			}
		}catch(InvocationTargetException e){
		}
	}
}
