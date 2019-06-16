package wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

public class wiki {
	final static int LEN = 1483277;
	static page pages[] = new page[LEN];

	public static void main(String[] args) {
		readPage();
		readLink();
		int startIndex = getIndexOf("アンパサンド");
		int targetIndex = getIndexOf("プログラミング言語");
		isNotFound(startIndex);
		isNotFound(targetIndex);
		int answerIndex = dfs(startIndex, targetIndex);
		isNotFound(answerIndex);
	}

	static void readPage() {
		int index = 0;
		try {
			//pagesファイルを指定
			File file = new File("src/pages.txt");
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
					File file = new File("src/pageArray.txt");
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
			File file = new File("src/links.txt");
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
			File file = new File("src/linkList.txt");
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

	//pから探し始める
	static int dfs(int start, int target) {
		//visitで訪問済みかどうか管理する
		stack stack = new stack();
		stack.push(start);
		while (stack.count() > -1) {
			int current = stack.pop();
			pages[current].visited = true;
			System.out.printf("%s --> \n", pages[current].title);
			if (pages[current].index == target) {
				System.out.println("found!");
				return pages[current].index;
			}
			Iterator<Integer> itr = pages[current].reference.iterator();
			while (itr.hasNext()) {
				int next = itr.next();
				if (pages[next].visited != true) {
					stack.push(next);
				}
			}
		}
		return -1;
	}
}