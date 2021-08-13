package logika;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import logika.Zetoni.Polje;

public class Igra {
	
	protected Igralec igralec1;
	protected Igralec igralec2;
	protected Igralec trenutni_igralec;
	
	public Zetoni[] polje = new Zetoni[24];
	
	protected HashMap<int[], List<int[]>> vse_validne = new HashMap<int[], List<int[]>>(); // validna zaporedja potez
	protected Set<int[]> trenutne_validne; // validne poteze za trenutni korak
	
	public int treba_izvesti = 0; // koraki, ki jih je potrebno še izvesti
	
	public enum Stanje {
		ZMAGA1, ZMAGA2, V_TEKU
	}
	
	public Igra(Igralec igralec1, Igralec igralec2) {
		
		this.igralec1 = igralec1;
		this.igralec2 = igralec2;
		this.trenutni_igralec = igralec1;
		
		priprava_igre();
		
	}
	
	public Igra() { // brez posebnih imen
		this(new Igralec("1. igralec", Polje.IGRALEC1), new Igralec("2. igralec", Polje.IGRALEC2));
	}
	
	public Igra(Igra igra) { // kopija igre
		this(new Igralec(igra.igralec1), new Igralec(igra.igralec2));
		
		this.trenutni_igralec = (igra.trenutni_igralec() == igra.igralec1) ? this.igralec1() : this.igralec2();
		this.polje = igra.kopija_polja();
		this.trenutne_validne = new HashSet<int[]>(igra.trenutne_validne());
		this.vse_validne = new HashMap<int[], List<int[]>>(igra.vse_validne());
		this.treba_izvesti = igra.treba_izvesti;
	}
	
	public void priprava_igre() { // pripravi začetno postavitev igre
		for (int i = 0; i <= 23; i++) {
			polje[i] = new Zetoni(Polje.PRAZEN, 0);
		}
		
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
		if (treba_izvesti > 0) throw new Error("Nekaj potez je še treba izvesti");
		Random rand = new Random();
		int[] tabela = new int[2];
		int max = 6;
		
		int prva = rand.nextInt(max);
		int druga = rand.nextInt(max);
		tabela[0] = prva + 1;
		tabela[1] = druga + 1;
		
		vse_validne = vse_validne_poteze(prva + 1, druga + 1);

		trenutne_validne = vse_validne.keySet();
		
		if (trenutne_validne.size() == 0) zamenjaj_igralca(); // konec poteze igralca, saj nima več validnih potez
		
		return tabela;
	}
	
	public void odigraj(int zacetek, int konec) {
		if (zacetek == -1) {	// -1 označuje žetone, ki so bili udarjeni
			
			if (polje[konec].na_polju != trenutni_igralec.id() && polje[konec].na_polju != Polje.PRAZEN) {
				zamenjaj_igralca();
				trenutni_igralec.dodaj_udarjenega();
				zamenjaj_igralca();
			}
			
			trenutni_igralec.odstrani_udarjenega();
			polje[konec].dodaj(trenutni_igralec.id());
		}
		
		else {
			if (konec != 24) { // 24 označuje končno polje
				
				if (polje[konec].na_polju != trenutni_igralec.id() && polje[konec].na_polju != Polje.PRAZEN) {
					zamenjaj_igralca();
					trenutni_igralec.dodaj_udarjenega();
					zamenjaj_igralca();
				}
				
				polje[zacetek].odstrani();
				polje[konec].dodaj(trenutni_igralec.id());
			}
			else {
				polje[zacetek].odstrani();
				trenutni_igralec.dodaj_izlocenega();
			}
		}
		
		treba_izvesti--;
		
		int[] koord = new int[2]; // izvedena poteza
		koord[0] = zacetek;
		koord[1] = konec;
		
		List<int[]> nove_validne = new ArrayList<int[]>();
		
		for (int[] kljuc : vse_validne.keySet()) {
			if (Arrays.equals(koord, kljuc)) nove_validne = vse_validne.get(kljuc); // znotraj slovarja najde seznam 2. potez, ki pripada tej potezi
		}
		
		if (treba_izvesti > 0) trenutne_validne = new HashSet<int[]>(nove_validne);
		
		if (treba_izvesti == 0) zamenjaj_igralca();
	}
	
