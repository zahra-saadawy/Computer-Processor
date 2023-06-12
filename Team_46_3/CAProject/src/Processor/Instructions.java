package Processor;

public class Instructions {
	String opcode;
	String r1;
	String r2;
	String r3;
//	String r1Value;
//	String r2Value;
//	String r3Value;
	String shamt;
	String imm;
	String address;
	int instructionNo;

	public Instructions(String instruction, RegisterFile reg) {
		opcode = instruction.substring(0, 4);
		r1 = instruction.substring(4, 9);
//		System.out.println(Integer.parseInt(r1,2));
//		r1Value = reg.generalPurpose[(Integer.parseInt(r1, 2))].getWord();
		r2 = instruction.substring(9, 14);
//		System.out.println(Integer.parseInt(r1,2));
//		r2Value = reg.generalPurpose[(Integer.parseInt(r2, 2))].getWord();
		r3 = instruction.substring(14, 19);
//		r3Value = reg.generalPurpose[(Integer.parseInt(r3, 2))].getWord();
		shamt = instruction.substring(19, 32);
		imm = instruction.substring(14, 32);
//		System.out.println(imm
		address = instruction.substring(4, 32);
	}
//
//	public static void main(String[] args) {
//		String s = "011";
//		System.out.println(Integer.parseInt(s, 2));
//	}

	@Override
	public String toString() {
		return "" + opcode + r1 + r2 + r3 + shamt;
	}

}
