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
		int counthaut = 0;
		boolean passe = false;
		int largeur = itr[0].length;
		int n = hauteur * largeur + (hauteur - 2) * largeur + 2;
		Graph graph = new Graph(n);

		for (int i = 0; i <= largeur; i++) {
			graph.addEdge(new Edge(0, i, 0));
		}

		for (int i = 0; i < hauteur; i++) {
			for (int j = 0; j < largeur; j++) {
				if (count + largeur + 1 < n) {
					if (counthaut > 0 && counthaut % 2 == 1) {
						graph.addEdge(new Edge(count, count + largeur, 0));
						passe = true;
					} else {
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
					}
				} else {
					graph.addEdge(new Edge(count, n - 1, itr[hauteur - 1][j]));
				}
				count++;
			}
			counthaut++;
			if (passe == true) {
				i--;
				passe = false;
			}
		}
		graph.writeFile("test.dot");
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

	public static HashMap<String, ArrayList<Edge>> twograph(Graph g, int s, int t) {
		ArrayList<Edge> chemin = djikstra(g, s, t);
		ArrayList<Edge> temp = new ArrayList<Edge>();
		ArrayList<Edge> arretes = new ArrayList<Edge>();
		ArrayList<Edge> arretes1 = new ArrayList<Edge>();
		//ArrayList cout = new ArrayList();
		//int cout[] = new int[g.vertices()];
		Iterator<Edge> iterator = g.edges().iterator();
		HashMap<String, ArrayList<Edge>> map = new HashMap<String, ArrayList<Edge>>();
		HashMap<String, ArrayList<Edge>> cout = new HashMap<String, ArrayList<Edge>>();
		int res;
		int resultat[] = new int[g.getEdge() * 2];
		int iter = 0;
		int it = 0;
		int cost = 0;
		boolean continuer = true;
		int from;
		int to;

		 while (iterator.hasNext()) {
		 	Edge e = iterator.next();
			arretes.add(e);
		}

		cout[0] = 0;
		for (int i = 1; i < g.vertices(); i++) {
			long debut = System.currentTimeMillis();
			cout[i] = 999;
			System.out.println(arretes.size());
			while (iterator.hasNext()){
				Edge e = iterator.next();
				if (e.to == i) {
					//System.out.println(j);
					res = e.cost + cout[e.from];
					if (cout[i] > res) {
						cout[i] = res;
					}
				}else {
					arretes.add(e);
				}
				//System.out.println(arretes.remove(e) + "  " + i);
					//System.out.println(iter);
				//System.out.println(System.currentTimeMillis() - debut);
			}
			iterator =  arretes.iterator();
		}

		iterator = g.edges().iterator();
		while (iterator.hasNext()) {
			Edge edge = iterator.next();
			temp.add(edge);
			//System.out.println("Arrete " + edge.from + " - " + edge.to + " Ancien cout = " + edge.cost + " Operation -> " + coutChemin(djikstra(g, 0, edge.from)) + " - " + coutChemin(djikstra(g, 0, edge.to)));
		//	System.out.println(edge.from);
			res = edge.cost + cout[edge.from] - cout[edge.to];
			//res = edge.cost + coutChemin(djikstra(g, 0, edge.from)) - coutChemin(djikstra(g, 0, edge.to));
			resultat[it] = res;
			//System.out.println(resultat[it]);
			it++;
		}
		for (int j = 0; j < temp.size(); j++) {
			temp.get(j).setEdge(resultat[j]);
		}
		for (int k = 0; k < chemin.size(); k++) {
			Edge edge = chemin.get(k);
			from = edge.from;
			to = edge.to;
			edge.setFrom(to);
			edge.setTo(from);
		}
		ArrayList<Edge> chemin2 = djikstra(g, s, t);
		for (int k = 0; k < chemin.size(); k++) {
			Edge edge = chemin.get(k);
			from = edge.from;
			to = edge.to;
			edge.setFrom(to);
			edge.setTo(from);
		}
		for (int k = 0; k < chemin.size(); k++) {
			for (int l = 0; l < chemin2.size(); l++) {
				if (chemin.get(k).equals(chemin2.get(l))) {
					chemin.remove(chemin.get(k));
					chemin2.remove(chemin2.get(l));
				}
			}
		}
		g.writeFile("test");
		map.put("Chemin1", chemin);
		map.put("Chemin2", chemin2);
		return map;

	}

	public static void main(String args[]) {
		//long debut = System.currentTimeMillis();
		ArrayList<Edge> chemin;
		ArrayList<Edge> chemin2;
		int test[][] = { {3, 11, 24, 39},{8, 21, 29, 39}, {200, 60, 25, 0} };
		//int image[][] = readpgm("ex3.pgm");
		int itr[][] = interest(test);
		Graph g = tograph(itr);
		HashMap<String, ArrayList<Edge>> map = twograph(g, 0, g.vertices() - 1);
	//	System.out.println(System.currentTimeMillis() - debut);
		chemin = map.get("Chemin1");
		chemin2 = map.get("Chemin2");
		//image = supprChemin(image, chemin);
		//image = supprChemin(image, chemin2);
		//writepgm(image, "TestFinal3");
	}
}
//itr = interest(image);
//ArrayList<Edge> chemin = djikstra(g, 0, 8);
//System.out.println(coutChemin(chemin));
//for (int i = 0; i < chemin.size(); i++){
//System.out.println(chemin.get(i).cost);
//}
//int image[][] = readpgm("ex3.pgm");
//int itr[][] = interest(image);
//Graph g;
/**int i = 0;
 while (i < 50){
 g = tograph(itr);
 chemin = djikstra(g, 0, g.vertices() - 1);
 image = supprChemin(image, chemin);
 itr = interest(image);
 i++;
 }
 }**/

