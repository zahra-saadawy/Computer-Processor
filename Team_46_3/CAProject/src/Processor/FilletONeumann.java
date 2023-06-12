package Processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;

public class FilletONeumann {
//	Hashtable<String, String> instrString= new Hashtable();

	static String nextDecode = null;
	static Instructions nextExecute = null;
	static Instructions nextMem = null;
	static Instructions nextWriteBack = null;

	public String fetch(Memory mem, RegisterFile regFile) {
		System.out.println("	Fetching Started : ");
		String s = mem.memory[convertToInteger(regFile.PC.getWord())];
		regFile.PCadd();
		return s;
	}

	public Instructions decode(String s, RegisterFile regFile) {
		System.out.println("	Started Decoding the Instruction: " + s);
		return new Instructions(s, regFile);
	}

	public String execute(Instructions i, Memory mem, RegisterFile regFile) {
		int res = -1;
		String stemp;
		System.out.println("::::::::::::::::::::::::::::::::::::::::::::::");
		String r2Value = regFile.generalPurpose[Integer.parseInt(i.r2, 2)].getWord();

		String r3Value = regFile.generalPurpose[Integer.parseInt(i.r3, 2)].getWord();
		String r1Value = regFile.generalPurpose[Integer.parseInt(i.r1, 2)].getWord();
		System.out.println("	Started Executing Instr: " + displayInstruction(i.toString()) + " with R2Value: "
				+ r2Value + " And R3Value: " + r3Value + " and R1Value " + r1Value);
		switch (i.opcode) {
		case "0001": // add
			System.out.println("Adding");
			res = convertToInteger(r2Value)
					+ convertToInteger(r3Value);

//			System.out.println("this is add result "+res);
			stemp = Integer.toBinaryString(res);
			// writeBack(regFile.generalPurpose[convertToInteger(i.r1, 2)], stemp);
			return stemp;

		case "0010":// sub
			System.out.println("	Subtracting");
			res = convertToInteger(r2Value) - convertToInteger(r3Value);
			stemp = Integer.toBinaryString(res);
			return stemp;
		case "0011":// mul
			System.out.println("	multiplying");
			res = convertToInteger(r2Value) * convertToInteger(r3Value);
			stemp = Integer.toBinaryString(res);
			return stemp;
		case "0100":// AND
			System.out.println("	Anding");
			res = convertToInteger(r2Value) & convertToInteger(r3Value);
			stemp = Integer.toBinaryString(res);
			return stemp;
		case "0101":// LSL
			System.out.println("	Shifting Left");
			res = convertToInteger(r2Value) << Integer.parseInt(i.shamt, 2);
			stemp = Integer.toBinaryString(res);
			return stemp;
		case "0110":// LSR
			System.out.println("	Shifting Right");
			res = convertToInteger(r2Value)>>>Integer.parseInt(i.shamt, 2) ;
			stemp = Integer.toBinaryString(res);
			return stemp;
		// I-type
		case "1001":// MOVI
			System.out.println("	Moving the imm Value to the register");
			res = convertToInteger(i.imm);
			stemp = Integer.toBinaryString(res);
			return stemp;
		case "1010":// JEQ
			System.out.println("		cheking if equal and jumping");
			if (convertToInteger(r2Value) == convertToInteger(r1Value)) {
				res = Integer.parseInt(regFile.PC.getWord(), 2) + convertToInteger(i.imm);
				System.out.println(regFile.PC.getWord());
				stemp = Integer.toBinaryString(res);
//				nextDecode = null;
				return stemp + "j";
//				writeBack(regFile.PC, stemp);
			}
			break;
		case "1011":// XORI
			System.out.println("	XOR with Imm ");
			res = convertToInteger(r2Value) ^ convertToInteger(i.imm);
			stemp = Integer.toBinaryString(res);
			return stemp;
		case "1100": // read from memory
//			System.out.println("cheking if equal and jumping" );
//			String readFromMem = mem(i, mem);
//			writeBack(regFile.generalPurpose[convertToInteger(i.r1, 2)], readFromMem);

			break;
		case "1101":// write to memory
			// mem(i, mem);
			break;
		case "0000": // JMP
			System.out.println("	Jumping to address ");
			String pcWord = regFile.PC.getWord();
			String subString = pcWord.substring(0, 4);
			stemp = subString + i.address;
//			writeBack(regFile.PC, stemp);
//			nextDecode = null;
			return stemp + "j";

		}

		return null;
	}

