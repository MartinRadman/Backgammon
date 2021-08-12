package vodja;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import logika.Igra;
import logika.Igralec;
import logika.Zetoni.Polje;
import Vmesnik.Okno;
import inteligenca.MonteCarloTreeSearch;

public class Vodja {	
	
	protected Map<Igralec,VrstaIgralca> vrstaIgralca;
	
	protected Okno okno;
	
	protected Igra igra = null;
	
	protected boolean clovekNaVrsti = false;
	
	public Vodja(Okno okno, Map<Igralec, VrstaIgralca> vrstaIgralca) {
		this.okno = okno;
		this.vrstaIgralca = vrstaIgralca;
	}
		
	public void igramoNovoIgro(String igralec1_ime, String igralec2_ime) {
		Igralec igralec1 = new Igralec(igralec1_ime, Polje.IGRALEC1);
		Igralec igralec2 = new Igralec(igralec2_ime, Polje.IGRALEC2);
	
		igra = new Igra(igralec1, igralec2);
	}
	
	public void igramo() {
		okno.osvezi_vmesnik();
		switch (igra.stanje()) {
		case ZMAGA1: okno.platno().konecIgre(); return; 
		case ZMAGA2: okno.platno().konecIgre(); return;
		// odhajamo iz metode igramo
		case V_TEKU: 
			Igralec igralec = igra.trenutni_igralec();
			VrstaIgralca vrstaNaPotezi = vrstaIgralca.get(igralec);
			switch (vrstaNaPotezi) {
			case C: 
				clovekNaVrsti = true;
				break;
			case R:
				clovekNaVrsti = false;
				igrajRacunalnikovoPotezo();
				break;
			}
		}
	}

	
	public MonteCarloTreeSearch racunalnikovaInteligenca = new MonteCarloTreeSearch();
	
	public void igrajRacunalnikovoPotezo() {
		/*
		Koordinati poteza = racunalnikovaInteligenca.izberiPotezo(igra);
		
		igra.odigraj(poteza);
		okno.odigraj(poteza);
		okno.osvezi_vmesnik();
		igramo();
		*/
		
		
		Igra zacetekIgra = igra;
		SwingWorker<Koordinati, Void> worker = new SwingWorker<Koordinati, Void> () {
			@Override
			protected Koordinati doInBackground() {
				Koordinati poteza = racunalnikovaInteligenca.izberiPotezo(igra);
				try {TimeUnit.MICROSECONDS.sleep(10);} catch (Exception e) {};
				return poteza;
			}
			@Override
			protected void done() {
				Koordinati poteza = null;
				try {poteza = get();} catch (Exception e) {};	
				if (igra == zacetekIgra) {
					igra.odigraj(poteza);
					okno.odigraj(poteza);
					okno.osvezi_vmesnik();
					igramo();
				}
			}
		};
		worker.execute();
		
		
		
	
	}
		
	public void igrajClovekovoPotezo(Koordinati poteza) {
		if (igra.odigraj(poteza)) clovekNaVrsti = false;
		okno.osvezi_vmesnik();
		igramo();
	}
	
	public Igra igra() {
		return this.igra;
	}
	
	public boolean clovekNaVrsti() {
		return clovekNaVrsti;
	}
}
