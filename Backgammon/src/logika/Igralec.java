package logika;

import logika.Zetoni.Polje;

public class Igralec {
	
	protected String ime;
	protected Polje id_igralca;
	
	public Igralec(String ime, Polje id_igralca) {
		this.ime = ime;
		this.id_igralca = id_igralca;
	}
	
	public String ime() {
		return ime;
	}
	
	public Polje id() {
		return id_igralca;
	}
}
