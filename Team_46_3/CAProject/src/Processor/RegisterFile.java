package Processor;

import java.util.Vector;

public class RegisterFile {
	Register PC;
	Register[] generalPurpose;

	
	public RegisterFile() {
		PC = new Register("PC");
		String temp =Integer.toBinaryString(0);
		PC.setWord(temp);
		generalPurpose = new Register[32];
		generalPurpose[0] =new Register("R" + (0));		
		for(int i = 1; i< 32 ; i++) {
			generalPurpose[i] =new Register("R" + (i));
		}
		
	}
	public void PCadd() {
		PC.setWord(Integer.toBinaryString((Integer.parseInt(PC.getWord(),2)+1)));
	}
	
//	public static void main(String[] args) {
//		RegisterFile reg = new RegisterFile();
//
//		
//		
//	}
}
