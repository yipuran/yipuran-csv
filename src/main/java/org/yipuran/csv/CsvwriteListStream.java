package org.yipuran.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yipuran.csv4j.CSVWriter;

/**
 * ＣＳＶ生成インターフェース（List Stream）.
 * <PRE>
 * List＜List＜String＞＞ list = new ArrayList＜＞();
 *    // list に格納
 * CsvwriteListStream c = ()->()->list.stream();
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
public interface CsvwriteListStream{

	Supplier<Stream<List<String>>> getSupplier();

	/**
	 * ＣＳＶ出力実行.
	 * @param out OutputStream
	 * @param charName 文字セット名
	 */
	default public void create(OutputStream out, String charName){
		try(OutputStreamWriter writer = new OutputStreamWriter(out, charName)){
			CSVWriter csvWriter = new CSVWriter(writer);
			getSupplier().get().map(list->list.stream().map(s->s==null ? "" : s).collect(Collectors.toList()))
			.forEach(t->{
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
			getSupplier().get().map(list->list.stream().map(s->s==null ? "" : s).collect(Collectors.toList()))
			.forEach(t->{
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
			getSupplier().get().map(list->list.stream().map(s->s==null ? "" : s).collect(Collectors.toList()))
			.forEach(t->{
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
			getSupplier().get().map(list->list.stream().map(s->s==null ? "" : s).collect(Collectors.toList()))
			.forEach(t->{
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
	 * Collection→ダブルクォート括り１行作成.
	 * @param list １行生成する文字列 List
	 * @return ダブルクォート括り１行
	 */
	default public String csvline(List<String> list){
		return "\"" + list.stream().map(s->s.replaceAll("\"","\"\"")).collect(Collectors.joining("\",\"")) + "\"";
	}
}
