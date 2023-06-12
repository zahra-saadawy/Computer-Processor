package Processor;

import java.util.Vector;

public class Memory {
	String [] memory;
	int index;
	boolean enableMemWrite = true;
	boolean enableMemRead = true;

	public Memory() {
		this.memory = new String[2048];
		index = 0;
	}

	public void insert(String instructions) {
		int size = instructions.length() / 32;
		String subString = "";
		for (int i = 0; i < size; i++) {
			if (index > 1023) {
				index = 0;
			}
			subString = instructions.substring(i * 32, (i * 32) + 32);
			this.memory[index]= subString;
			index++;
//			System.out.println(this.memory[i]);
		}

	}

	public void write(String instructions, int pc) {
		if (enableMemWrite && pc > 1023 && pc < 2048) {
			while (instructions.length()<  32) {
				instructions = "0" + instructions;
			}
//			System.out.println(instructions);
			this.memory[pc]= instructions;
		}
	}

	public String read(int pc) {
		if (enableMemRead && pc > 1023 && pc < 2048) {
			return this.memory[pc];
		}
		return null;
	}

//	public static void main(String[] args) {
//		String s = "00100100101101010101010100010101001001001011010101010101000101010010010010110101010101010001010100100100101101010101010100010101";
//		Memory mem = new Memory();
//		mem.insert(s);
//	}
}
