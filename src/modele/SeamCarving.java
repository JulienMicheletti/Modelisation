package modele;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
public class SeamCarving {
	private int hauteur;

	public static void writepgm(int[][] image, String filename) {
		int largeur = image.length;
		int hauteur = 0;
		if (largeur != 0) {
			hauteur = image[0].length;
		}
		try {
			String line;
			BufferedWriter fichierpgm = new BufferedWriter(new FileWriter(filename + ".pgm"));
			fichierpgm.write("P2\n");
			fichierpgm.write(hauteur + " ");
			fichierpgm.write(largeur + "\n");
			fichierpgm.write("255\n");
			for (int i = 0; i < largeur; i++) {
				for (int j = 0; j < hauteur; j++) {
					fichierpgm.write(image[i][j] + " ");
				}
				fichierpgm.write("\n");
			}
			fichierpgm.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int[][] interest(int[][] image) {
		int largeur = image.length;
		int hauteur = 0;
		int moyenne = 0;
		if (largeur != 0) {
			hauteur = image[0].length;
		}
		int[][] interet = new int[largeur][hauteur];
		for (int i = 0; i < largeur; i++) {
			for (int j = 0; j < hauteur; j++) {
				if (j != 0 && j != hauteur - 1) {
					moyenne = (image[i][j - 1] + image[i][j + 1]) / 2;
					interet[i][j] = Math.abs(image[i][j] - moyenne);
				} else if (j == 0) {
					interet[i][j] = Math.abs(image[i][j] - image[i][j + 1]);
				} else if (j == hauteur - 1) {
					interet[i][j] = Math.abs(image[i][j] - image[i][j - 1]);
				}
			}
		}
		return interet;
	}

	public static Graph tograph(int[][] itr) {
		int count = 1;
		int hauteur = itr.length;
		int largeur = 0;
		if (hauteur != 0) {
			largeur = itr[0].length;
		}
		int n = largeur * hauteur + 2;
		Graph graph = new Graph(n);
		for (int i = 0; i <= largeur; i++) {
			graph.addEdge(new Edge(0, i, 0));
		}
		for (int i = 0; i < hauteur; i++) {
			for (int j = 0; j < largeur; j++) {
				if (count + largeur + 1 < n) {
					if (j != 0 && j != largeur - 1) {
						graph.addEdge(new Edge(count, count + largeur, itr[i][j]));
						graph.addEdge(new Edge(count, count + largeur + 1, itr[i][j]));
						graph.addEdge(new Edge(count, count + largeur - 1, itr[i][j]));
					} else if (j == 0) {
						graph.addEdge(new Edge(count, count + largeur, itr[i][j]));
						graph.addEdge(new Edge(count, count + largeur + 1, itr[i][j]));
					} else if (j == largeur - 1) {
						graph.addEdge(new Edge(count, count + largeur - 1, itr[i][j]));
						graph.addEdge(new Edge(count, count + largeur, itr[i][j]));
					}
				} else {
					graph.addEdge(new Edge(count, n - 1, itr[i][j]));
				}
				count++;
			}
		}
		graph.writeFile("graph.dot");
		return graph;
	}

	public static ArrayList<Edge> djikstra(Graph g, int s, int t) {
		int n = g.vertices();
		int v = 0;
		int nb = 0;
		int cout = 0;
		ArrayList<Edge> chemin = new ArrayList<Edge>();
		Edge parent[] = new Edge[n];
		Heap tas = new Heap(n);
		boolean visite[] = new boolean[n];
		tas.decreaseKey(0, 0);
		while (!tas.isEmpty()) {
			v = tas.pop();
			visite[v] = true;
			for (Edge e : g.next(v)) {
				if (visite[e.to] == false) {
					cout = e.cost + tas.priority(v);
					if (cout < tas.priority(e.to)) {
						tas.decreaseKey(e.to, cout);
						parent[e.to] = e;
					}
				}
			}
		}
		int i = t;
		while (i != s) {
			chemin.add(parent[i]);
			i = parent[i].from;
		}
		return chemin;
	}

	public static int[][] readpgm(String fn) {
		try {
			InputStream f = ClassLoader.getSystemClassLoader().getResourceAsStream(fn);
			BufferedReader d = new BufferedReader(new InputStreamReader(f));
			String magic = d.readLine();
			String line = d.readLine();
			while (line.startsWith("#")) {
				line = d.readLine();
			}
			Scanner s = new Scanner(line);
			int width = s.nextInt();
			int height = s.nextInt();
			line = d.readLine();
			s = new Scanner(line);
			int maxVal = s.nextInt();
			int[][] im = new int[height][width];
			s = new Scanner(d);
			int count = 0;
			while (count < height * width) {
				im[count / width][count % width] = s.nextInt();
				count++;
			}
			return im;
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			return null;
		}
	}

	public static int[][] supprChemin(int[][] tab, ArrayList<Edge> chemin) {
		boolean check = true;
		int y = 0;
		int hauteur = tab.length;
		int largeur = tab[0].length;
		int[][] newTab = new int[hauteur][largeur - 1];
		int total = 1;
		int index = hauteur;

		for (int i = 0; i < hauteur; i++) {
			index--;
			y = 0;
			Edge act = chemin.get(index);
			for (int j = 0; j < largeur; j++) {
				if (act.from != total) {
					newTab[i][y] = tab[i][j];
					y++;
				}
				total++;
			}
		}
		return newTab;
	}


	public static void main(String args[]) {
		//int test[][] = { {3, 11, 24, 39},{8, 21, 29, 39}, {200, 60, 25, 0} };
		long debut = System.currentTimeMillis();
		int image[][] = readpgm("ex3.pgm");
		int itr[][] = interest(image);
		//Graph g = tograph(itr);
		//ArrayList<Edge> chemin = djikstra(g, 0, g.vertices() - 1);
		//int newTab[][] = supprChemin(test, chemin);

		/**for (int i = 0; i < newTab.length; i++){
			for (int j = 0; j < newTab[0].length; j++){
				System.out.print(newTab[i][j] + " ");
			}
			System.out.println("\n");
		}**/
		Graph g;// = tograph(itr);
		ArrayList<Edge> chemin; //= djikstra(g, 0, g.vertices() - 1);
		//int newTab[][] = supprChemin(image, chemin);
		int i = 0;
		while (i < 50){
			g = tograph(itr);
			chemin = djikstra(g, 0, g.vertices() - 1);
			//System.out.println(System.currentTimeMillis()-debut2);
			image = supprChemin(image, chemin);
			itr = interest(image);
			i++;
		}
		writepgm(image, "TestFinal2");
		System.out.println(System.currentTimeMillis()-debut);
		/**while (i <= 50){
		 System.out.println(i);
		 ArrayList<Edge> chemin = djikstra(g, 0, 13);
		 int newTab[][] = supprChemin(itr, chemin);
		 g = tograph(newTab);
		 i++;
		 }**/
	}
}
