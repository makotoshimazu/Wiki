package wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

//TODO コマンドラインで始点と終点を指定する
//プログラムの実行方法 $ java -Xss16m wiki/wiki (これでスタックサイズを指定)

public class wiki {
	final static int LEN = 1483277;
	final static int MAX_DEPTH = 30;
	static page pages[] = new page[LEN];

	public static void main(String[] args) {
		readPage();
		readLink();
		int startIndex = getIndexOf("六本木ヒルズ森タワー");
		int targetIndex = getIndexOf("スクランブル交差点");
		isNotFound(startIndex);
		isNotFound(targetIndex);
		dfs(startIndex, targetIndex, -1);
		System.out.printf("not found\n");
	}

	static void readPage() {
		int index = 0;
		try {
			//pagesファイルを指定
			File file = new File("./pages.txt");
			//BufferReaderは1行ずつ読み込む
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			//nullまで読み込む
			String line;
			StringTokenizer tok;
			while ((line = bufferedReader.readLine()) != null) {
				/*
				トークンに文字を分割したい(文字列をトークンに分割することができる.
				トークンに分けるとき，区切り文字を使用する.
				デフォルトでは，空白，タブ，改行，用紙送り文字
				*/
				tok = new StringTokenizer(line);
				while (tok.hasMoreTokens()) {
					pages[index++] = new page(Integer.valueOf(tok.nextToken()), tok.nextToken());
				}

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
			File file = new File("./pageArray.txt");
			BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(file));
			for (page p : pages) {
				bufferWriter.write(p.title);
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

	static void readLink() {
		try {
			//pagesファイルを指定
			File file = new File("./links.txt");
			//BufferReaderは1行ずつ読み込む
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			//nullまで読み込む
			String line;
			StringTokenizer tok;
			while ((line = bufferedReader.readLine()) != null) {
				tok = new StringTokenizer(line);
				while (tok.hasMoreTokens()) {
					pages[Integer.valueOf(tok.nextToken())].reference.add(Integer.valueOf(tok.nextToken()));
				}
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
			File file = new File("./linkList.txt");
			BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(file));
			for (int p : pages[0].reference) {
				bufferWriter.write(pages[p].title);
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

	static int getIndexOf(String word) {
		for (page p : pages) {
			if (p.title.equals(word))
				return p.index;
		}
		return -1;
	}

	static void isNotFound(int index) {
		if (index < 0) {
			System.out.println("Not Found!");
			System.exit(1);
		}
	}

	//TODO 再起の深さを計算する、深さがリミットを超えたらそれ以上再起をしない
	static void dfs(int start, int target, int depth) {
		depth++;
		//visitで訪問済みかどうか管理する
		pages[start].visited = true;
		Iterator<Integer> itr = pages[start].reference.iterator();
		while (itr.hasNext()) {
			int num = itr.next();
			if (num == target) {
				System.out.printf("found! %s --> %s \n", pages[start].title, pages[num].title);
				System.exit(0);
				break;
			} else {
				if (pages[num].visited == false && depth < MAX_DEPTH) {
					System.out.printf("%s --> %s \n", pages[start].title, pages[num].title);
					dfs(num, target, depth);
				}
			}
		}
	}
}