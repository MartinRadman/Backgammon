package Vmesnik;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import logika.Igra;




@SuppressWarnings("serial")
public class Okno extends JFrame implements ActionListener{
	
	protected Platno platno;
	// imena igralcev ()
	protected static String ime_igralca_1 = "Igralec 1";
	protected static String ime_igralca_2 = "Igralec 2";
	
	private JMenuItem menuBarvaIgralca1;
	private JMenuItem menuBarvaIgralca2;
	
	private JMenuItem menuOsnovnaIgra;
	private JMenuItem menuIgraProtiRacunalniku;
	private JMenuItem menuIgraPoMeri;

	// Nredimo in oblikujemo okno
	public Okno(String igralec1, String igralec2) {
		super();
		// ime okna
		setTitle("Backgammon");
		
		// pripravljeno za vodjo
		/* Map<Igralec, VrstaIgralca> vrstaIgralca = new EnumMap<Igralec, VrstaIgralca>(Igralec.class);
		vrstaIgralca.put(Igralec.O, igralec1);
		vrstaIgralca.put(Igralec.X, igralec2);
		
		Map<Igralec, KdoIgra> kdoIgra = new EnumMap<Igralec, KdoIgra>(Igralec.class);
		kdoIgra.put(Igralec.O, new KdoIgra(igralec1_ime));
		kdoIgra.put(Igralec.X, new KdoIgra(igralec2_ime));
		
		Vodja vodja = new Vodja(this, vrstaIgralca, kdoIgra);
		
		vodja.igramoNovoIgro(sirina_igralnega_polja, visina_igralnega_polja, igralec1_ime, igralec2_ime);
		
		igra = vodja.igra();
		
		vodja.igramo();
		
		(Uporabljena koda iz Gomokuja, ko bo narjeno se jo prilagodi na tisto iz Backgammona)
		
		*/
		
		
		// nastavimo platno in osnovne dimenzije
		Igra igra = new Igra();
		platno = new Platno(800, 800, this, igra);
		add(platno);
		//"aktiviramo" zaèetno pozicijo
		platno.zacetnaPozicija();
		
		// naredimo nekaj menijev, iz katerih lahko recimo izberemo barvo igralca ipd.
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		// dodamo meni igra, z podmenijem nova igra, ki pa bo ponudila dve možnosti, nova osnova igra in nova igra proti raèunalniku
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
		
		
	}
	
	// metoda, ki zažnee okno vendar ne sprejme nobenih argumentov
	public Okno() {
		this(ime_igralca_1, ime_igralca_2);
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
	
	
	// nastavimo kaj se zgodi, ko pritisnemo na doloèen item znotraj menija
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource(); 
		
		// èe izberemo spremei barvo igralca 1 nam odpre okno, kjer lahko zamenjamo barvo igralca 1
		if (source == menuBarvaIgralca1) {
			Color barva = JColorChooser.showDialog(this, "Izberi barvo igralca", platno.barva_igralca_1);
			if (barva != null) {
				platno.barva_igralca_1 = barva;
				platno.repaint();
			}
		}
		
		// èe izberemo spremei barvo igralca 1 nam odpre okno, kjer lahko zamenjamo barvo igralca 2
		if (source == menuBarvaIgralca2) {
			Color barva = JColorChooser.showDialog(this, "Izberi barvo igralca", platno.barva_igralca_2);
			if (barva != null) {
				platno.barva_igralca_2 = barva;
				platno.repaint();
			}
		}
		
		// nova igra zažene novo igro (oèitno)
		if (source == menuOsnovnaIgra) {
			platno.novaIgra();
			platno.repaint();
		}
		
		// nova igra proti raèunalniku zažene novo igro proti raèunalniku 
		if (source == menuIgraProtiRacunalniku) {
			platno.launcher();
		}
		
		// igra po meri, èe bo kaj za po meri ampak zaenkrat ne kaže na to možnost
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

