package Processor;

public class Register {
	private String Regword;
	String name;
	boolean regWrite = true;
	boolean regRead = true;

	public Register(String s) {
		Regword = "0";
		this.name = s;
	}

	public String getWord() {
		return Regword;
	}

	public void setWord(String word) {
		while (word.length() < 32) {
			word = "0" + word;
		}
		if (!(this.name.equals("R0")))
			this.Regword = word;
	}

}
