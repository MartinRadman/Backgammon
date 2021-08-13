package inteligenca;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.Set;

import logika.Igra;
import logika.Zetoni.Polje;
import logika.Igra.Stanje;


public class MonteCarloTreeSearch {
	
	final int casovna_omejitev = 1000; // v milisekundah
	final double c = Math.sqrt(2);
	protected long start; // čas, ko se začne izvajati algoritem
	protected long konec; // čas, po katerem se več ne izvaja algoritem
	
	
	
	protected Drevo koren;
	protected Polje jaz;
	protected Igra igra;
	
	public MonteCarloTreeSearch(Drevo koren, Polje jaz, Igra igra) {
		this.koren = koren;
		this.jaz = jaz;
		this.igra = igra;
	}

	
	public Drevo[] monte_carlo_tree_search() {
	  start = System.currentTimeMillis();
	  konec = start + casovna_omejitev;
	  while (System.currentTimeMillis() < konec) {
	        Drevo list = prehodi(koren); // list = neobiskan node
	        list.je_obiskan = true;
	        int rezultat_simulacije = odigraj_do_konca(list); // odigra eno igro
	        izpolni_za_nazaj(list, rezultat_simulacije); // v vsakem koraku izvedene igre posodobi podatke z izidom
	  }
	  Drevo najboljsi1 = najboljsi_podlist(koren); // najboljša 1. poteza
	  Drevo najboljsi2 = najboljsi_podlist(najboljsi1); // najboljša 2. poteza
	  return new Drevo[] {najboljsi1, najboljsi2};
	}
	
	public Drevo prehodi(Drevo list) { // najde še neobiskani list
		Drevo nov_list = list;
		if (nov_list.sez_listov.size() == 0) return nov_list;
	    while (popolnoma_obiskani_podlisti(nov_list)) {
	    	if (nov_list.sez_listov.size() == 0) return nov_list;
	        nov_list = najboljsi_uct(nov_list);
	        }
	    return izberi_neobiskanega(nov_list.sez_listov); // če ni podlistov oz. je to končni list
	}
	
	public boolean popolnoma_obiskani_podlisti(Drevo list) { // preveri, če so vsi podlisti obiskani
		if (list == null) return true;
		for (Drevo podlist : list.sez_listov) {
			if (podlist.je_obiskan == false) return false;
		}
		return true;
	}
	
	public double izracunaj_uct(Drevo list) { // algoritem za izračun najboljše naslednje poteze
		if (list == null) return -1000000;
		double vrednost_lista = (double) list.v;
		double obiskanost_lista = (double) list.n;
		double obiskanost_korena = (double) koren.n;
		
		double prvi_clen = vrednost_lista / obiskanost_lista;
		double v_korenu = Math.log(obiskanost_korena / obiskanost_lista);
		double drugi_clen = c * Math.sqrt(v_korenu);
		
		return prvi_clen + drugi_clen;
	}
	
	public Drevo najboljsi_uct(Drevo list) { // najde najboljši podlist za naslednjo potezo
		Drevo najboljsi = null;
		double uct_najboljsi = 0;
		for (Drevo podlist : list.sez_listov) {
			if (podlist == null) continue;
			double uct_nov = izracunaj_uct(podlist);
			if (najboljsi == null || uct_nov > uct_najboljsi) {
				najboljsi = podlist;
				uct_najboljsi = uct_nov;
			}
		}
		return najboljsi;
	}
	
	public Drevo izberi_neobiskanega(List<Drevo> sez_listov) { // naključen izbor neobiskanega podlista
		List<Drevo> neobiskani = new ArrayList<Drevo>();
		for (Drevo list : sez_listov) {
			if (!list.je_obiskan) neobiskani.add(list);
		}
		return izberi_nakljucno(neobiskani);
	}
	
	
	public Drevo izberi_nakljucno(List<Drevo> sez_listov) {
		Random rand = new Random();
		return sez_listov.get(rand.nextInt(sez_listov.size()));
	}
	    		
	 	
	public int odigraj_do_konca(Drevo list) { // igranje igre do zaključka
		napolni_s_podlisti(list, list.igra);
	    while (je_nekoncen(list)) {
	        list = pravilo_igranja(list);
	        napolni_s_podlisti(list, list.igra);
	    }
	    return rezultat(list);
	}
	
	
	
