package inteligenca;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import logika.Igra;
import logika.Igra.Igralec;
import logika.Igra.Stanje;
import splosno.Koordinati;

public class MonteCarloTreeSearch {
	
	final int casovna_omejitev = 4800; // v milisekundah
	final double c = Math.sqrt(2);
	protected long start = System.currentTimeMillis();
	protected long konec = start + casovna_omejitev;
	
	
	
	protected Drevo koren;
	protected Igralec jaz;
	protected Igra igra;
	
	public MonteCarloTreeSearch(Drevo koren, Igralec jaz, Igra igra) {
		this.koren = koren;
		this.jaz = jaz;
		this.igra = igra;
	}

	
	public Drevo monte_carlo_tree_search() {
	  while (System.currentTimeMillis() < konec) {
	        Drevo list = prehodi(koren); // list = neobiskan node
	        list.je_obiskan = true;
	        int rezultat_simulacije = odigraj_do_konca(list);
	        izpolni_za_nazaj(list, rezultat_simulacije);
	  }
	  return najboljsi_podlist(koren);
	}
	
	public Drevo prehodi(Drevo list) {
		Drevo nov_list = list;
		if (nov_list.sez_listov.size() == 0) return nov_list;
	    while (popolnoma_obiskani_podlisti(nov_list)) {
	    	if (nov_list.sez_listov.size() == 0) return nov_list;
	        nov_list = najboljsi_uct(nov_list);
	        }
	    return izberi_neobiskanega(nov_list.sez_listov); // in case no children are present / node is terminal
	}
	
	public boolean popolnoma_obiskani_podlisti(Drevo list) {
		if (list == null) return true;
		for (Drevo podlist : list.sez_listov) {
			if (podlist.je_obiskan == false) return false;
		}
		return true;
	}
	
	public double izracunaj_uct(Drevo list) {
		if (list == null) return -1000000;
		double vrednost_lista = (double) list.v;
		double obiskanost_lista = (double) list.n;
		double obiskanost_korena = (double) koren.n;
		
		double prvi_clen = vrednost_lista / obiskanost_lista;
		double v_korenu = Math.log(obiskanost_korena / obiskanost_lista);
		double drugi_clen = c * Math.sqrt(v_korenu);
		
		return prvi_clen + drugi_clen;
	}
	
	public Drevo najboljsi_uct(Drevo list) {
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
	
	public Drevo izberi_neobiskanega(List<Drevo> sez_listov) {
		List<Drevo> neobiskani = new ArrayList<Drevo>();
		for (Drevo list : sez_listov) {
			if (!list.je_obiskan) neobiskani.add(list);
		}
		return izberi_nakljucno(neobiskani);
	}
	
	
	public int razdalja_polj(Koordinati a, Koordinati b) {
		int a0 = a.getX();
		int a1 = a.getY();
		int b0 = b.getX();
		int b1 = b.getY();
		int x_razdalja = Math.abs(a0 - b0);
		if (x_razdalja == 0) return Math.abs(a1 - b1);
		return x_razdalja;
	}
	
	public Drevo izberi_nakljucno(List<Drevo> sez_listov) {
		Random rand = new Random();
		return sez_listov.get(rand.nextInt(sez_listov.size()));
	}
	    		
	 	
	public int odigraj_do_konca(Drevo list) {
		napolni_s_podlisti(list, list.igra);
	    while (je_nekoncen(list)) {
	        list = pravilo_igranja(list);
	        napolni_s_podlisti(list, list.igra);
	    }
	    return rezultat(list);
	}
	
	
	
	public void napolni_s_podlisti(Drevo list, Igra kopija_igre) {
		if (list.sez_listov.size() != 0) return;
		else {
			HashSet<Koordinati> moznePoteze = kopija_igre.mnozica_potez();
			List<Drevo> odigrane_poteze = new ArrayList<Drevo>();
			for (Koordinati p: moznePoteze) {
				
				if (list.je_koren()) {
					boolean nadaljuj = true;
					for(Koordinati kamencek : kopija_igre.mnozica_izvedenih_potez()) {
						if (halal(kamencek, p)) nadaljuj = false;;					
					}
					if (nadaljuj) continue;
				}
				
				Igra kopijaIgre = new Igra(kopija_igre);
				kopijaIgre.odigraj(p);
				Drevo poteza = new Drevo(kopijaIgre, p, 0, 0, new ArrayList<Drevo>());
				poteza.s = list;
				odigrane_poteze.add(poteza);
			}
			list.sez_listov = odigrane_poteze;
		}
		
	}
	
	public boolean halal(Koordinati kamencek, Koordinati p) {
		if (Math.abs(kamencek.getX() - p.getX()) != Math.abs(kamencek.getY() - p.getY())) return false;
		if (razdalja_polj(kamencek, p) <= 2) return true;
		return false;
	}
	
	public boolean je_nekoncen(Drevo list) {
		return (!(list.sez_listov.size() == 0));
	}
	
	public Drevo pravilo_igranja(Drevo list) {
		return izberi_nakljucno(list.sez_listov);
	}
	
	public int rezultat(Drevo list) {
		int rezultat = -2;
		Igra igra = list.igra;
		Stanje stanje = igra.stanje();
		switch(stanje) {
		case ZMAGA_O: rezultat = (jaz == Igralec.O) ? 1 : -1; break;
		case ZMAGA_X: rezultat = (jaz == Igralec.X) ? 1 : -1; break;
		case NEODLOCENO: rezultat = 0; break;
		case V_TEKU: new Error(); break;
		default: new Error(); break;
		}
		if (rezultat == -2) new Error();
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

    
    public Drevo najboljsi_podlist(Drevo list) {
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

