package org.yipuran.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.yipuran.csv4j.CSVReader;
import org.yipuran.csv4j.CSVStreamProcessor;
import org.yipuran.csv4j.ParseException;
import org.yipuran.csv4j.ProcessingException;

/**
 * CSV読込みProcessor.
 * ヘッダ有り読込みとヘッダ無し読込みを提供する。
 * <PRE>
 * ヘッダ有り読込み、(for Windows SJIS CSV)
 * Csvprocess process = new Csvprocess();
 * process.read(new InputStreamReader(in, "MS932")
 * , h->{
 *    // ヘッダ１列目
 *    String value = h.get(0);
 * }, (n, p)->{
 *     // n = CSV行カウント（１始まり）、p = CSV １行の Line&lt;String&gt;
 *     // １列目
 *     String value =  p.get(0);
 * });
 *
 *
 * ヘッダ無し読込み、(for Windows SJIS CSV)
 * Csvprocess process = new Csvprocess();
 * process.readNoheader(new InputStreamReader(in, "MS932"), (i, p)->{
 *      // i = 行index（０始まり）
 *      // p = １行の Line&lt;String&gt;
 *      p.stream().forEach(e->{
 *         System.out.print("[" + e + "]");
 *      });
 * });
 *
 * ヘッダ有りＣＳＶ読込み実行（Map形式読込み）.
 * Csvprocess process = new Csvprocess();
 * process.readNoheader(new InputStreamReader(in, "MS932"), (i, map)->{
 *      // i = 行index（１始まり）
 *      // map : key=ヘッダの値、value=対応するコンテンツ行の値
 *      map.entrySet().stream().forEach(e->{
 *          String key = e.getKey();
 *          String value = e.getValue();
 *      });
 * });
 * </PRE>
 */
public class Csvprocess extends CSVStreamProcessor{
	private boolean blankIsNull = false;
	/**
	 * デフォルトコンストラクタ.
	 * ブランク、",," は、null にしないで、空文字として読み込む。
	 */
	public Csvprocess(){}

	/**
	 * ブランク→null指定コンストラクタ.
	 * @param true=ブランク、",," は、null として読み込む。
	 * @since 4.18
	 */
	public Csvprocess(boolean blankIsNull) {
		this.blankIsNull = blankIsNull;
	}

	/**
	 * ヘッダ有りＣＳＶ読込み実行.
	 * @param inReader InputStreamReader
	 * @param header ヘッダ行 Consumer
	 * @param processor コンテンツ行BiConsumer、CSV行読込みカウント（１始まり）とCSV文字列のList
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public void read(InputStreamReader inReader, Consumer<List<String>> header, BiConsumer<Integer, List<String>> processor)
	throws IOException, ProcessingException{
		CSVReader reader = new CSVReader(new BufferedReader(inReader), getComment(), blankIsNull);
		try{
			int lineCount = 0;
			while(true){
				List<String> fields = reader.readLine();
				if (fields.size()==0) break;
				try{
					if (isHasHeader() && lineCount==0){
						String rep = fields.get(0);
						if (BOMfunction.match(rep)) {
							fields.remove(0);
							fields.add(0, BOMfunction.chop(rep));
						}
						header.accept(fields);
					}else{
						processor.accept(lineCount, fields);
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
	 * ヘッダ無しＣＳＶ読込み実行.
	 * @param inReader InputStreamReader
	 * @param processor BiConsumer 行のindexとCSV文字列のList
	 * @throws IOException
	 * @throws ProcessingException
	 * @throws ParseException
	 */
	public void readNoheader(InputStreamReader inReader, BiConsumer<Integer, List<String>> processor) throws IOException, ProcessingException, ParseException{
		CSVReader reader = new CSVReader(new BufferedReader(inReader), getComment(), blankIsNull);
		try{
			int lineIndex = 0;
			while(true){
				List<String> fields = reader.readLine();
				if (fields.size()==0) break;
				if (lineIndex==0){
					String rep = fields.get(0);
					if (BOMfunction.match(rep)) {
						fields.remove(0);
						fields.add(0, BOMfunction.chop(rep));
					}
				}
				try{
					processor.accept(lineIndex, fields);
				}catch(Exception e){
					throw new ProcessingException(e, reader.getLineNumber());
				}
				lineIndex++;
			}
		}finally{
			reader.close();
		}
	}

	/**
	 * ヘッダ有りＣＳＶ読込み実行（Map形式読込み）.
	 * <PRE>
	 * ヘッダ行列をキーとして読込み結果をMapで実行
	 * </PRE>
	 * @param inReader InputStreamReader
	 * @param processor コンテンツ行BiConsumer、CSV行読込みカウント（１始まり）とヘッダのキーに対するコンテンツ行の値のMap
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public void read(InputStreamReader inReader, BiConsumer<Integer, Map<String, String>> processor) throws IOException, ProcessingException{
		CSVReader reader = new CSVReader(new BufferedReader(inReader), getComment(), blankIsNull);
		try{
			Map<Integer, String> headerMap = new HashMap<>();
			int lineCount = 0;
			while(true){
				List<String> fields = reader.readLine();
				if (fields.size()==0) break;
				try{
					if (isHasHeader() && lineCount==0){
						String rep = fields.get(0);
						if (BOMfunction.match(rep)) {
							fields.remove(0);
							fields.add(0, BOMfunction.chop(rep));
						}
						int i = 0;
						for(String key:fields){
							headerMap.put(i, key);
							i++;
						}
					}else{
						processor.accept(lineCount,
							Stream.iterate(0, i->i+1).limit(fields.size())
							.collect(HashMap<String, String>::new, (r, t)->r.put(headerMap.get(t), fields.get(t)), (r, t)->{})
						);
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
}