	public void napolni_s_podlisti(Drevo list, Igra kopija_igre) { // listu poda vse njegove podliste
		if (list.sez_listov.size() != 0) return;
		else {
			if (kopija_igre.treba_izvesti == 0) kopija_igre.vrzi_kocki();
			Set<int[]> moznePoteze = kopija_igre.trenutne_validne();
			while (moznePoteze.size() == 0 && kopija_igre.stanje() == Stanje.V_TEKU) {
				kopija_igre.vrzi_kocki();
				moznePoteze = kopija_igre.trenutne_validne();
				
			}
			List<Drevo> odigrane_poteze = new ArrayList<Drevo>();
			for (int[] p: moznePoteze) {
				Igra kopijaIgre = new Igra(kopija_igre);
				
				kopijaIgre.odigraj(p[0], p[1]);
				
				Drevo poteza = new Drevo(kopijaIgre, p, 0, 0, new ArrayList<Drevo>());
				poteza.s = list;
				odigrane_poteze.add(poteza);
				
				Set<int[]> moznePoteze1 = kopijaIgre.trenutne_validne();
				List<Drevo> odigrane_poteze1 = new ArrayList<Drevo>();
				for (int[] b: moznePoteze1) {
					Igra kopijaIgre1 = new Igra(kopija_igre);
					boolean failsafe = false;
					
					try {
						kopijaIgre1.odigraj(b[0], b[1]);
					} catch (Error e)
					{failsafe = true;}
					
					if (failsafe) continue;
					
					Drevo poteza1 = new Drevo(kopijaIgre1, b, 0, 0, new ArrayList<Drevo>());
					poteza1.s = poteza;
					odigrane_poteze1.add(poteza);
				}
				if (moznePoteze1.size() != 0) poteza.sez_listov = odigrane_poteze1;
			}
			list.sez_listov = odigrane_poteze;
		}
		
	}
	
	
	public boolean je_nekoncen(Drevo list) {
		return (!(list.sez_listov.size() == 0));
	}
	
	public Drevo pravilo_igranja(Drevo list) {
		return izberi_nakljucno(list.sez_listov);
	}
	
	public int rezultat(Drevo list) { // pove, ali je za igralca zmaga ali poraz
		int rezultat = -2;
		Igra igra = list.igra;
		Stanje stanje = igra.stanje();
		switch(stanje) {
		case ZMAGA1: rezultat = (jaz == Polje.IGRALEC1) ? 1 : -1; break;
		case ZMAGA2: rezultat = (jaz == Polje.IGRALEC2) ? 1 : -1; break;
		case V_TEKU: throw new Error();
		default: throw new Error();
		}
		if (rezultat == -2) throw new Error();
		return rezultat;
	}


    public void izpolni_za_nazaj(Drevo list, int rezultat) {
    	if (list.je_koren()) {
    		list.n += 1;
    		return ;
    	}
    	list.v += rezultat;
    	list.n += 1;
	    izpolni_za_nazaj(list.s, rezultat);
    }

    
    public Drevo najboljsi_podlist(Drevo list) { // izbere najboljšo potezo za igranje
    	Drevo najboljsi = null;
    	int najboljsi_n = 0;
		for (Drevo podlist : list.sez_listov) {
			int nov_n = podlist.n;
			if (najboljsi == null || nov_n > najboljsi_n) {
				najboljsi = podlist;
				najboljsi_n = nov_n;
			}
		}
		return najboljsi;
    }
}