	public static String mem(Instructions i, Memory m, Register r, RegisterFile reg) {
		int res = convertToInteger(reg.generalPurpose[Integer.parseInt(i.r2, 2)].getWord()) + convertToInteger(i.imm)
				+ 1024;

		if (i.opcode.equals("1100")) {
			System.out.println("	Reading From memory at address: " + res);
			return m.read(res);
		} else if (i.opcode.equals("1101")) {
			System.out.println("	Writing to memory at adress: " + res + " with word " + r.getWord());
			m.write(r.getWord(), res);
		}
		return null;
	}

	public static void writeBack(Register reg, String s) {
		if (s != null) {
			System.out.println("	Writing to the register: " + reg.name + " with word " + s);
			reg.setWord(s);

		}

	}

	public static String readfile(String path) throws FileNotFoundException {

		String binary = "";
		File file = new File(path);
		Scanner reader = new Scanner(file);

		while (reader.hasNextLine()) {

			char format = 'n';
			// boolean shamtFlag = false;
			String instruction = reader.nextLine();
			String[] InstrucArray = instruction.split(" ");
			binary += instructionVal(InstrucArray[0].toLowerCase());
			// System.out.println(binary);
			String instructionType = instructionVal(InstrucArray[0].toLowerCase());
			if (instructionType.equals("0000"))
				format = 'j';
			else if (instructionType.equals("0101") || instructionType.equals("0110")) {
				binary += registerVal(InstrucArray[1]) + registerVal(InstrucArray[2]) + "00000" + String
						.format("%13s", Integer.toBinaryString(Integer.parseInt(InstrucArray[3]))).replace(' ', '0');
			} else if (instructionType.charAt(0) == '0'
					&& (!(instructionType.equals("0101") || instructionType.equals("0110"))))

				format = 'r';
			else if (instructionType.equals("1001"))
				binary += registerVal(InstrucArray[1]) + "00000" + immediateVal(InstrucArray[2]);
			else if (instructionType.charAt(0) == '1')
				format = 'i';
			switch (format) {
			case 'j':
				binary += String.format("%28s", Integer.toBinaryString(Integer.parseInt(InstrucArray[1]))).replace(' ',
						'0');
				break;
			case 'r':
				binary += registerVal(InstrucArray[1]) + registerVal(InstrucArray[2]) + registerVal(InstrucArray[3])
						+ "0000000000000";
				break;
			case 'i':
				binary += registerVal(InstrucArray[1]) + registerVal(InstrucArray[2]) + immediateVal(InstrucArray[3]);
				break;
			default:
				break;
			}

		}

		return binary;
	}

	public static String displayInstruction(String instruction) {
		String opcode, r1, r2, r3, shamt, imm, address;
		char format = 'n';
		opcode = instruction.substring(0, 4);
		r1 = instruction.substring(4, 9);
		r2 = instruction.substring(9, 14);
		r3 = instruction.substring(14, 19);
		shamt = instruction.substring(19, 32);
		imm = instruction.substring(14, 32);
		address = instruction.substring(4, 32);

		String instructionType = instructionOp(opcode);
		String mips = "";
		if (instructionType.equals("JMP"))
			format = 'j';
		else if (instructionType.equals("LSL") || instructionType.equals("LSR")) {
			mips += instructionOp(opcode) + " R" + Integer.parseInt(r1, 2) + " R" + Integer.parseInt(r2, 2) + " "
					+ Integer.parseInt(shamt, 2);

		} else if (opcode.charAt(0) == '0' && (!(instructionType.equals("LSL") || instructionType.equals("LSR"))))
			format = 'r';
		else if (instructionType.equals("MOVI")) {
			mips += instructionOp(opcode) + " R" + Integer.parseInt(r1, 2) + " " + convertToInteger(imm);

		} else if (opcode.charAt(0) == '1')
			format = 'i';

		switch (format) {
		case 'j':
			mips += instructionOp(opcode) + " " + Integer.parseInt(address, 2);
			break;
		case 'r':
			mips += instructionOp(opcode) + " R" + Integer.parseInt(r1, 2) + " R" + Integer.parseInt(r2, 2) + " R"
					+ Integer.parseInt(r3, 2);
			break;
		case 'i':
			mips += instructionOp(opcode) + " R" + Integer.parseInt(r1, 2) + " R" + Integer.parseInt(r2, 2) + " "
					+ convertToInteger(imm);
			break;
		}
		return mips;
	}

