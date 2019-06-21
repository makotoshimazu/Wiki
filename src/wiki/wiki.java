package wiki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;

//プログラムの実行方法 $ java -Xss16m wiki/wiki (これでスタックサイズを指定)

public class wiki {
	final static int LEN = 1483277;
	final static int MAX_DEPTH = 2;
	final static int LOOP = 3;
	final static String START = "バナナ";
	static page pages[] = new page[LEN];
	static boolean isFound = false;

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
				//0より大きければswapが起きる
				if (pages[o1].rank > pages[o2].rank)
					return 1;
				else if (pages[o1].rank < pages[o2].rank)
					return -1;
				else
					return 0;
			}
		};
		//すべてのページの参照リストをページランクの低いものから並び替える
		//こうすることでよりそのページに関連したマニアックなデータから取得できると予想
		for (page p : pages)
			Collections.sort(p.reference, comparator);
		System.out.println("Sort By Page Rank Done!");
		/*
		//STARTが参照するリストについてページランクの確認
		for (int p : pages[getIndexOf(START)].reference)
			System.out.println(p + " " + pages[p].rank + " " + pages[p].title);
		*/
		int startIndex = getIndexOf(START);
		Scanner scanner = new Scanner(System.in);

		//File file = new File("./searchingDFS.txt");
		//File file = new File("./searchingBFS.txt");
		/*
		BufferedWriter bufferWriter = null;
		try {
			bufferWriter = new BufferedWriter(new FileWriter(file));
		} catch (FileNotFoundException e) {
			System.out.println(e);
			//writeでエラーが出た時
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		//Cntl+Dで終了
		for (;;) {
			System.out.print(pages[startIndex].title + " => ");
			int targetIndex = -1;
			try {
				targetIndex = getIndexOf(scanner.nextLine());
			} catch (Exception e) {
				System.out.println("Exit!");
				break;
			}
			if (isNotFound(targetIndex)) {
				System.out.println("Unknown input");
				continue;
			}
			isFound = false;
			resetVisited();
			dfs(startIndex, targetIndex, 0);
			//bfs(startIndex, targetIndex);
			/*
			try {
				bufferWriter.write("-----------start------------");
				//改行コードをOS似合わせて自動で判断して出力
				bufferWriter.newLine();
				//dfs(startIndex, targetIndex, 0, bufferWriter);
				bfs(startIndex, targetIndex, bufferWriter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			if (isFound) {
				System.out.printf("Success!\n");
				editList(startIndex, targetIndex);
				startIndex = targetIndex;
			} else {
				System.out.printf("Failed\n");
				System.out.printf("Retry\n");
				startIndex = getIndexOf(START);
			}
		}
		/*
		try {
			bufferWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		scanner.close();

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
			for (int p : pages[getIndexOf("バナナ")].reference) {
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
		//もしtargetとstartが相互に参照していたら
		//startとtargetの参照リストの先頭にお互いを移動する
		//こうすることで二回目以降の検索が高速になると推測
		if (pages[targetIndex].reference.contains(new Integer(startIndex))) {
			pages[targetIndex].reference.remove(new Integer(startIndex));
			pages[targetIndex].reference.add(0, new Integer(startIndex));
			System.out.println("edited");
		}
	}

	static boolean isNotFound(int index) {
		return (index < 0) ? true : false;
	}

	static void dfs(int start, int target, int depth) {
		//early return
		//visitで訪問済みかどうか管理する
		if (pages[start].visited)
			return;
		if (start == target) {
			isFound = true;
			return;
		}
		if (++depth > MAX_DEPTH)
			return;
		pages[start].visited = true;
		Iterator<Integer> itr = pages[start].reference.iterator();
		while (itr.hasNext()) {
			int num = itr.next();
			dfs(num, target, depth);
		}
	}

	//debug用のメソッドをオーバーロード
	static void dfs(int start, int target, int depth, BufferedWriter bw) throws IOException {
		//early return
		//visitで訪問済みかどうか管理する
		if (pages[start].visited)
			return;
		if (start == target) {
			isFound = true;
			System.out.println("depth : " + depth);
			return;
		}
		if (++depth > MAX_DEPTH)
			return;
		pages[start].visited = true;
		Iterator<Integer> itr = pages[start].reference.iterator();
		while (itr.hasNext()) {
			int num = itr.next();
			bw.write(pages[start].title + " -> " + pages[num].title);
			//改行コードをOS似合わせて自動で判断して出力
			bw.newLine();
			dfs(num, target, depth, bw);
		}
	}

	static void bfs(int start, int target) {
		ArrayList<Integer> queue = new ArrayList<Integer>();
		for (int p : pages[start].reference) {
			queue.add(p);
		}
		int depth = 1;
		int index = 0;
		int tail = queue.size();
		while (true) {
			int num = queue.get(index++);
			if (num == target) {
				isFound = true;
				return;
			}
			pages[num].visited = true;
			for (int p : pages[num].reference) {
				if (pages[p].visited)
					continue;
				queue.add(p);
			}
			if (queue.indexOf(num) == tail - 1) {
				if (++depth > MAX_DEPTH)
					return;
				//0からtail-1(subListはtailを含まない)まで削除
				queue.subList(0, tail).clear();
				index = 0;
				tail = queue.size();
			}
		}
	}

	static void bfs(int start, int target, BufferedWriter bw) throws IOException {
		ArrayList<Integer> queue = new ArrayList<Integer>();
		for (int p : pages[start].reference) {
			queue.add(p);
			bw.write(pages[start].title + " -> " + pages[p].title);
			//改行コードをOS似合わせて自動で判断して出力
			bw.newLine();
		}
		int depth = 1;
		int index = 0;
		int tail = queue.size();
		while (true) {
			int num = queue.get(index++);
			if (num == target) {
				isFound = true;
				System.out.print(depth + " : ");
				return;
			}
			pages[num].visited = true;
			for (int p : pages[num].reference) {
				if (pages[p].visited)
					continue;
				bw.write(pages[num].title + " -> " + pages[p].title);
				//改行コードをOS似合わせて自動で判断して出力
				bw.newLine();
				queue.add(p);
			}
			if (queue.indexOf(num) == tail - 1) {
				if (++depth > MAX_DEPTH)
					return;
				//0からtail-1(subListはtailを含まない)まで削除
				queue.subList(0, tail).clear();
				index = 0;
				tail = queue.size();
			}
		}
	}

	static void resetVisited() {
		for (page p : pages)
			p.visited = false;
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