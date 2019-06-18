package wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;

//プログラムの実行方法 $ java -Xss16m wiki/wiki (これでスタックサイズを指定)

public class wiki {
	final static int LEN = 1483277;
	final static int MAX_DEPTH = 30;
	final static int LOOP = 3;
	static page pages[] = new page[LEN];
	static boolean isFound;

	public static void main(String[] args) {
		//下準備
		readPage();
		System.out.println("Read Page Done!");
		readLink();
		System.out.println("Read Link Done!");
		calcPageRank();
		System.out.println("Calc Page Rank Done!");
		// Comparatorを実装した匿名クラス
		Comparator<Integer> comparator = new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				//0より大きければswapが起きる、今回は降順にしたいのでo2の方が大きい時1とした
				return (pages[o2].rank > pages[o1].rank) ? 1 : -1;
			}
		};
		//すべてのページの参照リストをページランクの高いものから並び替える
		//こうすることでポピュラーなものから調べられると推測
		for (page p : pages)
			Collections.sort(p.reference, comparator);
		System.out.println("Page Rank Sort Done!");

		int startIndex = getIndexOf("バナナ");
		Scanner scanner = new Scanner(System.in);
		//Cntl+Dで終了
		for (;;) {
			System.out.print(pages[startIndex].title + " => ");
			int targetIndex = getIndexOf(scanner.nextLine());
			if (isNotFound(targetIndex)) {
				System.out.println("unknown input");
				continue;
			}
			isFound = false;
			dfs(startIndex, targetIndex, 0);
			if (isFound) {
				System.out.printf("Success!\n");
				editList(startIndex, targetIndex);
				startIndex = targetIndex;
			} else {
				System.out.printf("failed\n");
				System.out.printf("retry\n");
				startIndex = getIndexOf("バナナ");
			}
		}
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

	//referenceリストの並び替えを行うことでdfsを早くしようという試み
	static void editList(int startIndex, int targetIndex) {
		//start -> targetを辿ったと考える
		//もしtargetの参照リストにstartが入っていたら、target参照リストの先頭にstartを移動する
		//こうすることで類似のものが引っ張りやすくなるのではないかと想定
		if (pages[targetIndex].reference.contains(new Integer(startIndex))) {
			pages[targetIndex].reference.remove(new Integer(startIndex));
			pages[targetIndex].reference.add(0, new Integer(startIndex));
			//System.out.println("edited");
		}
	}

	static boolean isNotFound(int index) {
		return (index < 0) ? true : false;
	}

	static void dfs(int start, int target, int depth) {
		//visitで訪問済みかどうか管理する
		pages[start].visited = true;
		if (++depth < MAX_DEPTH) {
			Iterator<Integer> itr = pages[start].reference.iterator();
			while (itr.hasNext()) {
				int num = itr.next();
				//System.out.printf("%s --> %s \n", pages[start].title, pages[num].title);
				if (num == target) {
					isFound = true;
					return;
				} else {
					if (pages[num].visited == false) {
						dfs(num, target, depth);
					}
				}
			}
		}
	}

	//ページランクを計算してみる
	static void calcPageRank() {
		for (int i = 0; i < 3; i++) {
			for (page p : pages) {
				//referenceは必ず1つ以上あるという想定で行う
				p.present = p.rank / p.reference.size();
			}
			//for文を分けないと同時実行にならない
			for (page p : pages) {
				for (int given : p.reference) {
					pages[given].rank += p.present;
				}
			}
		}
	}
}