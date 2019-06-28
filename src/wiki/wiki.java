package wiki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;

//プログラムの実行方法 $ java -Xss16m wiki/wiki (これでスタックサイズを指定)
//BFSは指定しないと実行が難しい

public class wiki {
	final static int LEN = 1483277;
	final static int MAX_DEPTH = 2;
	final static int LOOP = 3;
	final static String START = "バナナ";

	public static void main(String[] args) {
		//下準備
		List<page> pages = readPage("./pages.txt");
		System.out.printf("pages: %d\n", pages.size());
		System.out.println("Read Page Done!");
		readLinkAndUpdatePageReferences(pages, "./links.txt");
		System.out.println("Read Link Done!");
		calcPageRank(pages);
		System.out.println("Calc Page Rank Done!");
		// Comparatorを実装した匿名クラス
		Comparator<Integer> comparator = new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				//0より大きければswapが起きる
				if (pages.get(o1).rank > pages.get(o2).rank)
					return 1;
				else if (pages.get(o1).rank < pages.get(o2).rank)
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
		int startIndex = getIndexOf(pages, START);
		if (startIndex < 0) {
			System.out.printf("%s is not found.\n", START);
			return;
		}
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
			System.out.print(pages.get(startIndex).title + " => ");
			int targetIndex = -1;
			try {
				targetIndex = getIndexOf(pages, scanner.nextLine());
			} catch (Exception e) {
				System.out.println("Exit!");
				break;
			}
			if (isNotFound(targetIndex)) {
				System.out.println("Unknown input");
				continue;
			}
			resetVisited(pages);
			long startTime = System.currentTimeMillis();
			// List<Integer> route = dfs(pages, startIndex, targetIndex, 0);
			List<Integer> route = bfs(pages, startIndex, targetIndex);
			long endTime = System.currentTimeMillis();
			System.out.println("処理時間：" + (endTime - startTime) + " ms");

			// Print the route.
			if (route != null) {
				Collections.reverse(route);
				System.out.println("Route found:");
				System.out.printf("%s (%d) ", pages.get(route.get(0)).title, route.get(0));
				for (Integer n : route.subList(1, route.size())) {
					System.out.printf(" => %s (%d)", pages.get(n).title, n);
				}
				System.out.println("");
			} else {
				System.out.println("No route found.");
			}
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
			if (route != null) {
				System.out.printf("Success!\n");
				editList(pages, startIndex, targetIndex);
				startIndex = targetIndex;
			} else {
				System.out.printf("Failed\n");
				System.out.printf("Retry\n");
				startIndex = getIndexOf(pages, START);
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

	static List<page> readPage(String filepath) {
		List<page> pages = new ArrayList<>();
		try {
			//pagesファイルを指定
			File file = new File(filepath);
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
					pages.add(new page(Integer.valueOf(tok.nextToken()), tok.nextToken()));
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
		return pages;
	}

	static void readLinkAndUpdatePageReferences(List<page> pages, String filepath) {
		try {
			//pagesファイルを指定
			File file = new File(filepath);
			//BufferReaderは1行ずつ読み込む
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			//nullまで読み込む
			String line;
			StringTokenizer tok;
			while ((line = bufferedReader.readLine()) != null) {
				tok = new StringTokenizer(line);
				while (tok.hasMoreTokens()) {
					pages.get(Integer.valueOf(tok.nextToken())).reference.add(Integer.valueOf(tok.nextToken()));
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

	static int getIndexOf(List<page> pages, String word) {
		for (page p : pages) {
			// This could be faster if you use Map<String, Integer>.
			if (p.title.equals(word))
				return p.index;
		}
		return -1;
	}

	//referenceリストの並び替えを行うことでdfsを早くしようという試み
	static void editList(List<page> pages, int startIndex, int targetIndex) {
		//start -> targetを辿ったと考える
		//もしtargetとstartが相互に参照していたら
		//startとtargetの参照リストの先頭にお互いを移動する
		//こうすることで二回目以降の検索が高速になると推測
		if (pages.get(targetIndex).reference.contains(new Integer(startIndex))) {
			pages.get(targetIndex).reference.remove(new Integer(startIndex));
			pages.get(targetIndex).reference.add(0, new Integer(startIndex));
			System.out.println("edited");
		}
	}

	static boolean isNotFound(int index) {
		return (index < 0) ? true : false;
	}

	// Return the route if target is found in this path. If not, return null.
	static List<Integer> dfs(List<page> pages, int start, int target, int depth) {
		if (start == target) {
			return new ArrayList<Integer>(Arrays.asList(start));
		}
		if (depth > MAX_DEPTH)
			return null;
		for (int neighbor_index : pages.get(start).reference) {
			if (pages.get(neighbor_index).visited)
				continue;
			pages.get(neighbor_index).visited = true;
			List<Integer> route = dfs(pages, neighbor_index, target, depth + 1);
			if (route != null) {
				route.add(start);
				return route;
			}
		}
		return null;
	}

	// Return the steps from |start| to |target| in the |pages|.
	static List<Integer> bfs(List<page> pages, int start, int target) {
		Queue<Integer> queue = new ArrayDeque<>();
		int depth[] = new int[pages.size()];
		int previous_ids[] = new int[pages.size()];
		queue.add(start);
		depth[start] = 0;
		previous_ids[start] = -1;
		pages.get(start).visited = true;
		while (!queue.isEmpty()) {
			// Retrieve the first element in the queue.
			int num = queue.remove();
			if (num == target) {
				List<Integer> route = new ArrayList<>();
				// Calculate the route.
				int current = num;
				while (current >= 0) {
					route.add(current);
					current = previous_ids[current];
				}
				return route;
			}
			if (depth[num] > MAX_DEPTH) {
				// Target isn't found within MAX_DEPTH.
				return null;
			}
			for (int p : pages.get(num).reference) {
				if (pages.get(p).visited)
					continue;
				pages.get(p).visited = true;
				queue.add(p);
				depth[p] = depth[num] + 1;
				previous_ids[p] = num;
			}
		}
		return null;
	}

	static void resetVisited(List<page> pages) {
		for (page p : pages)
			p.visited = false;
	}

	//ページランクを計算してみる
	static void calcPageRank(List<page> pages) {
		for (int i = 0; i < 3; i++) {
			for (page p : pages) {
				//referenceは必ず1つ以上あるという想定で行う
				p.present = p.rank / p.reference.size();
			}
			//for文を分けないと同時実行にならない
			for (page p : pages) {
				for (int given : p.reference) {
					pages.get(given).rank += p.present;
				}
			}
		}
	}
}
