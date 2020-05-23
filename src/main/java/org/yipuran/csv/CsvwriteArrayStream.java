package org.yipuran.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yipuran.csv4j.CSVWriter;

/**
 * ＣＳＶ生成インターフェース（配列 Stream）.
 * <PRE>
 * List＜String[]＞ list = new ArrayList＜＞();
 *    // list に格納
 * CsvwriteArrayStream c = ()->()->list.stream();
 *    // 宣言
 *
 * 文字列セット指定の場合
 * try(OutputStream out = new FileOutputStream("sample.csv")){
 *    c.create(out, "MS932");
 * }catch(Exception e){
 *    e.printStackTrace();
 * }
 *
 * 文字列セット指定ダブルクォート括りの場合
 * try(OutputStream out = new FileOutputStream("sample.csv")){
 *    c.createWithDblQuot(out, "MS932");
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
@FunctionalInterface
public interface CsvwriteArrayStream{

	/**
	 * ＣＳＶ出力行数分の String配列の Stream を返す Supplier を取得する.
	 * @return Supplier＜Stream＜String[]＞＞
	 */
	Supplier<Stream<String[]>> getSupplier();

	/**
	 * ＣＳＶ出力実行.
	 * @param out OutputStream
	 * @param charName 文字セット名
	 */
	default public void create(OutputStream out, String charName){
		try(OutputStreamWriter writer = new OutputStreamWriter(out, charName)){
			CSVWriter csvWriter = new CSVWriter(writer);
			getSupplier().get().map(s->{
				for(int i=0;i < s.length;i++){
					s[i] = s[i]==null ? "" : s[i];
				}
				return s;
			}).forEach(t->{
				try{
					csvWriter.writeLine(t);
				}catch(IOException ex){
					throw new RuntimeException(ex);
				}
			});
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	/**
	 * ダブルクォート括りＣＳＶ出力実行.
	 * @param out OutputStream
	 * @param charName 文字セット名
	 */
	default public void createWithDblQuot(OutputStream out, String charName){
		String lineSeparator = System.getProperty("line.separator");
		try(OutputStreamWriter writer = new OutputStreamWriter(out, charName)){
			getSupplier().get().map(s->{
				for(int i=0;i < s.length;i++){
					s[i] = s[i]==null ? "" : s[i];
				}
				return s;
			}).forEach(t->{
				try{
					writer.write(csvline(t));
					writer.write(lineSeparator);
				}catch(IOException ex){
					throw new RuntimeException(ex);
				}
			});
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	/**
	 * BOM付きUTF-8 ＣＳＶ出力実行
	 * @param out OutputStream
	 */
	default public void createBomUTF8(OutputStream out){
		try(OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)){
			BOMfunction.push(out);
			CSVWriter csvWriter = new CSVWriter(writer);
			getSupplier().get().map(s->{
				for(int i=0;i < s.length;i++){
					s[i] = s[i]==null ? "" : s[i];
				}
				return s;
			}).forEach(t->{
				try{
					csvWriter.writeLine(t);
				}catch(IOException ex){
					throw new RuntimeException(ex);
				}
			});
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	/**
	 * BOM付きUTF-8ダブルクォート括り ＣＳＶ出力実行
	 * @param out OutputStream
	 */
	default public void createBomUTF8WithDblQuot(OutputStream out){
		String lineSeparator = System.getProperty("line.separator");
		try(OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)){
			BOMfunction.push(out);
			getSupplier().get().map(s->{
				for(int i=0;i < s.length;i++){
					s[i] = s[i]==null ? "" : s[i];
				}
				return s;
			}).forEach(t->{
				try{
					writer.write(csvline(t));
					writer.write(lineSeparator);
				}catch(IOException ex){
					throw new RuntimeException(ex);
				}
			});
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	/**
	 * ダブルクォート括り１行作成.
	 * @param ary １行生成する配列
	 * @return ダブルクォート括り１行
	 */
	default public String csvline(String[] ary){
		return "\"" + Arrays.stream(ary).map(s->s.replaceAll("\"","\"\"")).collect(Collectors.joining("\",\"")) + "\"";
	}
}
