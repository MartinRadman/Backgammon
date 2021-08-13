package logika;

public class Zetoni { // razred, s katerim predstavimo posamezno mesto na polju
	
	public enum Polje {
		PRAZEN, IGRALEC1, IGRALEC2;

	}

	protected Polje na_polju;
	protected int st_zetonov;
	
	public Zetoni(Polje na_polju, int st_zetonov) {
		this.na_polju = na_polju;
		this.st_zetonov = st_zetonov;
	}
	
	public Polje na_polju() {
		return na_polju;
	}
	
	public int stevilo() {
		return st_zetonov;
	}
	
	public void dodaj(Polje igralec) {
		if (na_polju != igralec && st_zetonov == 1) odstrani(); // če smo udarili nasprotnikov žeton
		if (na_polju != Polje.PRAZEN && na_polju != igralec) throw new Error("Neveljavna poteza");
		na_polju = igralec;
		st_zetonov += 1;
	}
	
	public void odstrani() {
		if (st_zetonov <= 0) throw new Error("Tukaj ni več žetonov");
		st_zetonov -= 1;
		if (st_zetonov == 0) na_polju = Polje.PRAZEN; // v primeru, ko odstranimo vse žetone, se območje označi, da je prazno
	}
}