	public static String instructionVal(String instruction) {
		String binary = "";
		switch (instruction) {
		case "add":
			binary = binary + "0001";
			break;
		case "sub":
			binary = binary + "0010";

			break;
		case "mul":
			binary = binary + "0011";

			break;
		case "and":
			binary = binary + "0100";

			break;
		case "lsl":
			binary = binary + "0101";

			break;
		case "lsr":
			binary = binary + "0110";

			break;
		case "jmp":
			binary = binary + "0000";

			break;
		case "movi":
			binary = binary + "1001";

			break;
		case "jeq":
			binary = binary + "1010";

			break;
		case "xori":
			binary = binary + "1011";

			break;

		case "movr":
			binary = binary + "1100";

			break;
		case "movm":
			binary = binary + "1101";

			break;
		default:
			System.out.println("SYNTAX ERROR!");
		}
		return binary;
	}

	public static String instructionOp(String opcode) {
		String op = "";
		switch (opcode) {
		case "0001":
			op = "ADD";
			break;
		case "0010":
			op = "SUB";

			break;
		case "0011":
			op = "MUL";

			break;
		case "0100":
			op = "AND";

			break;
		case "0101":
			op = "LSL";

			break;
		case "0110":
			op = "LSR";

			break;
		case "0000":
			op = "JMP";

			break;
		case "1001":
			op = "MOVI";

			break;
		case "1010":
			op = "JEQ";

			break;
		case "1011":
			op = "XORI";

			break;

		case "1100":
			op = "MOVR";

			break;
		case "1101":
			op = "MOVM";

			break;
		default:
			System.out.println("SYNTAX ERROR!");
		}
		return op;
	}

	public static String registerVal(String r) {
		int number = Integer.parseInt(r.substring(1));
		String binary = String.format("%5s", Integer.toBinaryString(number)).replace(' ', '0');
		return binary;
	}

	public static String immediateVal(String r) {
		int number = Integer.parseInt(r);
		String binary = "";

		if (number < 0) {
			int bitWidth = 17;
			int truncated = number & ((1 << bitWidth) - 1);
			int complement = (1 << bitWidth) + truncated;

			binary = String.format("%" + bitWidth + "s", Integer.toBinaryString(complement)).replace(' ', '0');
		} else {
			binary = String.format("%18s", Integer.toBinaryString(number)).replace(' ', '0');
		}

		return binary;
	}

	public static void compiper(Memory mem, RegisterFile registers, FilletONeumann processor)
			throws NumberFormatException {
		int pc = Integer.parseInt(registers.PC.getWord());
		int instructions = mem.index;
		int maxClockCycles = 7 + ((mem.index - 1) * 2);
		boolean isFetching = false;
//		System.out.println(mem.index);

		int countExec = 0;
		int countDecode = 0;
		String readFromMemory = null;
		String execValue = null;
//			FilletONeumann processor = new FilletONeumann();
		int cycle = 1;
		while (true) {
			System.out.println();
			System.out.println("Program Counter at start of cycle: " +registers.PC.getWord());
			System.out.println();
			System.out.println("We are in Clock Cycle: " + cycle);
//			WriteBack
			if (nextWriteBack != null) {
				System.out.println("We nextWriteBack this instruction: " + displayInstruction(nextWriteBack.toString())
						+ " >> " + nextWriteBack.toString());
				if (readFromMemory != null) {
					writeBack(registers.generalPurpose[Integer.parseInt(nextWriteBack.r1, 2)], readFromMemory);
				} else {
					if (execValue != null && execValue.charAt(execValue.length() - 1) == 'j') {
						nextDecode = null;
						nextExecute = null;
						writeBack(registers.PC, execValue.substring(0, execValue.length() - 1));

					} else
						writeBack(registers.generalPurpose[Integer.parseInt(nextWriteBack.r1, 2)], execValue);
				}
				nextWriteBack = null;
				readFromMemory = null;
				execValue = null;
			}
			// memAccess

			if (nextMem != null) {

				System.out.println("We nextMem this instruction: " + displayInstruction(nextMem.toString()) + " >> "
						+ nextMem.toString());

				nextWriteBack = nextMem;
				readFromMemory = mem(nextWriteBack, mem, registers.generalPurpose[Integer.parseInt(nextMem.r1, 2)],
						registers);
				nextMem = null;
			}
			// execute
			if (nextExecute != null) {
				countExec++;
				System.out.println("We Executing this instruction: " + displayInstruction(nextExecute.toString())
						+ " >> " + nextExecute + " number " + countExec);

				if (countExec == 2) {
					execValue = processor.execute(nextExecute, mem, registers);
					String r2Value1 = registers.generalPurpose[Integer.parseInt(nextExecute.r2, 2)].getWord();

					String r3Value1 = registers.generalPurpose[Integer.parseInt(nextExecute.r3, 2)].getWord();
					String r1Value1 = registers.generalPurpose[Integer.parseInt(nextExecute.r1, 2)].getWord();
					System.out.println("	Finished Executing Instr: " + displayInstruction(nextExecute.toString())
							+ " with R2Value: " + r2Value1 + " And R3Value: " + r3Value1 + " and R1Value " + r1Value1);
					System.out.println("::::::::::::::::::::::::::::::::::::::::::::::");
					nextMem = nextExecute;
					countExec = 0;
					nextExecute = null;
				}

			}

			// decode
			if (nextDecode != null) {
				Instructions decoded = new Instructions(nextDecode, registers);
				countDecode++;
				System.out.println("We Decoding this instruction: " + displayInstruction(decoded.toString()) + " >> "
						+ decoded + " number " + countDecode);
				if (countDecode == 2) {
					nextExecute = decoded;
					countDecode = 0;
				}

			}
//			fetch
			if (cycle % 2 != 0) {
				nextDecode = processor.fetch(mem, registers);

				if (nextDecode != null)
					System.out.println(
							"We Fetched this instruction: " + displayInstruction(nextDecode) + " >> " + nextDecode);

			}
			if (nextDecode == null && nextWriteBack == null && nextExecute == null && nextMem == null)
				break;
			cycle++;

		}
		processor.printAll(mem, registers);

	}

