package vodja;

public enum VrstaIgralca {
	R, C; 

	@Override
	public String toString() {
		switch (this) {
		case C: return "Človek";
		case R: return "Računalnik";
		default: assert false; return "";
		}
	}

}
