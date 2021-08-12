package Vmesnik;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import Vmesnik.Okno;



@SuppressWarnings("serial")
public class Platno extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
	
	// Najprej naredimo prostor za definiranje vseh spremenljivk, ki jih bomo potrebovali za vmesnik

	protected boolean osnovni_meni = true;
	protected boolean aktivna_igra = false;
	protected boolean diceRoll = false;
	protected boolean igra_po_meri = false;
	protected boolean konec_igre = false;
	protected int v_polja;
	protected int s_polja;
	protected int velikost_pisave;
	protected int start;
	protected int dolzina_naslova;	
	protected int n_polovica;
	protected int s_rob;
	protected int v_rob;
	protected int s_trikotnika;
	protected int v_trikotnika;
	protected Okno okno_igra;
	protected int start_met_x;
	protected int start_met_y;
	protected int dim_met;
	protected int kocka1 = 1;
	protected int kocka1_poteze = 0;
	protected int kocka2 = 1;
	protected int kocka2_poteze = 0;
	protected int dim_zeton;
	protected float faktor_zeton_velikost = (float) 0.75;
	protected int debelina_puscice = 5;
	

	
	protected boolean pokazi_poteze = false;
	protected boolean igralec_na_potezi = true;
	protected boolean med_potezo = false;
	protected boolean poteze = false;
	
	
	protected HashMap<String, List<Integer>> plosca;
	protected HashMap<Integer, Integer> plosca_igralec_1;
	protected HashMap<Integer, Integer> plosca_igralec_2;
	protected List<Integer> zacetek_igralec_1;
	protected List<Integer> zacetek_igralec_2;
	protected List<HashMap<Integer, Integer>> seznam_igralcev = new ArrayList<HashMap<Integer, Integer>>();
	protected List<Integer> mozne_poteze;
	protected List<Integer> mozni_zacetki;
	protected int aktivno_polje = 250;
	protected int af;
	
	int countdown = 20;
	
	
	protected Color barva_igralca_1 = Color.BLACK;
	protected Color barva_igralca_2 = Color.RED;
	protected Color barva_igralca_na_potezi = barva_igralca_1;
	protected Color barva_ovala_1 = new Color(51,51,51);
	protected Color barva_ovala_2 = new Color(51,51,51);
	protected Color barva_ovala_3 = new Color(51,51,51);
	protected Color barva_ozadja = new Color(230, 170, 110);
	protected Color barva_pisave = Color.YELLOW;
	protected Color aktivna_barva = Color.RED;
	protected Color zmagovalna_barva = new Color(255,215,0);
	protected Color barva_naslova = new Color(255, 255, 51);
	protected Color barva_stevilk = new Color(255, 255, 51);
	protected Color barva_kock = new Color(255, 255, 51);
	protected Color barva_med_metom = Color.GREEN;
	protected float faktor_velikosti_stevcev_polj = (float) 0.75;
	
	
	// definiramo platno, zato da se nam bodo zadeve prikazovali
	
	public Platno(int sirina, int visina, Okno okno) {
		super();
		setPreferredSize(new Dimension(sirina, visina));
		
		
		

		
		addMouseListener(this); 
		addMouseMotionListener(this);
		addKeyListener(this);
		setFocusable(true);
	}
	
	// pod paintcomponent bomo zapisali vse kar želimo, da se nam ob doloèenih trenutkih prikaže
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Rectangle r = this.getBounds(); // Najprej pridobimo dimenzije okna
		// nastavimo osnovne dolžine, ki jih bomo za prikaz tako ali drugaèe uporabljali. Naj opomnim, da je vse prilagojeno na sliko, ki je pravzaprav zgolj slika polja za Backgammon
		v_polja = r.height;  // v_pola je okrajšava za višino polja in jo preberemo direktno iz višine "r"
		s_polja = r.width;   // podobno velja za s_polja, ki je okrajšava za širino polja 
		s_rob = (int) (s_polja * 0.055); // s_rob je okrajšava za širino roba, keoficient je izmerjen z nekim drugim pripomoèkom a je izmerjen precej natanèno
		s_trikotnika = (int) (s_polja * 0.07); // s_trikotnika je okrajšava za širino trikotnika, prav tako je razmerje med širino trioktnika in širino polja izmerjeno
		n_polovica = (int) (s_polja * 0.03); // n_polovica je širina vmesne preèke, postopek merjenja je enak kot pri prejšnih dveh
		v_rob = (int) (v_polja * 0.055); // v_rob je okrajšava za višino polja
		v_trikotnika = (int) (v_polja * 0.337); // v_trikotnika pa višina trikotnika
		
		
		// najrej narišemo ozadje, da lahko potem èez rišemo še ostale zadeve, ta bo vedno v ozadju, ne glede na del igre, v prvem delu bo sledilo kot ozadje, v drugem pa kot igralno polje
		ImageIcon img = new ImageIcon("Extras/BGv1.jpg");
		g.drawImage(img.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
		
		// glede na vrednost osnovnega menija (true / false) bomo prikazali osnonvi meni oz. "launcher" ali pa polje z igro
		if (osnovni_meni) {
			
			
			// nato narišemo naslov
	        String naslov = "BACKGAMMON";
			g.setColor(barva_naslova);
			velikost_pisave = (int) Math.min(0.1 * s_polja, 0.1 * v_polja);
			g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_pisave));
			
			dolzina_naslova = (int) g.getFontMetrics().getStringBounds(naslov, g).getWidth();
			start = (int) Math.round(s_polja / 2) - dolzina_naslova / 2;

			g.drawString(naslov, start, (int) Math.round(0.2 * v_polja));			
			
			// sedaj pa bomo odvisno od tega ali je igra_po_meri true ali false prikazali dva razlièna menija
			// za zaèetek najprej nastavimo kaj bo pisalo na gumbih, kjer si bomo lahko izbrali igro
			String gumb1 = "NOVA IGRA";
			String gumb2 = "NOVA IGRA PROTI RAÈUNALNIKU";
			String gumb3 = "NOVA IGRA PO MERI";
			
			// naslednja dva primera if bosta na videz zelo podobna in res sta si, vendar sta razlièna, ker razlièno razoredita gumbe
			// teoretièno bi se dalo kodo skrajšati vendar bi to lahko škodilo preglednosti v primeru prilagajanja
			if (!igra_po_meri) {
				
				//v primeru ko igre po meri ni, potrebujemo samo dva gumba, ki sta nižje, najprej pod (1) nareišemo gumba, potem pod (2) napišemo èez, kar na gumbih piše
				
				//(1)
				g.setColor(barva_ovala_1);
				g.fillOval(start, (int) Math.round(0.4 * v_polja) - velikost_pisave / 2 + v_rob, dolzina_naslova, velikost_pisave / 2);
				
				g.setColor(barva_ovala_2);
				g.fillOval(start, (int) Math.round(0.4 * v_polja) + velikost_pisave / 2 + v_rob, dolzina_naslova, velikost_pisave / 2);
				
				//(2)
				g.setColor(barva_pisave);
				int velikost_pisave1 = (int) velikost_pisave / 3;
				g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_pisave1));
				int dolzina_naslova1 = (int) g.getFontMetrics().getStringBounds(gumb1, g).getWidth();
				int start1 = (int) Math.round(s_polja / 2) - dolzina_naslova1 / 2;
				g.drawString(gumb1, start1 , (int) Math.round(0.4 * v_polja) - velikost_pisave1 / 2 + v_rob);
				
				int velikost_pisave2 = velikost_pisave / 5;
				g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_pisave2));
				int dolzina_naslova2 = (int) g.getFontMetrics().getStringBounds(gumb2, g).getWidth();
				int start2 = (int) Math.round(s_polja / 2) - dolzina_naslova2 / 2;
				g.drawString(gumb2, start2, (int) Math.round(0.4 * v_polja) + velikost_pisave - velikost_pisave2 + v_rob);
			}
			
			if (igra_po_meri) {
				
				//v primeru ko je igra po meri vkluèena, se polje izriše malce drugaèe, a na enak naèin
				
				//(1)
				g.setColor(barva_ovala_1);
				g.fillOval(start, (int) Math.round(0.4 * v_polja) - velikost_pisave / 2, dolzina_naslova, velikost_pisave / 2);
				
				g.setColor(barva_ovala_2);
				g.fillOval(start, (int) Math.round(0.4 * v_polja) + velikost_pisave / 2, dolzina_naslova, velikost_pisave / 2);
				
				g.setColor(barva_ovala_3);
				g.fillOval(start, (int) Math.round(0.4 * v_polja) + 3 * velikost_pisave / 2, dolzina_naslova, velikost_pisave / 2);
				
				//(2)
				g.setColor(barva_pisave);
				int velikost_pisave1 = (int) velikost_pisave / 3;
				g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_pisave1));
				int dolzina_naslova1 = (int) g.getFontMetrics().getStringBounds(gumb1, g).getWidth();
				int start1 = (int) Math.round(s_polja / 2) - dolzina_naslova1 / 2;
				g.drawString(gumb1, start1, (int) Math.round(0.4 * v_polja) - velikost_pisave1 / 2);
				
				int velikost_pisave2 = velikost_pisave / 5;
				g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_pisave2));
				int dolzina_naslova2 = (int) g.getFontMetrics().getStringBounds(gumb2, g).getWidth();
				int start2 = (int) Math.round(s_polja / 2) - dolzina_naslova2 / 2;
				g.drawString(gumb2, start2, (int) Math.round(0.4 * v_polja) + velikost_pisave - velikost_pisave2);
			
			
				int velikost_pisave3 =velikost_pisave / 4;
				g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_pisave3));			
				int dolzina_naslova3 = (int) g.getFontMetrics().getStringBounds(gumb3, g).getWidth();
				int start3 = (int) Math.round(s_polja / 2) - dolzina_naslova3 / 2;
				g.drawString(gumb3, start3, (int) Math.round(0.4 * v_polja) + 2 * velikost_pisave - velikost_pisave3 / 2);
			
			}
			
		}
		
		if (aktivna_igra) {
			
			//ko enkrat zapustimo osnovni zaslo aka. launcher se igra spremeni v aktivno (glej MouseClicked)
			
			//najprej bomo oštevilèili polja (trikotnike):
			
	        velikost_pisave = (int) Math.min(0.05 * s_polja, 0.05 * v_polja); //nastavimo velikost pisave (to je samo vmesni korak, ki pripomorje k preglednosti) 
	        int velikost_stevilk = (int) (velikost_pisave * faktor_velikosti_stevcev_polj); // sedaj pa s pomoèjo prejšnega koraka nastavimo željeno velikost številk
	        //Opomba: faktor_velikosti_stevcev_polj je konstanta, ki pa je sicer definirana na vrhu, tako da lažje spreminjamo, v primeru, da se odloèimo, da nam ne bi bila velikost pisave povsem všeè
	        g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk)); // nastavimo še tip (naèin?) pisave 
	        
	        //najprej napišemo zgornjih 12 številk
	        for (int i=1; i < 13; i++) {
	        	int start_i_y = v_polja / 20; // nastavimo visino, ta bo za vse stevilke enaka
	        	int start_i_x = 0; // nastavimo še širino, kjer se bo številka izpisala, tega bomo v naslednjih par korakih z nekaj raèunanja pravilno nastavili, mi pa smo mu za zaèetek pripisali neko osnovno vrednost v velikosti 0
	        	
	        	//najprej pogledamo prvi kvadrant (prvih 6 in drugih 6 moramo obravnavati posebej zaradi vmesne linije, ki naredi zamik)
	        	if (i <= 6) {
	        		start_i_x = s_polja - s_rob -  i * s_trikotnika * 103 / 100 + s_trikotnika / 3; // z nekaj regulacijami in poskušanjem smo našli najbolj optimalno pozicijo za prvih 6 številk
	        	} 
	        	else {
	        		start_i_x = s_polja - s_rob - n_polovica - i * s_trikotnika * 103 / 100 + s_trikotnika / 5 * 2; // podobno smo naredili za pozicije drugih 6 številk
	        	}
	        	
	        	// ko smo pravilno nastavili pozicijo števila, ki ga hoèemo izpisati, nastavimo še potrebne lastnosti g-ja.
	        	g.setColor(barva_stevilk);
	        	String stevilka = "" + i;
	        	// številko sedaj izrišemo na željeno mesto
	        	g.drawString(stevilka, start_i_x, start_i_y);
	        	
	        	// to ponovimo 12x
	        	}
	        
	        // še spodnjih 12 številk, postopek analogen zgornjemu, zato ne bo opisan ponovno, le manjša opomba, da so vrednosti razlièe od zgoraj, saj so vse spodnje številke dvomestne in je tako prilagajanje bilo malce drugaèno kot zgoraj
	        for (int i=1; i < 13; i++) {
	        	int start_i_y = v_polja - v_rob / 2 + v_rob / 6;
	        	int start_i_x = 0;
	        	int j = 13 - i;
	        	if (i <= 6) {
	        		start_i_x = s_polja - s_rob -  j * s_trikotnika * 103 / 100;
	        	}
	        	else {
	        		start_i_x = s_polja - s_rob - j * s_trikotnika * 103 / 100 + s_trikotnika / 3;
	        	}
	        	g.setColor(barva_stevilk);
	        	String stevilka = "" + (i + 12);
	        	g.drawString(stevilka, start_i_x, start_i_y);
	        	
	        }
	        // sedaj imamo oznaèena vsa polja s številko
	        
	        
	        // dodajmo kocke, najprej natavimo lastnosti, ki jih želimo, da jih kocke imajo (predvsem barva), potem pa doloèimo željeno pozicijo, ki je v našem primeru nekje na levi strani polja
	        g.setColor(barva_kock);
	        int start_kocka_x = s_rob + 2 * s_trikotnika;
	        int dim_kocke = Math.min(s_rob, v_rob) * 3 / 2; 
	        int start_kocka_y = v_polja / 2 - dim_kocke / 2;
	        
	        // ko smo enkrat doloèili vse kar smo želeli, izrišemo kocke
	        g.fillRoundRect(start_kocka_x, start_kocka_y, dim_kocke, dim_kocke, dim_kocke / 4, dim_kocke / 4);
	        g.fillRoundRect(start_kocka_x + dim_kocke + s_trikotnika / 5, start_kocka_y, dim_kocke, dim_kocke,dim_kocke / 4, dim_kocke / 4);
	        g.setColor(Color.BLACK);
	        g.drawRoundRect(start_kocka_x, start_kocka_y, dim_kocke, dim_kocke, dim_kocke / 4, dim_kocke / 4);
	        g.drawRoundRect(start_kocka_x + dim_kocke + s_trikotnika / 5, start_kocka_y, dim_kocke, dim_kocke, dim_kocke / 4, dim_kocke / 4);
	        //Opomba: Zaenkrat so izrisane prazne kocke, pike metov bodo dopisane naknadno
	        
	        // dodamo tudi gumb, s katerim se bo kocke vrglo
	        start_met_x = s_rob + s_trikotnika;
	        dim_met = Math.min(s_rob, v_rob); 
	        start_met_y = v_polja / 2 - dim_met / 2;
	        
	        g.setColor(barva_igralca_na_potezi);
    		g.fillRoundRect(start_met_x, start_met_y, dim_met, dim_met, dim_met /4, dim_met /4);
    		
    		g.setColor(Color.BLACK);
    		g.drawRoundRect(start_met_x, start_met_y, dim_met, dim_met, dim_met / 4, dim_met / 4);
	        
    		// sedaj bomo izpisali število pik na posamezni kocki
	        String kocka1 = "" + this.kocka1;
	        String kocka2 = "" + this.kocka2;
	        g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
	        g.drawString(kocka1, start_kocka_x + dim_kocke* 5 / 16, start_kocka_y + dim_kocke - dim_kocke / 6);
	        g.drawString(kocka2, start_kocka_x + dim_kocke + s_trikotnika / 5 + dim_kocke * 5/ 16, start_kocka_y + dim_kocke - + dim_kocke / 6);
	        
	        // hkrati pa bomo v primeru da je nastavljeno pokazi_poteze, kar dobesedno pomeni "met je bil izveden, sedaj igralec premika plošèke" 
	        
	        // naslednji razdelek nam bo izpisal zadeve, ki se prikažejo samo takrat, ko je igralec dejansko na potezi, namreè pike na kockah ostaneju tudi po tem, ko je igralec že konèal in ostanejo vse dokler drugi igralec ne vrže ponovno
	        if (pokazi_poteze) {
	        	// spodnjih nekja vrstic najprej pokaže koliko potez za met doloèene kocke nam je še ostalo, ponavadi gre za enice, ki se po premaknji potezi spremenijo v 0 
	        	g.setColor(barva_stevilk);
	        	g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk));
	        	g.drawString("Poteze:", start_met_x - dim_kocke / 2, start_met_y - dim_kocke / 4);
	        	g.drawString("" + kocka1_poteze, start_kocka_x + dim_kocke * 6 / 16 , start_kocka_y - dim_kocke / 6 );
	        	g.drawString("" + kocka2_poteze, start_kocka_x + dim_kocke + s_trikotnika / 5 + dim_kocke * 6/ 16, start_kocka_y  - dim_kocke / 6);
	        	
	        	// med_potezo je zanimiv, kriterij, ki sicer zelo redko ni aktivne hkrati z pokazi_poteze pa vendar se vse to dogaja pod njim:
	        	if (med_potezo) {
	        		// najprej se gumb za met obarva v skladu z nastavljeno barva_med_metom, to igralcem pove, da je igra v fazi potez in ne meta, na nek naèin si lahko prestavljamo to, kot sporoèilo, da je gumb za met takrat "zaklenjen"
	        		g.setColor(barva_med_metom);
	        		g.fillRoundRect(start_met_x, start_met_y, dim_met, dim_met, dim_met /4, dim_met /4);
	        		g.setColor(Color.BLACK);
	        		g.drawRoundRect(start_met_x, start_met_y, dim_met, dim_met, dim_met / 4, dim_met / 4);
	        		
	        		// ker pa s tem, ko se gumb zaklene in prebarva zgubimo podatek o tem kdo je na vrsti, se poleg gumba za met pojavi še žeton v barvi igralca na potezi
	        		g.setColor(barva_igralca_na_potezi);
	        		g.fillOval(s_rob + s_trikotnika / 2 - dim_met / 2, start_met_y, dim_met, dim_met);
	        		
	        		//poleg tega se na desni strani kock pojavi pušèica, ki kaže v katero smer igralec na potezi igra (èe sluèajno pozabi)
	        		Graphics2D g2d = (Graphics2D)g;
	        		g2d.setStroke(new BasicStroke(debelina_puscice));
	        		g2d.setColor(barva_igralca_na_potezi);
	        		
	        		//pušèica v obliki podrte èrke U je sestavljena iz petih èrt: 
	        		
	        		// osnovne tri ostanejo enake in na istih mestih, le barva se spremeni
	        		// zgornja 
	        		g2d.drawLine(start_kocka_x + 2 * dim_kocke + s_trikotnika / 2, start_kocka_y + dim_kocke / 6, start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4  , start_kocka_y + dim_kocke / 6);
	        		// navpicna
	        		g2d.drawLine(start_kocka_x + 2 * dim_kocke + s_trikotnika / 2, start_kocka_y + dim_kocke / 6, start_kocka_x + 2 * dim_kocke +  s_trikotnika / 2 , start_kocka_y +  5 * dim_kocke / 6);
	        		// spodnja
	        		g2d.drawLine(start_kocka_x + 2 * dim_kocke + s_trikotnika / 2, start_kocka_y + 5 * dim_kocke / 6, start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4  , start_kocka_y + 5 * dim_kocke / 6);
	        		
	        		//preostavli dve, tvorita konico pušèice in sta na nasprotnih repih pušèice
	        		//spodnji rep
	        		if (igralec_na_potezi) {
	        			g2d.drawLine(start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4  , start_kocka_y + 5 * dim_kocke / 6,  start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4 - dim_kocke / 4 , start_kocka_y + 5 * dim_kocke / 6 - dim_kocke / 4);
	        			g2d.drawLine(start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4  , start_kocka_y + 5 * dim_kocke / 6,  start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4 - dim_kocke / 4 , start_kocka_y + 5 * dim_kocke / 6 + dim_kocke / 4);
	        		}
	        		//zgornji rep
	        		if (!igralec_na_potezi) {
	        			g2d.drawLine(start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4  , start_kocka_y + dim_kocke / 6, start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4 - dim_kocke / 4 , start_kocka_y + dim_kocke / 6 - dim_kocke / 4);
	        			g2d.drawLine(start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4  , start_kocka_y + dim_kocke / 6, start_kocka_x + 2 * dim_kocke + 5 * s_trikotnika / 4 - dim_kocke / 4 , start_kocka_y + dim_kocke / 6 + dim_kocke / 4);
	        		}
	        	}
	        }
	        
	        
	        // o tu naprej bomo izrisevali pozicionirane žetone
	        
	        //najprej v seznam igralcev dodamo plosèo posameznega igralca (le-ta se spreminja, ko plošèke premikamo, zato jo moramo vpeljati vedno znova)
	        seznam_igralcev.add(plosca_igralec_1);
	        seznam_igralcev.add(plosca_igralec_2);
	        
	        // doloèimo velikost žetona, to lahko prav tako kasneje spreminjamo na vrhu
	        int dim_zeton = (int) ((int) dim_kocke * faktor_zeton_velikost); 
	        
	        /* lotimo se izrisevanja žetonov:
	         * 
	         * Žetone bomo risali od roba igralnega polja proti notranjosti
	         * V primeru, da bo æetonov veè kot 5 bomo vse nabasali v en kup, nato pa zraven kupa zapisali koliko žetonov sestavja ta stolp 
	         *  
	         */
	        
	        // seveda moramo za oba igralca narisati, tako da se spremhodimo èez seznam_igralcev, najprej narišemo za enega, nato èe za drugega
	        for (HashMap<Integer, Integer> seznam : seznam_igralcev) { 
	        	// potem za vsako polje preverimo koliko žetonov moramo tamo narisati in pa na katerm mestu
	        	for (int key : seznam.keySet()) {
	        		int polje = seznam.get(key);
	        		
	        		
	        		
	        		// plosca_igralec_x vsebuje informacije za vseh 24 polj, potem pa se informacije o izloèenih plošèkih, pod številko 26, in o plošèkih iz igre (0 ali 25), ter kdo je lastnik te ploèe (100) 
	        		// v primeru da plošèa pripada igralcu ena, nastavimo barvo na barva_igralca_1
	        		if(seznam.get(100) == 1) {
	        			g.setColor(barva_igralca_1);
	        		}
	        		// èe pa plošèa pripada igralcu dva, nastavimo barvo na barva_igralca_2
	        		if (seznam.get(100) == 2) {
	        			g.setColor(barva_igralca_2);
	        		}     			
	        		
	        		// podobno kot pri številkah moramo oèiti 4 primere, vsak svoj "kvadrant" polja, ker prilagajamo pozicijo glede na polje
	        		if (key < 7 && key > 0) { 
		   
		        		if (polje == 0) {
		        			// Nothing really happens
		        		}
		        		if (polje == 1) {
		        			// Narisemo en žeton
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 2) {
		        			// Narisemo dva žetona
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 3) {
		        			// Narisemo tri žetone
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + 2 * dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 4) {
		        			// Narisemo stiri žetone
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + 2 * dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + 3 * dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 5) {
		        			// Narisemo pet žetonov
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + 2 * dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + 3 * dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 103/100, v_rob  + v_trikotnika * 1/50 + 4 * dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje > 5) {
		        			// Narisemo standard za 5+ zetonov
		        			g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika  * 103/100, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.drawString("" + polje, s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika  * 103/100 + s_trikotnika / 8, v_rob + v_trikotnika * 1/50 + 2 * dim_zeton);
		        		}
	        		}
	        		if (key < 13 && key > 6) { 
		        				      
		        		if (polje == 0) {
		        			// Nothing really happens
		        		}
		        		if (polje == 1) {
		        			// Narisemo en žeton
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 2) {
		        			// Narisemo dva žetona
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 3) {
		        			// Narisemo tri žetone
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + 2 * dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 4) {
		        			// Narisemo stiri žetone
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica,  v_rob  + v_trikotnika * 1/50 + dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + 2 * dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + 3 * dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 5) {
		        			// Narisemo pet žetonov
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + 2 * dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + 3 * dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika * 102/100 - n_polovica, v_rob  + v_trikotnika * 1/50 + 4 * dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje > 5) {
		        			// Narisemo standard za 5+ zetonov
		        			g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika  * 102/100 - n_polovica, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.drawString("" + polje, s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (key - 1) * s_trikotnika  * 102/100 + s_trikotnika / 8 - n_polovica, v_rob + v_trikotnika * 1/50 + 2 * dim_zeton);
		        		}
	        		}
	        		if (key < 19 && key > 12) { 
		        				        		
		        		if (polje == 0) {
		        			// Nothing really happens
		        		}
		        		if (polje == 1) {
		        			// Narisemo en žeton
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * polje - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 2) {
		        			// Narisemo dva žetona
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * polje  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 3) {
		        			// Narisemo tri žetone
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * (polje - 1)  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * polje  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 4) {
		        			// Narisemo stiri žetone
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * (polje - 2)  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * (polje - 1)  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * polje  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 5) {
		        			// Narisemo pet žetonov
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * (polje - 3)  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * (polje - 2)  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * (polje - 1) - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton * polje  - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
		        		if (polje > 5) {
		        			// Narisemo standard za 5+ zetonov
		        			g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
		        			g.drawString("" + polje, s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100 + dim_zeton / 4, v_polja - v_rob - dim_zeton - v_trikotnika * 1/50 * 2);
		        			g.fillOval(s_rob * 113/100 + s_trikotnika / 2 - dim_zeton / 2 + s_trikotnika * (key - 13) * 101/100, v_polja - v_rob - dim_zeton - v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
	        		}
	        		if (key > 18 && key < 25) { 
		        		
		        		if (polje == 0) {
		        			// Nothing really happens
		        		}
		        		if (polje == 1) {
		        			// Narisemo en žeton
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika, v_polja - v_rob - v_trikotnika * 1/50 - dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 2) {
		        			// Narisemo dva žetona
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 3) {
		        			// Narisemo tri žetone
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - dim_zeton , dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - 2 * dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 4) {
		        			// Narisemo stiri žetone
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - 2 * dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - 3 * dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje == 5) {
		        			// Narisemo pet žetonov
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - 2 * dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - 3 * dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika * 103/100, v_polja - v_rob  - v_trikotnika * 1/50 - 4 * dim_zeton - dim_zeton, dim_zeton, dim_zeton);
		        		}
		        		if (polje > 5) {
		        			// Narisemo standard za 5+ zetonov
		        			g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
		        			g.fillOval(s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika  * 103/100, v_polja - v_rob - v_trikotnika * 1/50 - dim_zeton,  dim_zeton, dim_zeton);
		        			g.drawString("" + polje, s_polja - s_rob * 113/100 - s_trikotnika / 2 - dim_zeton / 2 - (24 - key) * s_trikotnika  * 103/100 + s_trikotnika / 8, v_polja - v_rob - v_trikotnika * 2/50 - 2 * dim_zeton + dim_zeton);
		        		}
	        		}
	        		// odstranjeni žetoni igralca 1
	        		if (key == 0) { 
		        		
		        		if (polje == 0) {
		        			// Nothing really happens
		        		}
		        		if (polje == 1) {
		        			// Narisemo en žeton
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 2) {
		        			// Narisemo dva žetona
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 3) {
		        			// Narisemo tri žetone
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + 2 * dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 4) {
		        			// Narisemo stiri žetone
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + 2 * dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + 3 * dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 5) {
		        			// Narisemo pet žetonov
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + 2 * dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + 3 * dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50 + 4 * dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje > 5) {
		        			// Narisemo standard za 5+ zetonov
		        			g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_rob + v_trikotnika * 1/50,  dim_zeton, dim_zeton);
		        			g.drawString("" + polje, s_polja - s_rob / 2 - dim_zeton / 2 + s_rob / 8, v_rob + v_trikotnika * 1/50 + 2 * dim_zeton);
		        		}
	        		}
	        		//odstranjeni žetoni igralca 2
	        		if (key == 25) { 
		        		
		        		if (polje == 0) {
		        			// Nothing really happens
		        		}
		        		if (polje == 1) {
		        			// Narisemo en žeton
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 2) {
		        			// Narisemo dva žetona
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 2 * dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 3) {
		        			// Narisemo tri žetone
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 2 *dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 3 * dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 4) {
		        			// Narisemo stiri žetone
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 2 * dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 3 * dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 4 * dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje == 5) {
		        			// Narisemo pet žetonov
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 2 * dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 3 * dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 4 * dim_zeton,  dim_zeton, dim_zeton);
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - 5 * dim_zeton,  dim_zeton, dim_zeton);
		        		}
		        		if (polje > 5) {
		        			// Narisemo standard za 5+ zetonov
		        			g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
		        			g.fillOval(s_polja - s_rob / 2 - dim_zeton / 2 - s_rob / 16, v_polja - v_rob - v_trikotnika * 1/50 - dim_zeton,  dim_zeton, dim_zeton);
		        			g.drawString("" + polje, s_polja - s_rob / 2 - dim_zeton / 2 + s_rob / 8,v_polja -  v_rob  - dim_zeton * 1/10 - v_trikotnika * 1/50 - dim_zeton);
		        		}
	        		}
	        		
	        		//izloèeni žetoni
	        		if (key == 26) {
	        			
	        			if (seznam.get(100) == 1) {
	        				
	        				if (polje == 0) {
	        					// Prazno
	        				}
	        				if (polje == 1) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        				}
	        				if (polje == 2) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        				}
	        				if (polje == 3) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 2 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        				}
	        				if (polje == 4) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 2 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 3 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        				}
	        				if (polje == 5) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 2 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 3 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 4 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        				}
	        				if (polje > 5) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 - v_trikotnika * 1/20 - dim_zeton, dim_zeton, dim_zeton);
	        					g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
	        					g.drawString("" + polje, s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 4/100, v_polja / 2 - v_trikotnika * 3/40 );
	        				}
	        			}
	        			if (seznam.get(100) == 2) {
	        				
	        				if (polje == 0) {
	        					// Prazno
	        				}
	        				if (polje == 1) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        				}
	        				if (polje == 2) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        				}
	        				if (polje == 3) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 - v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 2 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        				}
	        				if (polje == 4) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 2 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 3 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        				}
	        				if (polje == 5) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 2 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20 , dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 3 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20 , dim_zeton, dim_zeton);
	        					g.fillOval(s_polja / 2 + s_trikotnika + 4 * dim_zeton + s_trikotnika * 1/100, v_polja / 2 + v_trikotnika * 1/20 , dim_zeton, dim_zeton);
	        				}
	        				if (polje > 5) {
	        					g.fillOval(s_polja / 2 + s_trikotnika, v_polja / 2 + v_trikotnika * 1/20, dim_zeton, dim_zeton);
	        					g.setFont(new Font("Times New Roman", Font.PLAIN, velikost_stevilk * 2));
	        					g.drawString("" + polje, s_polja / 2 + s_trikotnika + dim_zeton + s_trikotnika * 4/100, v_polja / 2 + v_trikotnika * 1/40 + dim_zeton);
	        				}
	        			}
	        		}
	        	}
	        }
	        // èe je igre konec nam odpre novo okence
	        if (konec_igre) {
				konecIgre();
			}
	       }
		
	        	
	}
	
	
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		//dogajanje na osnovnem meniju
		if (osnovni_meni) {
			
			//ker se pozicija menija glee na igra_po_meri spremeni, se tudi zaznavna polja spremenijo
			if (igra_po_meri) {
				int x1 = start + dolzina_naslova / 2;
				int y1 = (int) Math.round(0.4 * v_polja) - velikost_pisave / 2 + velikost_pisave / 4;
			
				int x2 = x1;
				int y2 = (int) Math.round(0.4 * v_polja) + velikost_pisave / 2 + velikost_pisave / 4;
			
				int x3 = x1;
				int y3 = (int) Math.round(0.4 * v_polja) + 3 * velikost_pisave / 2 + velikost_pisave / 4;
				//naredimo okolico, ki jo zazna kot pritisk na gumb za vsak gumb posebej
				if (Math.abs(x - x1) <= dolzina_naslova / 2 && Math.abs(y - y1) <= velikost_pisave / 2 ) {
					novaIgra();
				}
				if (Math.abs(x - x2) <= dolzina_naslova / 2 && Math.abs(y - y2) <= velikost_pisave / 2) {
					launcher();
				}
				if (Math.abs(x - x3) <= dolzina_naslova / 2 && Math.abs(y - y3) <= velikost_pisave / 2) {
					launcher();
				}
			
			}
			// zelo podobno kot pri zgornjem primeru	
			if (!igra_po_meri) {
				int x1 = start + dolzina_naslova / 2;
				int y1 = (int) Math.round(0.4 * v_polja) - velikost_pisave / 2 + velikost_pisave / 4 + v_rob;
				
				int x2 = x1;
				int y2 = (int) Math.round(0.4 * v_polja) + velikost_pisave / 2 + velikost_pisave / 4 + v_rob;
				
				if (Math.abs(x - x1) <= dolzina_naslova / 2 && Math.abs(y - y1) < velikost_pisave / 3) {
					novaIgra();
				}
				if (Math.abs(x - x2) <= dolzina_naslova / 2 && Math.abs(y - y2) < velikost_pisave / 3) {
					launcher();
				}
				
			}
			
			
		
			
				
		}
		//DOGAJANJE MED IGRo
		//preverimo, èe je igra sploh aktivna
		if (aktivna_igra) {
			
			// najprej doloèimo dve spremenljivki, ki nam bosta pomagali pri potezah
			af = polje(x,y);
			int a = aktivno_polje;
			
			if (med_potezo) {
					if (af < 100) { // z vrednostjo veè kot 100 so rezervirani kliki, ki ne naredijo niè
						// preverimo, èe imamo na voljo še kakšno potezo
						if (kocka1_poteze + kocka2_poteze > 0) {
							// èe ja lahko naredimo potezo
							if (poteze) {
								// naredimo potezo
								poteza(a, af, igralec_na_potezi);
								}
							// le ne, pa bi morali biti zmožni naresti potezo, zato "prižegemo" poteze
							if (!poteze) poteze = true;
					}
					aktivno_polje = af;
					
					// èe pa nimamo veè potez, potem nastavimo med_potezo na false, kar pravzaprav pomeni, da nastavimo poticijo kock na "pripravljeno za nov met", ter zamenjamo igralca
					if (kocka1_poteze + kocka2_poteze == 0) {
						// Opomba: kocka1_poteze + kocka2_poteze je vedno nenegativno število
						med_potezo = false;
						menjajIgralca();
						resetirajPotezo(); 
					}
					
				}
					// v primeru, da igralec ne more veè narediti poteze lahko s pritiskom na gumb zakljuèi potezo in jo preda naslednjemu igralcu
					if (Met(x, y, start_met_x, start_met_y, dim_met)) {
						med_potezo = false;
						menjajIgralca();
						resetirajPotezo();
					}	
				
			}
			else { 
				// èe pa nismo med potezo, potem preverimo, èe smo pritisnili na gumb za met
				if (Met(x, y, start_met_x, start_met_y, dim_met)) {
					med_potezo = true; // nastavimo na true, da aktiviramo potezo
					RollTheDice(); // vremo kocke, ki nastavijo novi vrednosti
				}
			}    
		       	   	
		}
		
		repaint(); // na koncu pononvo pobarvamo, da se nam polje osveži

	}
	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		// Mouse pressed bomo uporabili samo, na zaèetnem meniju. In sicer ko bomo pritisnili gumb, se bo ta obarval, da bomo videli, da smo ga pritisnili 
		if (osnovni_meni) {
			
			// zopet loèimo dva primera, glede na igro_po_meri
			if (igra_po_meri) {
				// gre za kopijo kode od zgoraj, razen tega, namesto pa da se aktiviran nova igra ali igra proti raèunalniku pa se obmoèje gumba obarva rdeèe
				int x1 = start + dolzina_naslova / 2;
				int y1 = (int) Math.round(0.4 * v_polja) - velikost_pisave / 2 + velikost_pisave / 4;
			
				int x2 = x1;
				int y2 = (int) Math.round(0.4 * v_polja) + velikost_pisave / 2 + velikost_pisave / 4;
			
				int x3 = x1;
				int y3 = (int) Math.round(0.4 * v_polja) + 3 * velikost_pisave / 2 + velikost_pisave / 4;
			
				if (Math.abs(x - x1) <= dolzina_naslova / 2 && Math.abs(y - y1) <= velikost_pisave / 2 ) {
					barva_ovala_1 = Color.red;
				}
				if (Math.abs(x - x2) <= dolzina_naslova / 2 && Math.abs(y - y2) <= velikost_pisave / 2) {
					barva_ovala_2 = Color.red;
				}
				if (Math.abs(x - x3) <= dolzina_naslova / 2 && Math.abs(y - y3) <= velikost_pisave / 2) {
					barva_ovala_3 = Color.red;
				}
			
			}
			// enako kot zgoraj, samo drugane pozicije	
			if (!igra_po_meri) {
				int x1 = start + dolzina_naslova / 2;
				int y1 = (int) Math.round(0.4 * v_polja) - velikost_pisave / 2 + velikost_pisave / 4 + v_rob;
				
				int x2 = x1;
				int y2 = (int) Math.round(0.4 * v_polja) + velikost_pisave / 2 + velikost_pisave / 4 + v_rob;
				
				if (Math.abs(x - x1) <= dolzina_naslova / 2 && Math.abs(y - y1) < velikost_pisave / 3) {
					barva_ovala_1 = Color.red;
				}
				if (Math.abs(x - x2) <= dolzina_naslova / 2 && Math.abs(y - y2) < velikost_pisave / 3 ) {
					barva_ovala_2 = Color.red;
				}
				
			}
		
		}
		
		repaint();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		// ko miško spustimo (ko nehamo pritisnit na gumb) se nam gumb povrne v osonvo barvo
		if (osnovni_meni) {
			barva_ovala_1 = barva_ovala_2  = barva_ovala_3 =  new Color(51,51,51);
		}
		
		repaint();
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	// OD TOD NAPREJ SO DODATNE METODE (naredijo kodo bolj pregledno in enostavno)
	
	// metoda Met preveri ali se je gumb za met res pritisnil
	public boolean Met(int x, int y, int start_x, int start_y, int dim) {
		boolean preveri_x = (start_x <=  x) && (x <= start_x + dim);
		boolean preveri_y = (start_y <=  y) && (y <= start_y + dim);
		return (preveri_x && preveri_y);
							
		}
	//metoda RollTheDice nastavi nove vrednosti za kocki, le te nakljuèno izbere (1), prav tako nastavi osnovno število potez, ki jih ima igralec za vsak met glede na padli kocki (2)
	public void RollTheDice() {
		pokazi_poteze = true;
		//(1)
		kocka1 = (int)(Math.random() * 6) + 1;
		kocka2 = (int)(Math.random() * 6) + 1;
		
		//(2)
		if (kocka1 == kocka2) {
			kocka1_poteze = 2;
			kocka2_poteze = 2;
			
		}
		else {
			kocka1_poteze = 1;
			kocka2_poteze = 1;
			
		}
		
		
	}
	
	//metoda menjajIgralca zmenja igralca (kdo bi si mislil :) ), preprosto obrne vrednost igralec_na_potezi in nastavi barvo barva_igralca_na_potezi
	public void menjajIgralca() {
		if (igralec_na_potezi) {
			igralec_na_potezi = false;
			barva_igralca_na_potezi = barva_igralca_2;
		}
		else {
			igralec_na_potezi = true;
			barva_igralca_na_potezi = barva_igralca_1;
		}
	}
	
	//metoda razlika izraèuna absolutno vrednost razlike med dvema številoma, metodo se uporabi znotraj metode poteza
	public int razlika(int aktivna, int nova) {
		return Math.abs(aktivna - nova);
	}
	
	//metoda resetirajPotezo v primeru napaène poteze nastavi osnovne vrednosti poteze na nerelavantne
	public void resetirajPotezo() {
		aktivno_polje = 250; // aktivno polje je tisto, s katerega bomo želeli narediti potezo
		af = 250; // 
	}
	
	//metoda izolciZeton se poklièe, kadar igralec "poje" žeton nasprotnika, metoda poveèa število izloèenih žetonov tistega igralca in premakne žeton igralca, ki je pojedel 
	public void izlociZeton(int mesto, boolean igralec_na_potezi) {
		if (igralec_na_potezi) {
			int n = plosca_igralec_2.get(26);
			plosca_igralec_2.replace(26, n + 1);
			plosca_igralec_2.replace(mesto, 0);
		}
		if (!igralec_na_potezi) {
			int n = plosca_igralec_1.get(26);
			plosca_igralec_1.replace(26, n + 1);
			plosca_igralec_1.replace(mesto, 0);
		}
	}
	
	//metoda poteza naredi potezo, vmes pa še preveri, èe je poteza sploh legalna
	public void poteza(int aktivna, int nova, boolean igralec_na_potezi) {
		// na novo definiramo nekaj parametrov, prviè, da so krajpi, drugiè, da jih lahko spreminjamo, ne da bi vplivali na generalne spremenljivke igre
		int a = razlika(aktivna, nova); // razlika aktivne in nove je pravzaprav razdalja med dvema poljema, ki sta bila izbrana ee za drugim (s klikom na miško)
		int b = kocka1;
		int c = kocka2;
		int e = kocka1_poteze;
		int f = kocka2_poteze;
		// èe je razdalja 0 oz. èe smo zaporedoma pritisnili dvakrat isto polje s ne zgodi niè
		if (a != 0){
			// preverimo, èe je prvo izbrano polje (od koder želimo premikati plošèke) sluèajno kode 75 ali 50, to sta ratlièni kodi ki obe opisujeta mesto 26 ali izloèene plošèke. 50 je za igralca 1, 75 za igralca 2
			if (aktivna == 50 || aktivna == 75) {
				if (aktivna == 50) {
					a = razlika(0, nova); // zmanipuliramo, da misli, da je pravzaprav 0, saj je, èe bi izloèili žetone ta ravno 0 (met 1 pomeni, da lahko žeton postavimo nazaj v igro na polje 1)
					aktivna = 26; // aktivno pa prav tako roèno prestavimo na 26, da bodo spremembe v seznamu, torej plošèi pravilno zabeležene
					//definiramo r (da zavzame manj prsotora)
					int r = plosca_igralec_1.get(26);
					// èe je r = 0, kar pomeni, da igralec nima izloèenih žetonov, se vse skupaj zakljuèi, torej resetiramo potezo (ne moremo narediti poteze od nekje, kjer nimamo žetonov)
					if (r == 0) resetirajPotezo();
					if (plosca_igralec_2.get(nova) > 1) resetirajPotezo(); // preverimo, èe je legalen premik in resetiramo potezo, èe ni
					else {
						if ((a == b && e != 0) || (a == c && f != 0)) { // preverimo, èe nam kateri od metov dovoljuje premik za to razdaljo
							if (a == b && e > 0) { // èe nam to dovoljuje prvi met ga "porabimo", torej zmanjšamo kolièino metov tiste kocke za 1
								b = 0; 
								e = 0; 
								kocka1_poteze--;
							}
							else { // sicer nam to dovoljuje drugi met (drugaèe ne gre), je pa znotraj else zanke saj tega ne sme preveriti, èe je preveril prvi if, saj bi tedaj v primeru dvojnih kock odštel obe hkrati  
								if (a == c) c = 0; f = 0; kocka2_poteze--;
							}
						
					
							
							// sicer pa odštejemo en žeton iz obmoèja izloèenih in ga prištejemo na novo dodeljeno polje
							plosca_igralec_1.replace(26, r - 1);
							int m = plosca_igralec_1.get(nova);
							plosca_igralec_1.replace(nova, m + 1);
							if (plosca_igralec_2.get(nova) == 1) izlociZeton(nova, igralec_na_potezi);
																
							
						}
					}
					}
				// podobno naredimo èe je na potezi igralec 2 le malenkost moramo spremeniti oštevilèena polja (0 -> 25 in obratno) ter zamenjati polje, ki ga spreminjamo
				if (aktivna == 75) {
					a = razlika(25, nova);
					aktivna = 26;
				
					int r = plosca_igralec_2.get(26);
					
					if (r == 0) resetirajPotezo();
					if (plosca_igralec_1.get(nova) > 1) resetirajPotezo(); 
					else {
						if ((a == b && e != 0) || (a == c && f != 0)) { 
							if (a == b && e > 0) { 
								b = 0; 
								e = 0; 
								kocka1_poteze--;
							}
							else { 
								if (a == c) c = 0; f = 0; kocka2_poteze--;
							}
						
							plosca_igralec_2.replace(26, r - 1);
							int m = plosca_igralec_1.get(nova);
							plosca_igralec_2.replace(nova, m + 1);
							if (plosca_igralec_1.get(nova) == 1) izlociZeton(nova, igralec_na_potezi);
																
							
						}
					}
					}
				
				}
			// ko pa nimamo tako zahtvnih primerov (prestavlamo iz polja v polje), principi so enaki kot zgoraj vendar veliko bolj posplošeni
			// najprej preverimo, želejana poteza zadostuje meto kock
			if ((a == b && e != 0) || (a == c && f != 0)) { 
				// preverimo, kdo je na vrsti, da bomo vedeli èigave poteze bomo pravzaprav spremljali in posledièno èigavo plošèo bomo spreminjali
				if (igralec_na_potezi) { 
					// preverimo, še da je aktivno polje pod 100 (tore da je premik iz enega polja) ter preverimo, da je premik narejen v polje (karakterizacija polja je zadnja metoda)
					if (plosca_igralec_1 != null && aktivna < 100 && nova < 100) {
						
						/* preverimo še:
						 * èe nova < aktivna , bi pomenilo, da igralec 1 premika figure v negativno smer, kar pa jih ne more, se poteza resetira
						 * èe ima igralec 2 na mestu kamor želi igralec 1 postaviti žeton že 2, se poteza tdi resetira
						 * èe igralec 1 nima izpraznjenega polja z izloèenimi žetoni mora najprej narediti potezo z njimi, torej se ta poteza razveljavi oz. resetira
						 * èe igralec še ni v zakljuèni fazi (nima vseh svojih žetonov v svoji hiši), se resetira
						 */
						if ((nova < aktivna) || (plosca_igralec_2.get(nova) > 1) || (plosca_igralec_1.get(26) != 0) || (nova == 25 && !zakljucnaFaza(plosca_igralec_1))) {
							resetirajPotezo();						
						}
						else {
							// in še enkrat tako kot zgoraj, preverimo še zadnjo stvar, èe iz mesta sploh lahko prestavljamo plošèek ali ne
							int r = plosca_igralec_1.get(aktivna);
							if (r == 0) resetirajPotezo();
							else {
							
								// enako kot v primeru, ko smo premikali plošèke iz obmoèja izloèenih, tudi tu popravimo število metov
								if (a == b && e > 0) {
									b = 0; 
									e = 0; 
									kocka1_poteze--;
								}
								else {
									if (a == c) c = 0; f = 0; kocka2_poteze--;
								
								}
							
									// ko zadovoljuje naša potez avsem pravilom jo izvedemo, število plošèkov na aktivnem polju zmanjšamo za 1, število na novem polju pa za 1 poveèamo
									int z = aktivna;
									plosca_igralec_1.replace(z, r - 1);
									int m = plosca_igralec_1.get(nova);
									plosca_igralec_1.replace(nova, m + 1);
									if (plosca_igralec_2.get(nova) == 1) izlociZeton(nova, igralec_na_potezi);
							
							}
						}
					}
				}
				// zadevo ponovimo, za igralca 2, žal moramo ppreveriti, kdo je na vrsti že precej zgoaj v fazi preverjanja, saj preverjamo plošèo posameznega igralca (to nanese malo ponovitev kode)
				if (!igralec_na_potezi) { 
					if (plosca_igralec_1 != null && aktivna < 100 && nova < 100) {
						
						if ((nova > aktivna) || (plosca_igralec_1.get(nova) > 1) || (plosca_igralec_2.get(26) != 0) || (nova == 0 && !zakljucnaFaza(plosca_igralec_2))) {
							resetirajPotezo();	
						}
						else {
							int r = plosca_igralec_2.get(aktivna);
							if (r == 0) resetirajPotezo();
							else {
							
								if (a == b && e > 0) {
									b = 0; 
									e = 0; 
									kocka1_poteze--;
								}
								else {
									if (a == c) c = 0; f = 0; kocka2_poteze--;
								
								}
									int z = aktivna;
									plosca_igralec_2.replace(z, r - 1);
									int m = plosca_igralec_2.get(nova);
									plosca_igralec_2.replace(nova, m + 1);
									if (plosca_igralec_1.get(nova) == 1) izlociZeton(nova, igralec_na_potezi);
							
							}
						}
					}
				}
		 }	
		}
		
	}
	
	//metoda zacetnaPozicija nastavi plošèke tako, kot so na zaèetku vsake igre, praktièno nastavi zaètna polja igralcev
	public void zacetnaPozicija() {
		// definiramo zacetna slovarja
		this.plosca_igralec_1 = new HashMap<Integer, Integer>();
		this.plosca_igralec_2 = new HashMap<Integer, Integer>();
		
		//in ju napolnimo tako, da je na vsakem polju 0 žetonov
		for (int j = 0; j < 27; j++) {
			plosca_igralec_1.put(j, 0);
			plosca_igralec_2.put(j, 0);
		}
		//potem pa zamenjamo pri vsakem tako, da zadostuje zaèetni postavitvi
		plosca_igralec_1.replace(1, 2);
		plosca_igralec_1.replace(12, 5);
		plosca_igralec_1.replace(17, 3);
		plosca_igralec_1.replace(19, 5);
		plosca_igralec_2.replace(6, 5);
		plosca_igralec_2.replace(8, 3);
		plosca_igralec_2.replace(13, 5);
		plosca_igralec_2.replace(24, 2);
		// na koncu dodamo še "lastništvo" torej šteilo, ki nam pove, kdo je lastnik plošèe, ne da bi rabili iti ven iz plošèe
		plosca_igralec_1.put(100, 1);
		plosca_igralec_2.put(100, 2);
		
		
	}
	
	//metoda Polje iz klika doloèi katero polje (trikotnik) je bil pritisnjen. Kljub temu da so polja trikotniki je "hitbox" pravokotnik, saj je polje v primeru, ko polje zseda 5 žetonov le to bolj intuitivno pritisniti kot pravokotnik
	public int polje(int x, int y) {
		
		// levi zgornji del plošèe
		for (int i = 0; i < 6; i++) {
			if (s_rob + i * s_trikotnika < x && s_rob + s_trikotnika * (i + 1) > x) {
				if (v_rob  < y && v_rob + v_trikotnika > y) {
					return 12 - i;
				}
			}
		}
		// desni zgornji kvadrant
		for (int i = 0; i < 6; i++) {
			if (s_rob + (i + 6) * s_trikotnika + n_polovica < x && s_rob + s_trikotnika * (i + 7) + n_polovica > x) {
				if (v_rob  < y && v_rob + v_trikotnika > y) {
					return 6 - i;
				}
			}
		}
		// levi spodnji kvadrant
		for (int i = 0; i < 6; i++) {
			if (s_rob + i * s_trikotnika < x && s_rob + s_trikotnika * (i + 1) > x) {
				if (v_polja - v_rob - v_trikotnika < y && v_polja - v_rob > y) {
					return 13 + i;
				}
			}
		}
		// desni spodnji kvadrant
		for (int i = 0; i < 6; i++) {
			if (s_rob + (i + 6) * s_trikotnika + n_polovica < x && s_rob + s_trikotnika * (i + 7) + n_polovica > x) {
				if (v_polja - v_rob - v_trikotnika < y && v_polja - v_rob > y) {
					return 19 + i;
				}
			}
		}
		// izven polja 
		for (int i = 0; i < 2; i++) {
			if (s_polja - s_rob < x) {
				if (v_rob < y && v_rob + v_trikotnika > y) {
					return 0;
				}
				if (v_polja - v_rob > y && v_polja - v_rob - v_trikotnika < y) {
					return 25;
				}
			}
		}
		// pojedeni
		//Opomba: polje za pojedene je pravzaprav eno samo, a je s pomoèno preverjanja, kdo je na potezi moè doloèiti dve razlièni vrednosti. Izbrani sta 50 in 75, saj sta obe pod 100 (eden od pogojev pri metodi poteza), hkrat pa, èe se sluèajno pojavita v metodi razlika() (kar se sicer ne bi smeli) vrnedta vrednost veè kot 6 in se ne skladata z metom.
		if (s_rob + 6 * s_trikotnika + n_polovica < x && s_rob + s_polja - s_rob > x) {
			if (v_rob + v_trikotnika + v_trikotnika / 50 < y && v_polja - v_rob - v_trikotnika - v_trikotnika / 50 > y) {
				return (igralec_na_potezi) ? 50 : 75;
				// Mimogrede, od tod pridejo vrednosti aktivne 50 in 75 pri metodi poteza, ki se tarkat zdijo nepojasljive
			}
		}
		
		// v primeru da pa kliknemo izven vseh polj, pa vrne vrednost 1000. Dovolj dobro bi bilo katerokoli število > 100. Recimo števlo 250 pri resetiraj potezo ima praktièno enako uporabo vendar se je zaradi iskanja hrošèev uporabilo razlišno število.
		return 1000;
	}
	
	
	//metoda naPotezi vrne true / false, glee na to, kdo je na potezi, v teoriji je namenjena bolj za klicanje iz drugih razredov
	public boolean naPotezi() {
		return igralec_na_potezi;
	}

	// metoda novaIgra odpre novo okno in aktivira igro
	public void novaIgra() {
		okno_igra = new Okno();
		okno_igra.pack();
		okno_igra.setVisible(true);
		okno_igra.platno.osnovni_meni = false;
		okno_igra.platno.aktivna_igra = true;
	}
	// metoda launcher odpre nov launcher
	public void launcher() {
		Okno okno = new Okno();
		okno.pack();
		okno.setVisible(true);
	}

	// metoda konecIgre odpre manjše okence z obvestilom o koncu igre
	public void konecIgre() {
	
	 // ime se importa iz igre
	
		JPanel okence = new JPanel();
		JOptionPane.showMessageDialog(okence, "Zmagal je " + zmagovalec() , "Konec igre", JOptionPane.PLAIN_MESSAGE);
	
	}
	
	// metoda zakljucnaPoteza preveri, ali igralèeva plošèa zadostuje pogoju, da lahko zaène plošèke premikati izven plošèe
	public boolean zakljucnaFaza(HashMap<Integer, Integer> plosca) {
		if (!igralec_na_potezi) {
			int i = 24;
			while (i > 6) {
				if (plosca.get(i) != 0) return false;
				i--;
			}
		}
		if (igralec_na_potezi) {
			int i = 0;
			while (i < 19) {
				if (plosca.get(i) != 0) return false;
				i++;
			}
		}
		return true;
	}

	// preprosta metoda ki vrne ime zmagovalca
	public String zmagovalec() {
		if (igralec_na_potezi) return Okno.ime_igralca_1;
		else return Okno.ime_igralca_2;
	}
	
}