	public void printAll(Memory mem, RegisterFile reg) {
		System.out.println();
		System.out.println("---------------------------------------");
		System.out.println("Printing memory places with values only");
		for (int i = 0; i < 2034; i++) {
			if (mem.memory[i] != null)
				System.out.println("Mem " + i + "  has " + mem.memory[i]);
		}
		System.out.println();
		System.out.println("---------------------------------------");
		System.out.println("Printing Registers");
		for (int i = 0; i < 32; i++) {
			System.out.println("Register" + i + "  has " + reg.generalPurpose[i].getWord());
		}

	}

	public static int convertToInteger(String binary) {
		int result = 0;
		int bitSize = binary.length();

		// Check if the number is negative (based on two's complement representation)
		boolean isNegative = binary.charAt(0) == '1';

		// Perform conversion for positive numbers
		if (!isNegative) {
			for (int i = 0; i < bitSize; i++) {
				if (binary.charAt(i) == '1') {
					result |= (1 << (bitSize - 1 - i)); // Set the corresponding bit to 1
				}
			}
		}
		// Perform conversion for negative numbers (two's complement)
		else {
			// Invert the bits
			StringBuilder inverted = new StringBuilder();
			for (int i = 0; i < bitSize; i++) {
				inverted.append(binary.charAt(i) == '0' ? '1' : '0');
			}

			// Add 1 to the inverted value
			int carry = 1;
			StringBuilder twosComplement = new StringBuilder(inverted.toString());
			for (int i = bitSize - 1; i >= 0; i--) {
				char bit = inverted.charAt(i);
				if (bit == '1' && carry == 1) {
					twosComplement.setCharAt(i, '0');
				} else if (bit == '0' && carry == 1) {
					twosComplement.setCharAt(i, '1');
					carry = 0;
				}
			}

			// Convert the two's complement to integer
			for (int i = 0; i < bitSize; i++) {
				if (twosComplement.charAt(i) == '1') {
					result |= (1 << (bitSize - 1 - i)); // Set the corresponding bit to 1
				}
			}

			// Negate the result
			result = -result;
		}

		return result;
	}

	public static void main(String[] args) throws FileNotFoundException {

		FilletONeumann processor = new FilletONeumann();
		Memory mem = new Memory();
		mem.insert(readfile("src/instructions.txt"));
		RegisterFile regFile = new RegisterFile();
		compiper(mem, regFile, processor);
//		System.out.println((immediateVal("-5")));
//		System.out.println(-Integer.parseInt("1111111111111111011",2));
//		// Convert the binary string to a negative integer
//		int parsedInt = Integer.parseInt("1111111111111111011", 2);
//		int bitSize = "1111111111111111011".length();
//		int maxValue = (int) Math.pow(2, bitSize - 1); // Maximum value for the given bit size
//		int negativeInt = parsedInt - maxValue;

//		System.out.println(negativeInt); // Output: -3
//		String binary = immediateVal("-3"); // Example binary number in two's complement // Example binary number in
//											// two's complement representation
//		System.out.println(binary);
//		int result = convertToInteger(binary);
//
//		System.out.println(result);

//		Instructions s = new Instructions(mem.memory[11], regFile);
//		System.out.println(s.opcode);
//		System.out.println(Long.parseLong(mem.memory[8],2));
//		System.out.println(Integer.parseInt(regFile.generalPurpose[4].getWord(),2));
//		System.out.println(Integer.parseInt(mem.memory[1032],2));

		// 00100000100100000110000000000000
		// 00100000100100000110000000000000

	}

}
