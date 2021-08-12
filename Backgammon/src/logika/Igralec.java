package logika;

import logika.Zetoni.Polje;

public class Igralec {
	
	protected String ime;
	protected Polje id_igralca;
	protected int udarjeni_zetoni = 0;
	protected int izloceni_zetoni = 0;
	
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
	
	public int udarjeni_zetoni() {
		return udarjeni_zetoni;
	}
	
	public int izloceni_zetoni() {
		return izloceni_zetoni;
	}
	
	public void dodaj_udarjenega() {
		if (udarjeni_zetoni > 15) throw new Error("Preveč udarjenih žetonov");
		udarjeni_zetoni += 1;
	}
	
	public void odstrani_udarjenega() {
		if (udarjeni_zetoni < 0) throw new Error("Ni več udarjenih žetonov");
		udarjeni_zetoni -= 1;
	}
	
	public void dodaj_izlocenega() {
		if (izloceni_zetoni > 15) throw new Error("Preveč izločenih žetonov");
		izloceni_zetoni += 1;
	}
}
