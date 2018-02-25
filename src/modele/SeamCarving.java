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
			graph.addEdge(new Edge(0, i, 0, false));
		}

		for (int i = 0; i < hauteur; i++) {
			for (int j = 0; j < largeur; j++) {
				if (count + largeur + 1 < n) {
					if (counthaut > 0 && counthaut % 2 == 1) {
						graph.addEdge(new Edge(count, count + largeur, 0, true));
						passe = true;

					} else {
						if (j != 0 && j != largeur - 1) {
							graph.addEdge(new Edge(count, count + largeur, itr[i][j], false));
							graph.addEdge(new Edge(count, count + largeur + 1, itr[i][j], false));
							graph.addEdge(new Edge(count, count + largeur - 1, itr[i][j], false));
						} else if (j == 0) {
							graph.addEdge(new Edge(count, count + largeur, itr[i][j], false));
							graph.addEdge(new Edge(count, count + largeur + 1, itr[i][j], false));
						} else if (j == largeur - 1) {
							graph.addEdge(new Edge(count, count + largeur - 1, itr[i][j], false));
							graph.addEdge(new Edge(count, count + largeur, itr[i][j], false));
						}
					}
				} else {
					graph.addEdge(new Edge(count, n - 1, itr[hauteur - 1][j],false));
				}
				count++;
			}
			counthaut++;
			if (passe == true) {
				i--;
				passe = false;
			}
		}
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

	public static int[][] supprChemin(int[][] tab, ArrayList<Edge> chemin, ArrayList<Edge> chemin2) {
		boolean check = true;
		int y = 0;
		int hauteur = tab.length;
		int largeur = tab[0].length;
		int[][] newTab = new int[hauteur][largeur - 2];
		int mult = 0;
		int total = 1;
		int index = 1;
		boolean change = false;

		ArrayList<Edge> poubellech1 = new ArrayList<Edge>();
		for (Edge e : chemin) {
			if (change == true) {
				e.setFrom(e.from - largeur * mult);
				e.setTo(e.to - largeur * mult);
			}
			if (e.getNewArrete() == true) {
				change = true;
				poubellech1.add(e);
				mult++;
			}
		}
		chemin.removeAll(poubellech1);

		ArrayList<Edge> poubellech2 = new ArrayList<Edge>();
		change = false;
		mult = 0;
		System.out.println("------");
		Iterator<Edge> it = chemin2.iterator();
		while (it.hasNext()) {
			Edge e = it.next();
			if (change == true) {
				e.setFrom(e.from - largeur * mult);
				e.setTo(e.to - largeur * mult);
			}
			if (e.getNewArrete() == true) {
				change = true;
				poubellech2.add(e);
				mult++;
			}
		}
		chemin2.removeAll(poubellech2);

		for (int i = 0; i < hauteur; i++) {
			y = 0;
			Edge act = chemin.get(index);
			Edge act2 = chemin2.get(index);
			for (int j = 0; j < largeur; j++) {
				if (act.from != total && act2.from != total) {
					newTab[i][y] = tab[i][j];
					y++;
				}
				total++;
			}
			index++;
		}
		return newTab;
	}

	public static HashMap<String, ArrayList<Edge>> twograph(Graph g, int s, int t) {
		ArrayList<Edge> chemin = djikstra(g, s, t);
		ArrayList<Edge> temp = new ArrayList<Edge>();
		ArrayList<Edge> arretes = new ArrayList<Edge>();
		int coutSommets[] = new int[g.vertices()];
		Iterator<Edge> iterator = g.edges().iterator();
		HashMap<String, ArrayList<Edge>> map = new HashMap<String, ArrayList<Edge>>();
		int res;
		int resultat[] = new int[g.getEdge() * 2];
		int it = 0;
		int from;
		int to;
		int largeur = 0;

		while (iterator.hasNext()){
			Edge e = iterator.next();
			if (e.from == 0){
				largeur++;
			}
			arretes.add(e);
		}
		int hauteur = g.vertices() / largeur;
		int verticies[][] = new int[hauteur][largeur];

		int total = 0;
		for (int i = 0; i < hauteur; i++){
			for (int j = 0; j < largeur; j++){
				if (i == 0){
					coutSommets[j] = 0;
				}
				verticies[i][j] = total;
				total++;
			}
		}


		int nbArrTot = 4;
		int nbArrAct = 4;
		int tot = 5;
		for (int i = 1; i < hauteur; i++) {
			if (i % 2 == 0 && i + 1 != hauteur) {
				nbArrAct += 4;
				nbArrTot = nbArrAct - 4;
			} else {
				nbArrAct += 4 + (largeur - 2) * 3;
				nbArrTot = nbArrAct - (4 + (largeur - 2) * 3);
			}
			for (int j = 0; j < largeur; j++) {
				coutSommets[tot] = 999;
				if (j == 0) {

					Edge e1 = arretes.get(nbArrTot);
					Edge e2 = arretes.get(nbArrTot + 4);
					res = e1.cost + coutSommets[e1.from];
					int res2 = e2.cost + coutSommets[e2.from];
					if (res2 < res && e1.to == tot && e2.to == tot){
						res = res2;
					}
					if (coutSommets[tot] > res) {
						coutSommets[tot] = res;
					}
				} else if (j == largeur - 1) {
					Edge e1 = arretes.get(nbArrAct - 1);
					Edge e2 = arretes.get(nbArrAct - 4);
					res = e1.cost + coutSommets[e1.from];
					int res2 = e2.cost + coutSommets[e2.from];
					if (res2 < res && e1.to == tot && e2.to == tot){
						res = res2;
					}
					if (coutSommets[tot] > res) {
						coutSommets[tot] = res;
					}
				} else {
					for (int k = nbArrTot; k < nbArrAct; k++) {
						Edge e = arretes.get(k);
						if (e.to == tot) {
							res = e.cost + coutSommets[e.from];
							if (coutSommets[tot] > res) {
								coutSommets[tot] = res;
							}
						}
					}
				}
				tot++;
			}
		}
		coutSommets[g.vertices() - 1] = 888;
		for (int i = nbArrAct; i < arretes.size(); i++){
			Edge e = arretes.get(i);
			res = e.cost + coutSommets[e.from];
			if (coutSommets[g.vertices() - 1] > res) {
				coutSommets[g.vertices() - 1] = res;
			}
		}

		iterator = g.edges().iterator();
		while (iterator.hasNext()) {
			Edge edge = iterator.next();
			temp.add(edge);
			res = edge.cost + coutSommets[edge.from] - coutSommets[edge.to];
			resultat[it] = res;
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
		//System.out.println("ok");
		boolean change = false;
		boolean change2 = false;


		Comparator<Edge> comparator = Comparator.comparing(Edge::getFrom);
		Collections.sort(chemin, comparator);
		Collections.sort(chemin2, comparator);



		for (int k = 0; k < chemin.size(); k++) {
			for (int l = 0; l < chemin2.size(); l++) {
				if (chemin.get(k).equals(chemin2.get(l))) {
				//	System.out.println(chemin.get(k).from + " " + chemin.get(k).to + " [ " + chemin2.get(l).from + " " +  chemin2.get(l).to);
					chemin.remove(chemin.get(k));
					chemin2.remove(chemin2.get(l));
				}
			}
		}
		for (int i = 0; i < chemin.size(); i++) {
			for (int j = 0; j < chemin2.size(); j++) {
				Edge e = chemin.get(i);
				Edge e2 = chemin2.get(j);
				if (e.to == e2.from) {
					chemin.add(e2);
					chemin2.remove(e2);
				}
			}
		}

		for (int i = 0; i < chemin2.size(); i++) {
			for (int j = 0; j < chemin.size(); j++) {
				Edge e = chemin.get(j);
				Edge e2 = chemin2.get(i);
				if (e2.to == e.from){
					chemin2.add(e);
					chemin.remove(e);
				//	System.out.println("-----------------" + e.from + " " + e.to);
				}
			}
		}
		//Comparator<Edge> comparator = Comparator.comparing(Edge::getFrom);
		Collections.sort(chemin, comparator);
		Collections.sort(chemin2, comparator);


		map.put("Chemin1", chemin);
		map.put("Chemin2", chemin2);
		return map;

	}

	public static void main(String args[]) {
		long debut = System.currentTimeMillis();
		ArrayList<Edge> chemin;
		ArrayList<Edge> chemin2;
		int test[][] = {{3, 11, 29, 9}, {8, 21, 29, 9}, {200, 60, 25, 9}, {201, 292, 11, 112}, {81, 221, 111, 23}};
		int image[][] = readpgm("ex1.pgm");
		int itr[][] = interest(image);
		Graph g;
		int i = 0;

		while (i < 25) {
			g = tograph(itr);
			HashMap<String, ArrayList<Edge>> map = twograph(g, 0, g.vertices() - 1);
			chemin = map.get("Chemin1");
			chemin2 = map.get("Chemin2");
			image = supprChemin(image, chemin, chemin2);
			itr = interest(image);
			i++;
		}
			System.out.println(System.currentTimeMillis() - debut);
		writepgm(image, "TestFinal3");
	}
}
