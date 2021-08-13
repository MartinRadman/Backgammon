package vodja;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import logika.Igra;
import logika.Igralec;
import logika.Zetoni.Polje;
import Vmesnik.Okno;
import inteligenca.Drevo;
import inteligenca.MonteCarloTreeSearch;

public class Vodja { // vodi potek igre
	
	protected Map<Polje, VrstaIgralca> vrstaIgralca; // nam pove kdo je človek, kdo pa računalnik
	
	public Map<Polje, Igralec> kdoIgra; // vir objektov, ki predstavljata igralca
	
	protected Okno okno;
	
	protected Igra igra = null;
	
	public Vodja(Okno okno, Map<Polje, VrstaIgralca> vrstaIgralca) {
		this.okno = okno;
		this.vrstaIgralca = vrstaIgralca;
	}
		
	public void igramoNovoIgro(String igralec1_ime, String igralec2_ime) { // inicializira novo igro
		Igralec igralec1 = new Igralec(igralec1_ime, Polje.IGRALEC1);
		Igralec igralec2 = new Igralec(igralec2_ime, Polje.IGRALEC2);
	
		igra = new Igra(igralec1, igralec2);
		
		racunalnikovaInteligenca = new MonteCarloTreeSearch(new Drevo(igra), igra.trenutni_igralec().id(), igra, okno);
	}
	
	public void igramo() { // izvede rundo igre
		okno.osvezi_vmesnik();
		switch (igra.stanje()) {
		case ZMAGA1: okno.platno().konecIgre(); return; 
		case ZMAGA2: okno.platno().konecIgre(); return;
		// odhajamo iz metode igramo
		case V_TEKU: 
			Igralec igralec = igra.trenutni_igralec();
			VrstaIgralca vrstaNaPotezi = vrstaIgralca.get(igralec.id());
			switch (vrstaNaPotezi) { // določa, kateri tip igralca je sedaj na vrsti
			case C: 
				break;
			case R:
				igrajRacunalnikovoPotezo();
				break;
			}
		}
	}

	
	public MonteCarloTreeSearch racunalnikovaInteligenca;
	
	public void igrajRacunalnikovoPotezo() {
		// izvede računalnikovo rundo s pomočjo swing workerja
		// swing worker poskrbi, da se umetna inteligenca in vmesnik izvajata v ločenih nitih - pomembno zaradi grafičnih efektov na platnu
		
		/*
		Igra zacetekIgra = igra;
		SwingWorker<int[][], Void> worker = new SwingWorker<int[][], Void> () {
			@Override
			protected int[][] doInBackground() { // določi izbrani potezi
				Drevo[] poteza = racunalnikovaInteligenca.monte_carlo_tree_search();
				int[] poteza1 = poteza[0].k;
				int[] poteza2 = poteza[1].k;
				try {TimeUnit.MICROSECONDS.sleep(10);} catch (Exception e) {};
				return new int[][] {poteza1, poteza2};
			}
			@Override
			protected void done() { // izvede izbrani potezi
				int[] poteza1 = null;
				int[] poteza2 = null;
				int[][] poteza = null;
				try {poteza = get();} catch (Exception e) {};	
				if (igra == zacetekIgra) {
					poteza1 = poteza[0];
					poteza2 = poteza[1];
					okno.platno().poteza(prevedi(poteza1)[0], prevedi(poteza1)[1], okno.platno().igralec_na_potezi);
					okno.platno().poteza(prevedi(poteza2)[0], prevedi(poteza2)[1], okno.platno().igralec_na_potezi);
					okno.osvezi_vmesnik();
					igramo();
				}
			}
		};
		worker.execute();
		*/
		
		Drevo[] poteza = racunalnikovaInteligenca.monte_carlo_tree_search();
		int[] poteza1 = poteza[0].k;
		int[] poteza2 = poteza[1].k;
		okno.platno().poteza(prevedi(poteza1)[0], prevedi(poteza1)[1]);
		okno.platno().poteza(prevedi(poteza2)[0], prevedi(poteza2)[1]);
		igra.odigraj(poteza1[0], poteza1[1]);
		igra.odigraj(poteza2[0], poteza2[1]);
		okno.osvezi_vmesnik();
		igramo();
		
	
	}
		
	public void igrajClovekovoPotezo(int[] poteza) { // metoda, s katero človek izvede potezo
		igra.odigraj(poteza[0], poteza[1]);
		okno.osvezi_vmesnik();
		if (igra.treba_izvesti == 0) igramo();
	}
	
	public Igra igra() {
		return this.igra;
	}
	
	
	private int[] prevedi(int[] orig) { // prevede zapis izvedene poteze iz tipa za logiko v tip za vmesnik
		int[] prevod = new int[2];
		prevod[0] = (orig[0] == -1) ? 26 : 24 - orig[0];
		prevod[1] = (orig[1] == 24) ? ((okno.platno().igralec_na_potezi) ? 25 : 0) : 24 - orig[1];
		return prevod;
	}
}
