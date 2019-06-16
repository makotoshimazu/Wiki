package wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class wiki {
	final static int LEN = 1483277;
	static String pages[] = new String[LEN];

	public static void main(String[] args) {
		readPage();
	}

	static void readPage() {
		int index = 0;
		try {
			//pagesファイルを指定
			File file = new File("src/pages.txt");
			//BufferReaderは1行ずつ読み込む
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			//nullまで読み込む
			String line, title = null;
			StringTokenizer tok;
			while ((line = bufferedReader.readLine()) != null) {
				/*
				トークンに文字を分割したい(文字列をトークンに分割することができる.
				トークンに分けるとき，区切り文字を使用する.
				デフォルトでは，空白，タブ，改行，用紙送り文字
				*/
				tok = new StringTokenizer(line);
				while (tok.hasMoreTokens())
					//最後のトークン(=pageのタイトルのみほしい)
					title = tok.nextToken();
				pages[index++] = title;
			}
			//リソースの開放
			bufferedReader.close();
			//ファイルが見つからなければエラー
		} catch (FileNotFoundException e) {
			System.out.println(e);
			//readLineでエラーが出た時
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		//debug用にoutputStreamに書き込み
		try {
			File file = new File("src/pageArray.txt");
			BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(file));
			for (String title : pages) {
				bufferWriter.write(title);
				//改行コードをOS似合わせて自動で判断して出力
				bufferWriter.newLine();
			}
			bufferWriter.close();
			//ファイルが見つからなければエラー
		} catch (FileNotFoundException e) {
			System.out.println(e);
			//writeでエラーが出た時
		} catch (IOException e) {
			e.printStackTrace();
		}
		 */
	}
}
