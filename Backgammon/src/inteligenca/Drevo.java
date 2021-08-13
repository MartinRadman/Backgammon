package inteligenca;

import java.util.ArrayList;
import java.util.List;

import logika.Igra;

public class Drevo { // razred, s katerim bomo predstavljali drevesa
	
	public Igra igra;
	public int n; // število obiskov
	public int v; // vrednost drevesa glede na zmage - izgube
	public Drevo s; // starševsko drevo
	public List<Drevo> sez_listov; // seznam vseh listov
	public boolean je_obiskan = false;
	public int[] k;

	public Drevo(Igra igra, int[] k, int n, int v, List<Drevo> sez_listov) {
		this.n = n;
		this.v = v;
		this.sez_listov = sez_listov;
		this.igra = igra;
		this.k = k;
	}
	
	public Drevo(Igra igra) { // koren
		this(igra, new int[] {69, 69}, 0, 0, new ArrayList<Drevo>());
	}
	
	public boolean je_koren() {
		return (s == null);
	}
	
	public void nastavi_da_je_obiskan() {
		je_obiskan = true;
	}
	
}
