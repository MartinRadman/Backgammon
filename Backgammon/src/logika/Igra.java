package logika;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;



import logika.Zetoni.Polje;

public class Igra {
	
	protected Igralec igralec1;
	protected Igralec igralec2;
	protected Igralec trenutni_igralec;
	
	public Zetoni[] polje = new Zetoni[24];
	
	protected HashMap<int[], List<int[]>> vse_validne = new HashMap<int[], List<int[]>>();

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
		
		vse_validne = vse_validne_poteze(prva + 1, druga + 1);
		
		if (vse_validne.size() == 0) {
			zamenjaj_igralca();
			tabela[0] = -1;
			tabela[1] = -1;
		}
		
		return tabela;
	}
	
	public void odigraj(int zacetek, int konec) {
		polje[zacetek].odstrani();
		polje[konec].dodaj(trenutni_igralec.id());
		zamenjaj_igralca();
	}
	
	public List<int[]> mozne_poteze(int premik, Zetoni[] opazovano_polje) {
		int faktor_premika = (trenutni_igralec == igralec1) ? -1 : 1;
		int zacetek_doma = (trenutni_igralec == igralec1) ? 0 : 18;
		Polje id = trenutni_igralec.id();
		List<int[]> mozne_poteze = new ArrayList<int[]>();
		
		if (trenutni_igralec.udarjeni_zetoni() > 0) {
			int ciljno_mesto = (trenutni_igralec == igralec1) ? 24 - premik : premik - 1;
			Zetoni na_cilju = opazovano_polje[ciljno_mesto];
			if (na_cilju.na_polju() == id || na_cilju.stevilo() <= 1) {
				int[] poteza = new int[2];
				poteza[0] = -1;
				poteza[1] = ciljno_mesto;
				mozne_poteze.add(poteza);
			}
		}
		
		for (int mesto = 0; mesto < 24; mesto++) {
			int ciljno_mesto = mesto + premik * faktor_premika;
			Zetoni zetoni = opazovano_polje[mesto];
			if (zetoni.na_polju() != id || !je_veljaven(ciljno_mesto)) continue;
			Zetoni na_cilju = opazovano_polje[ciljno_mesto];
			if (na_cilju.na_polju() != id && na_cilju.stevilo() > 1) continue;
			int[] poteza = new int[2];
			poteza[0] = mesto;
			poteza[1] = ciljno_mesto;
			mozne_poteze.add(poteza);
		
		}
		
		if (je_vse_doma()) {
			for (int korak = 0; korak <= 5; korak++) {
				int mesto = zacetek_doma + korak;
				int ciljno_mesto = mesto + premik * faktor_premika;
				Zetoni opazovano_mesto = opazovano_polje[mesto];
				if (opazovano_mesto.na_polju() == id && !je_veljaven(ciljno_mesto)) {
					int[] poteza = new int[2];
					poteza[0] = mesto;
					poteza[1] = 24;
					mozne_poteze.add(poteza);
				}
			}
		}
		
		return mozne_poteze;
		
	}
	
	private boolean je_vse_doma() {
		int zacetek = (trenutni_igralec == igralec1) ? 6 : 0;
		for (int korak = 0; korak <= 17; korak++) {
			Zetoni opazovano_mesto = polje[zacetek + korak];
			if (opazovano_mesto.na_polju() == trenutni_igralec.id()) return false;
		}
		return true;
	}
	
	public List<HashMap<int[], List<int[]>>> mozna_zaporedja_potez(int premik1, int premik2) {
		List<HashMap<int[], List<int[]>>> zaporedja_potez = new ArrayList<HashMap<int[], List<int[]>>>();
		List<int[]> prve_poteze = mozne_poteze(premik1, polje);
		for (int[] poteza : prve_poteze) {
			HashMap<int[], List<int[]>> mozna_zaporedja_poteze = new HashMap<int[], List<int[]>>();
			Zetoni[] kopija_polja = polje.clone();
			kopija_polja[poteza[0]].odstrani();
			kopija_polja[poteza[1]].dodaj(trenutni_igralec.id());
			List<int[]> druge_poteze = mozne_poteze(premik2, kopija_polja);
			mozna_zaporedja_poteze.put(poteza, druge_poteze);
			zaporedja_potez.add(mozna_zaporedja_poteze);
		}
		return zaporedja_potez;
	}
	
	public List<List<HashMap<int[], List<int[]>>>> vse_mozne_poteze(int premik1, int premik2) {
		List<List<HashMap<int[], List<int[]>>>> vse_poteze = new ArrayList<List<HashMap<int[], List<int[]>>>>();
		List<HashMap<int[], List<int[]>>> prva_druga = mozna_zaporedja_potez(premik1, premik2);
		List<HashMap<int[], List<int[]>>> druga_prva = mozna_zaporedja_potez(premik2, premik1);
		vse_poteze.add(prva_druga);
		vse_poteze.add(druga_prva);
		return vse_poteze;
	}
	
	public HashMap<int[], List<int[]>> vse_validne_poteze(int premik1, int premik2) {
		List<List<HashMap<int[], List<int[]>>>> vse_poteze = vse_mozne_poteze(premik1, premik2);
		HashMap<int[], List<int[]>> validne_poteze = new HashMap<int[], List<int[]>>();
		List<List<HashMap<int[], List<int[]>>>> ociscene_poteze = new ArrayList<List<HashMap<int[], List<int[]>>>>();
		List<Integer> mozne_zaporedne_poteze = new ArrayList<Integer>();
		List<HashMap<int[], List<int[]>>> sez_ociscenih_potez;
		
		for (List<HashMap<int[], List<int[]>>> sez_potez : vse_poteze) {
			int mozne_zaporedne_poteze_aux = 0;
			sez_ociscenih_potez = prvo_ciscenje(sez_potez);
			ociscene_poteze.add(sez_ociscenih_potez);
			if (sez_ociscenih_potez.size() > 0) mozne_zaporedne_poteze_aux = 1;
			for (HashMap<int[], List<int[]>> poteza : sez_ociscenih_potez) {
				if (poteza.values().iterator().next().size() > 0) mozne_zaporedne_poteze_aux = 2;
			}
			mozne_zaporedne_poteze.add(mozne_zaporedne_poteze_aux);
		}
		
		
		if (mozne_zaporedne_poteze.contains(2)) {
			for (List<HashMap<int[], List<int[]>>> sez_potez : ociscene_poteze) {
				for (HashMap<int[], List<int[]>> poteza : sez_potez) {
					int[] prva_poteza = poteza.keySet().iterator().next();
					List<int[]> druge_poteze = poteza.values().iterator().next();
					if (druge_poteze.size() > 0) {
							validne_poteze.put(prva_poteza, druge_poteze);
					}
				}
			}
		}
		
		
		if (mozne_zaporedne_poteze.contains(1)) {
			if (mozne_zaporedne_poteze.get(0) == 1 && mozne_zaporedne_poteze.get(1) == 1) {
				List<HashMap<int[], List<int[]>>> prva_ociscena = ociscene_poteze.get(0);
				List<HashMap<int[], List<int[]>>> druga_ociscena = ociscene_poteze.get(1);
				int[] premik_prvi = prva_ociscena.get(0).keySet().iterator().next();
				int[] premik_drugi = druga_ociscena.get(0).keySet().iterator().next();
				int izbrani = (Math.abs(premik_prvi[0] - premik_prvi[1]) > Math.abs(premik_drugi[0] - premik_drugi[1])) ? 0 : 1;
				for (HashMap<int[], List<int[]>> poteza : ociscene_poteze.get(izbrani)) {
					int[] prva_poteza = poteza.keySet().iterator().next();
					List<int[]> druge_poteze = poteza.values().iterator().next();
					validne_poteze.put(prva_poteza, druge_poteze);
				}
			}
			
			
			for (List<HashMap<int[], List<int[]>>> sez_potez : ociscene_poteze) {
				for (HashMap<int[], List<int[]>> poteza : sez_potez) {
					int[] prva_poteza = poteza.keySet().iterator().next();
					List<int[]> druge_poteze = poteza.values().iterator().next();
					validne_poteze.put(prva_poteza, druge_poteze);
				}
			}	
		}
		
		return validne_poteze;
	}
	
	private List<HashMap<int[], List<int[]>>> prvo_ciscenje(List<HashMap<int[], List<int[]>>> sez_potez) {
		int udarjeni = trenutni_igralec.udarjeni_zetoni();
		List<HashMap<int[], List<int[]>>> sez_ociscenih_potez = new ArrayList<HashMap<int[], List<int[]>>>();
		
		for (HashMap<int[], List<int[]>> poteza : sez_potez) {
			int[] prva = poteza.keySet().iterator().next();
			List<int[]> drugi = poteza.get(prva);
			
			if (udarjeni > 0 && prva[0] != -1) continue;
			udarjeni--;
			if (udarjeni > 0) {
				for (int[] druga : drugi) {
					if (druga[0] != -1) drugi.remove(druga);
				}
			}
			
			HashMap<int[], List<int[]>> ociscena_poteza = new HashMap<int[], List<int[]>>();
			ociscena_poteza.put(prva, drugi);
			sez_ociscenih_potez.add(ociscena_poteza);
		}
		return sez_ociscenih_potez;
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
	
	public Igralec trenutni_igralec() {
		return trenutni_igralec;
	}
	
	public HashMap<int[], List<int[]>> vse_validne() {
		return vse_validne;
	}
}
