package logika;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import logika.Zetoni.Polje;

public class Igra {
	
	protected Igralec igralec1;
	protected Igralec igralec2;
	protected Igralec trenutni_igralec;
	public Zetoni[] polje = new Zetoni[24];

	public static void main(String[] args) {
		Igra igra = new Igra(new Igralec("enel", Polje.IGRALEC1), new Igralec("inel", Polje.IGRALEC2));
		System.out.println((Polje.PRAZEN == igra.polje[1].na_polju()) ? "YE" : "NE");

	}
	
	public Igra(Igralec igralec1, Igralec igralec2) {
		this.igralec1 = igralec1;
		this.igralec2 = igralec2;
		this.trenutni_igralec = igralec1;
		
		priprava_igre();
	}
	
	public Igra() {
		this(new Igralec("1. igralec", Polje.IGRALEC1), new Igralec("2. igralec", Polje.IGRALEC2));
	}
	
	public Igra(Igra igra) {
		this(igra.igralec1, igra.igralec2);
		
		this.trenutni_igralec = igra.trenutni_igralec;
		this.polje = igra.polje.clone();
	}
	
	public void priprava_igre() {
		Arrays.fill(polje, new Zetoni(Polje.PRAZEN, 0));
		
		polje[0] = new Zetoni(Polje.IGRALEC2, 2);
		polje[11] = new Zetoni(Polje.IGRALEC2, 5);
		polje[16] = new Zetoni(Polje.IGRALEC2, 3);
		polje[18] = new Zetoni(Polje.IGRALEC2, 5);
		
		polje[5] = new Zetoni(Polje.IGRALEC1, 5);
		polje[7] = new Zetoni(Polje.IGRALEC1, 3);
		polje[12] = new Zetoni(Polje.IGRALEC1, 5);
		polje[23] = new Zetoni(Polje.IGRALEC1, 2);
	}
	
	public int[] vrzi_kocki() {
		Random rand = new Random();
		int[] tabela = new int[2];
		int max = 6;
		
		int prva = rand.nextInt(max);
		int druga = rand.nextInt(max);
		tabela[0] = prva + 1;
		tabela[1] = druga + 1;
		
		return tabela;
	}
	
	public void odigraj(int zacetek, int konec) {
		polje[zacetek].odstrani();
		polje[konec].dodaj(trenutni_igralec.id());
		zamenjaj_igralca();
	}
	
	public List<int[]> mozne_poteze(int premik) {
		int faktor_premika = (trenutni_igralec == igralec1) ? -1 : 1;
		Polje id = trenutni_igralec.id();
		List<int[]> mozne_poteze = new ArrayList<int[]>();
		for (int mesto = 0; mesto < 24; mesto++) {
			int ciljno_mesto = mesto + premik * faktor_premika;
			Zetoni zetoni = polje[mesto];
			if (zetoni.na_polju() != id || !je_veljaven(ciljno_mesto)) continue;
			Zetoni na_cilju = polje[ciljno_mesto];
			if (na_cilju.na_polju() != id && na_cilju.stevilo() > 1) continue;
			int[] poteza = new int[2];
			poteza[0] = mesto;
			poteza[1] = ciljno_mesto;
			mozne_poteze.add(poteza);
		}
		return mozne_poteze;
		
	}
	
	public List<HashMap<int[], List<int[]>>> mozna_zaporedja_potez(int premik1, int premik2) {
		List<HashMap<int[], List<int[]>>> zaporedja_potez = new ArrayList<HashMap<int[], List<int[]>>>();
		List<int[]> prve_poteze = mozne_poteze(premik1);
		for (int[] poteza : prve_poteze) {
			
		}
	}
	
	private boolean je_veljaven(int mesto) {
		return (mesto >= 0 && mesto <= 23);
	}
	
	public void zamenjaj_igralca() {
		if (trenutni_igralec == igralec1) {
			trenutni_igralec = igralec2;
		}
		else {
			trenutni_igralec = igralec1;
		}
	}
}
