package Vmesnik;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import logika.Igra;
import logika.Igralec;
import vodja.VrstaIgralca;
import logika.Zetoni.Polje;
import vodja.Vodja;




@SuppressWarnings("serial")
public class Okno extends JFrame implements ActionListener{
	
	protected Platno platno;
	// imena igralcev ()
	protected static String ime_igralca_1 = "Igralec 1";
	protected static String ime_igralca_2 = "Igralec 2";
	
	public Vodja vodja;
	
	private JMenuItem menuBarvaIgralca1;
	private JMenuItem menuBarvaIgralca2;
	
	private JMenuItem menuOsnovnaIgra;
	private JMenuItem menuIgraProtiRacunalniku;
	private JMenuItem menuIgraPoMeri;

	// Nredimo in oblikujemo okno
	public Okno(String igralec1, String igralec2, VrstaIgralca vrsta1, VrstaIgralca vrsta2) {
		super();
		// ime okna
		setTitle("Backgammon");
		
		Igra igra = new Igra();
		
		// pripravljeno za vodjo
		Map<Polje, VrstaIgralca> vrstaIgralca = new EnumMap<Polje, VrstaIgralca>(Polje.class);
		vrstaIgralca.put(Polje.IGRALEC1, vrsta1);
		vrstaIgralca.put(Polje.IGRALEC2, vrsta2);
		
		vodja = new Vodja(this, vrstaIgralca);
		vodja.igramoNovoIgro(igralec1, igralec2);
		igra = vodja.igra();
		
		Map<Polje, Igralec> kdoIgra = new EnumMap<Polje, Igralec>(Polje.class);
		kdoIgra.put(Polje.IGRALEC1, igra.igralec1());
		kdoIgra.put(Polje.IGRALEC2, igra.igralec2());
		
		boolean ai = false;
		
		if (vrsta2 == VrstaIgralca.R) ai = true;
		
		
		
		// nastavimo platno in osnovne dimenzije
		platno = new Platno(800, 800, this, igra, ai);
		platno.okno_igra = this;
		add(platno);
		//"aktiviramo" začetno pozicijo
		platno.zacetnaPozicija();
		
		// naredimo nekaj menijev, iz katerih lahko recimo izberemo barvo igralca ipd.
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		// dodamo meni igra, z podmenijem nova igra, ki pa bo ponudila dve možnosti, nova osnova igra in nova igra proti računalniku
		JMenu menuIgra = dodajMenu(menubar, "Igra");
		JMenu menuNovaIgra = dodajMenuNaMenu(menuIgra, "Nova igra");
		
		menuOsnovnaIgra = dodajMenuItem(menuNovaIgra, "Nova osnovna igra");
		menuIgraProtiRacunalniku = dodajMenuItem(menuNovaIgra, "Nova igra proti računalniku");
		// v primeru, da smo na platnu prižgali igra_po_meri se poleg pokaže tudi igra po meri
		if (platno.igra_po_meri) {
			menuIgraPoMeri = dodajMenuItem(menuNovaIgra, "Nova igra po meri ...");
		}
		
		// dodajmo še nov meni z možnostimi prilagoditev, kjer lahko nastavimo barvo igralcev
		JMenu menuPrilagoditve = dodajMenu(menubar, "Prilagoditve");
		JMenu menuBarvaIgralcev = dodajMenuNaMenu(menuPrilagoditve, "Spremeni barvo igralcev ...");
		
		menuBarvaIgralca1 = dodajMenuItem(menuBarvaIgralcev, "Barva igralca " + ime_igralca_1 + " ..."); 
		menuBarvaIgralca2 = dodajMenuItem(menuBarvaIgralcev, "Barva igralca " + ime_igralca_2 + " ...");
		
		vodja.igramo();
		
		
	}
	
	// metoda, ki zažnee okno vendar ne sprejme nobenih argumentov
	public Okno() {
		this(ime_igralca_1, ime_igralca_2, VrstaIgralca.C, VrstaIgralca.R);
	}
	

	


	

	// spisali smo par metod, da bomo lažje naredili nove menije oz. nove podmenije in predmete? (item)
	public JMenu dodajMenu(JMenuBar menubar, String naslov) {
		JMenu menu = new JMenu(naslov);
		menubar.add(menu);
		return menu;	
	}
	
	public JMenuItem dodajMenuItem(JMenu menu, String naslov) {
		JMenuItem menuitem = new JMenuItem(naslov);
		menu.add(menuitem);
		menuitem.addActionListener(this);
		return menuitem;
	}
	
	public JMenu dodajMenuNaMenu(JMenu zgornji, String naslov) {
		JMenu spodnji = new JMenu(naslov);
		zgornji.add(spodnji);
		return spodnji;
	}
	
	
	// nastavimo kaj se zgodi, ko pritisnemo na določen item znotraj menija
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource(); 
		
		// če izberemo spremei barvo igralca 1 nam odpre okno, kjer lahko zamenjamo barvo igralca 1
		if (source == menuBarvaIgralca1) {
			Color barva = JColorChooser.showDialog(this, "Izberi barvo igralca", platno.barva_igralca_1);
			if (barva != null) {
				platno.barva_igralca_1 = barva;
				platno.repaint();
			}
		}
		
		// če izberemo spremei barvo igralca 1 nam odpre okno, kjer lahko zamenjamo barvo igralca 2
		if (source == menuBarvaIgralca2) {
			Color barva = JColorChooser.showDialog(this, "Izberi barvo igralca", platno.barva_igralca_2);
			if (barva != null) {
				platno.barva_igralca_2 = barva;
				platno.repaint();
			}
		}
		
		// nova igra zažene novo igro (očitno)
		if (source == menuOsnovnaIgra) {
			platno.novaIgra("Igralec 1", "Igralec 2", VrstaIgralca.C, VrstaIgralca.C);
			platno.repaint();
		}
		
		// nova igra proti računalniku zažene novo igro proti računalniku 
		if (source == menuIgraProtiRacunalniku) {
			platno.novaIgra("Igralec 1", "Igralec 2", VrstaIgralca.C, VrstaIgralca.R);
		}
		
		// igra po meri, če bo kaj za po meri ampak zaenkrat ne kaže na to možnost
		if (source == menuIgraPoMeri) {
			platno.launcher();
		}
	       
	        
		    
	}
	
	public void osvezi_vmesnik() {
		platno.repaint();
	}
	
	public Platno platno() {
		return platno;
	}
	
	
	}