	public Stanje stanje() { // zmaga tisti, ki prvi izloči 15 žetonov
		if (igralec1.izloceni_zetoni() == 15) return Stanje.ZMAGA1;
		if (igralec2.izloceni_zetoni() == 15) return Stanje.ZMAGA2;
		return Stanje.V_TEKU;
	}
	
	public List<int[]> mozne_poteze(int premik, Zetoni[] opazovano_polje, Igralec trenutni_igralec_kopija) { // pogleda katere poteze je možno izvesti z dano vrednostjo kocke (premik)
		int faktor_premika = (trenutni_igralec == igralec1) ? -1 : 1; // določi smer premikanja
		int zacetek_doma = (trenutni_igralec == igralec1) ? 0 : 18;
		Polje id = trenutni_igralec.id();
		List<int[]> mozne_poteze = new ArrayList<int[]>();
		
		if (trenutni_igralec_kopija.udarjeni_zetoni() > 0) { // poskrbi, da v igro najprej pridejo izločeni žetoni
			int ciljno_mesto = (trenutni_igralec_kopija == igralec1) ? 24 - premik : premik - 1;
			Zetoni na_cilju = opazovano_polje[ciljno_mesto];
			if (na_cilju.na_polju() == id || na_cilju.stevilo() <= 1) {
				int[] poteza = new int[2];
				poteza[0] = -1;
				poteza[1] = ciljno_mesto;
				mozne_poteze.add(poteza);
			}
		}
		for (int mesto = 0; mesto < 24; mesto++) { // se pelje po polju, pogleda če je tam žeton trenutnega igralca in pogleda kam ga lahko premakne
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
		
		if (je_vse_doma()) { // ko so vsi žetoni v končnem polju, se lahko začne izločanje
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
	
	private boolean je_vse_doma() { // preveri ali so vsi žetoni v končnem območju
		int zacetek = (trenutni_igralec == igralec1) ? 6 : 0;
		for (int korak = 0; korak <= 17; korak++) {
			Zetoni opazovano_mesto = polje[zacetek + korak];
			if (opazovano_mesto.na_polju() == trenutni_igralec.id()) return false;
		}
		return true;
	}
	
	public List<HashMap<int[], List<int[]>>> mozna_zaporedja_potez(int premik1, int premik2) { // najde vsa možna zaporedja premikov, ki ju določa met kocke
		List<HashMap<int[], List<int[]>>> zaporedja_potez = new ArrayList<HashMap<int[], List<int[]>>>();
		List<int[]> prve_poteze = mozne_poteze(premik1, polje, trenutni_igralec);
		for (int[] poteza : prve_poteze) {
			HashMap<int[], List<int[]>> mozna_zaporedja_poteze = new HashMap<int[], List<int[]>>();
			Zetoni[] kopija_polja = kopija_polja();
			Igralec igralec1_kopija = new Igralec(igralec1);
			Igralec igralec2_kopija = new Igralec(igralec2);
			
			if (poteza[0] == -1) { // odstrani udarjenega in ga pošlje v igro
				if (trenutni_igralec == igralec1) igralec1_kopija.odstrani_udarjenega();
				else igralec2_kopija.odstrani_udarjenega();
				kopija_polja[poteza[1]].dodaj(trenutni_igralec.id());
			}
			
			else {
				if (poteza[1] == 24) { // izloči žetone, ki so v zadnjem območju polja
					kopija_polja[poteza[0]].odstrani();
					if (trenutni_igralec == igralec1) igralec1_kopija.dodaj_izlocenega();
					else igralec2_kopija.dodaj_izlocenega();
				}
				else { // normalna poteza
					kopija_polja[poteza[0]].odstrani();
					kopija_polja[poteza[1]].dodaj(trenutni_igralec.id());
				}
			}
			
			Igralec trenutni_igralec_kopija = (trenutni_igralec == igralec1) ? igralec1_kopija : igralec2_kopija;
			List<int[]> druge_poteze = mozne_poteze(premik2, kopija_polja, trenutni_igralec_kopija);
			mozna_zaporedja_poteze.put(poteza, druge_poteze); // sestavi možne kombinacije potez
			zaporedja_potez.add(mozna_zaporedja_poteze);
		}
		return zaporedja_potez;
	}
	
	public List<List<HashMap<int[], List<int[]>>>> vse_mozne_poteze(int premik1, int premik2) { // isto kot prejšnja metoda, samo da tukaj ni pomemben vrstni red premikov
		List<List<HashMap<int[], List<int[]>>>> vse_poteze = new ArrayList<List<HashMap<int[], List<int[]>>>>();
		List<HashMap<int[], List<int[]>>> prva_druga = mozna_zaporedja_potez(premik1, premik2);
		List<HashMap<int[], List<int[]>>> druga_prva = mozna_zaporedja_potez(premik2, premik1);
		vse_poteze.add(prva_druga);
		vse_poteze.add(druga_prva);
		return vse_poteze;
	}
	
	public HashMap<int[], List<int[]>> vse_validne_poteze(int premik1, int premik2) { // upošteva dodatna pravila premikanja žetonov
		List<List<HashMap<int[], List<int[]>>>> vse_poteze = vse_mozne_poteze(premik1, premik2);
		HashMap<int[], List<int[]>> validne_poteze = new HashMap<int[], List<int[]>>();
		List<List<HashMap<int[], List<int[]>>>> ociscene_poteze = new ArrayList<List<HashMap<int[], List<int[]>>>>();
		List<Integer> mozne_zaporedne_poteze = new ArrayList<Integer>();
		List<HashMap<int[], List<int[]>>> sez_ociscenih_potez;
		
		for (List<HashMap<int[], List<int[]>>> sez_potez : vse_poteze) {
			int mozne_zaporedne_poteze_aux = 0;
			sez_ociscenih_potez = prvo_ciscenje(sez_potez); // poskrbi, da se udarjeni žetoni prvi uporabijo
			ociscene_poteze.add(sez_ociscenih_potez);
			if (sez_ociscenih_potez.size() > 0) mozne_zaporedne_poteze_aux = 1;
			for (HashMap<int[], List<int[]>> poteza : sez_ociscenih_potez) {
				if (poteza.values().iterator().next().size() > 0) mozne_zaporedne_poteze_aux = 2;
			}
			mozne_zaporedne_poteze.add(mozne_zaporedne_poteze_aux);
		}
		
		
		if (mozne_zaporedne_poteze.contains(2)) { // v primeru, ko je možno obe potezi igrati
			treba_izvesti = 2;
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
		
		
		if (mozne_zaporedne_poteze.contains(1)) { // ko je možno samo 1 potezo odigrati 
			treba_izvesti = 1;
			if (mozne_zaporedne_poteze.get(0) == 1 && mozne_zaporedne_poteze.get(1) == 1) { // ali se ena odigra ali druga, vendar ne obe hkrati - izbere večjo vrednost
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
			List<int[]> drugi_aux = new ArrayList<int[]>(drugi);
			
			if (udarjeni > 0 && prva[0] != -1) continue;
			udarjeni--;
			if (udarjeni > 0) {
				for (int[] druga : drugi) {
					if (druga[0] != -1) drugi_aux.remove(druga);
				}
			}
			
			drugi = drugi_aux;
			
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
	
	public Set<int[]> trenutne_validne() {
		return trenutne_validne;
	}
	
	private Zetoni[] kopija_polja() {
		Zetoni[] kopija = new Zetoni[24];
		for (int i = 0; i <= 23; i++) {
			Zetoni star_zeton = polje[i];
			Zetoni nov_zeton = new Zetoni(star_zeton.na_polju(), star_zeton.stevilo());
			kopija[i] = nov_zeton;
		}
		return kopija;
	}
	
	public Igralec igralec1() {
		return igralec1;
	}
	
	public Igralec igralec2() {
		return igralec2;
	}
}
