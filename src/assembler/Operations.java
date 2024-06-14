package assembler;

import kernel.ProcessState;
import shell.Shell;

public class Operations {

        public static final String halt = "0000";
        public static final String load = "0001";
        public static final String store = "0010";
        public static final String add = "0011";
        public static final String sub = "0100";
        public static final String mul = "0101";
        public static final String div = "0110";
        public static final String jmp = "0111";
        public static final String jmpz = "1000";
        public static final String jmpn = "1001";
        public static final String inc = "1010";
        public static final String dec = "1011";

        public static Register R1 = new Register("R1", Constants.R1, 0);
        public static Register R2 = new Register("R2", Constants.R2, 0);
        public static Register R3 = new Register("R3", Constants.R3, 0);
        public static Register R4 = new Register("R4", Constants.R4, 0);

        private static int ACC = 0;  // Implicitni akumulator

        public static void load(String reg) {
            Register r = getRegister(reg);
            if (r != null) {
                ACC = r.value;
            }
        }

        public static void store(String reg) {
            Register r = getRegister(reg);
            if (r != null) {
                r.value = ACC;
            }
        }

        public static void add(String val) {
            if (val.length() == 8) { // Vrednost
                ACC += Integer.parseInt(val, 2);
            } else if (val.length() == 4) { // Registar
                Register r = getRegister(val);
                if (r != null) {
                    ACC += r.value;
                }
            }
        }

        public static void sub(String val) {
            if (val.length() == 8) { // Vrednost
                ACC -= Integer.parseInt(val, 2);
            } else if (val.length() == 4) { // Registar
                Register r = getRegister(val);
                if (r != null) {
                    ACC -= r.value;
                }
            }
        }

        public static void mul(String val) {
            if (val.length() == 8) { // Vrednost
                ACC *= Integer.parseInt(val, 2);
            } else if (val.length() == 4) { // Registar
                Register r = getRegister(val);
                if (r != null) {
                    ACC *= r.value;
                }
            }
        }

        public static void div(String val) {
            if (val.length() == 8) { // Vrednost
                ACC /= Integer.parseInt(val, 2);
            } else if (val.length() == 4) { // Registar
                Register r = getRegister(val);
                if (r != null) {
                    ACC /= r.value;
                }
            }
        }

        public static void inc() {
            ACC += 1;
        }

        public static void dec() {
            ACC -= 1;
        }

        public static void halt() {
            Shell.currentlyExecuting.setProcessState(ProcessState.DONE);
        }

        public static void jmp(String adr) {
            int temp = Integer.parseInt(adr, 2);
            if (temp >= Shell.limit) {
                Shell.currentlyExecuting.setProcessState(ProcessState.TERMINATED);
                System.out.println("Error with address in process " + Shell.currentlyExecuting.getProcessName());
                return;
            }
            Shell.PC = temp;
        }


        private static Register getRegister(String adr) {
            switch (adr) {
                case Constants.R1:
                    return R1;
                case Constants.R2:
                    return R2;
                case Constants.R3:
                    return R3;
                case Constants.R4:
                    return R4;
                default:
                    return null;
            }
        }

        public static void printRegisters() {
            System.out.println("Registers:");
            System.out.println("R1 value - [ " + R1.value + " ]");
            System.out.println("R2 value - [ " + R2.value + " ]");
            System.out.println("R3 value - [ " + R3.value + " ]");
            System.out.println("R4 value - [ " + R4.value + " ]");
            System.out.println("ACC value - [ " + ACC + " ]");
        }

        public static void clearRegisters() {
            R1.value = 0;
            R2.value = 0;
            R3.value = 0;
            R4.value = 0;
            ACC = 0;
        }


}
