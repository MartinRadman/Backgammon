package vodja;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import inteligenca.Inteligenca;
import logika.Igra;
import logika.Igra.Igralec;
import splosno.KdoIgra;
import splosno.Koordinati;
import vmesnik.Okno;

public class Vodja {	
	
	protected Map<Igralec,VrstaIgralca> vrstaIgralca;
	protected Map<Igralec,KdoIgra> kdoIgra;
	
	protected Okno okno;
	
	protected Igra igra = null;
	
	protected boolean clovekNaVrsti = false;
	
	public Vodja(Okno okno, Map<Igralec, VrstaIgralca> vrstaIgralca, Map<Igralec, KdoIgra> kdoIgra) {
		this.okno = okno;
		this.vrstaIgralca = vrstaIgralca;
		this.kdoIgra = kdoIgra;
	}
		
	public void igramoNovoIgro(int x, int y, String igralec1_ime, String igralec2_ime) {
		igra = new Igra(x, y, igralec1_ime, igralec2_ime);
	}
	
	public void igramo() {
		okno.osvezi_vmesnik();
		switch (igra.stanje()) {
		case ZMAGA_O: okno.konec_igre(true); return; 
		case ZMAGA_X: okno.konec_igre(true); return;
		case NEODLOCENO: okno.konec_igre(false); return;
		// odhajamo iz metode igramo
		case V_TEKU: 
			Igralec igralec = igra.na_potezi();
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

	
	public Inteligenca racunalnikovaInteligenca = new Inteligenca();
	
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
