package kernel;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Comparator;

import assembler.Operations;
import fileSystem.FileSystem;
import memory.MemoryManager;
import memory.Ram;
import shell.Shell;

public class ProcessScheduler extends Thread {

    public static PriorityQueue<Process> readyQueue;
    public static ArrayList<Process> allProcesses = new ArrayList<>();
    private static int nextPID = 1;

    public ProcessScheduler() {
        readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getRemainingBurstTime));
    }

    @Override
    public void run() {
        while (!readyQueue.isEmpty()) {
            Process next = readyQueue.poll();
            executeProcess(next);
        }
        System.out.println("There are no processes to be executed");
    }

    private void executeProcess(Process process) {
        Shell.currentlyExecuting = process;
        int startAddress;
        if (process.getPcValue() == -1) {
            System.out.println("Process " + process.getName() + " started to execute");
            startAddress = Shell.manager.loadProcess(process);
        } else {
            System.out.println("Process " + process.getName() + " is executing again");
            startAddress = Shell.manager.loadProcess(process);
        }
        if (startAddress == -1) {
            System.out.println("Not enough memory to load process " + process.getName());
            return;
        }
        process.setStartAddress(startAddress);
        Shell.base = startAddress;
        Shell.limit = process.getInstructions().size();
        Shell.PC = (process.getPcValue() == -1) ? 0 : process.getPcValue();
        Shell.loadValues();
        process.setState(ProcessState.RUNNING);
        execute(process);
    }

    private void execute(Process process) {
        while (process.getState() == ProcessState.RUNNING) {
            if (Shell.PC >= Shell.limit) {
                process.setState(ProcessState.TERMINATED);
                System.out.println("Error: Program counter out of bounds in process " + process.getName());
                break;
            }
            int temp = Ram.getAt(Shell.PC + Shell.base);
            String instruction = Shell.fromIntToInstruction(temp);
            Shell.IR = instruction;
            Shell.executeMachineInstruction();
            process.setRemainingBurstTime(process.getRemainingBurstTime() - 1);

            if (process.getRemainingBurstTime() <= 0) {
                process.setState(ProcessState.DONE);
                break;
            }
        }
        if (process.getState() == ProcessState.BLOCKED) {
            System.out.println("Process " + process.getName() + " is blocked");
            Shell.saveValues();
        } else if (process.getState() == ProcessState.TERMINATED) {
            System.out.println("Process " + process.getName() + " is terminated");
            MemoryManager.removeProcess(process);
        } else if (process.getState() == ProcessState.DONE) {
            System.out.println("Process " + process.getName() + " is done");
            MemoryManager.removeProcess(process);
            if (!createOutputFileForProcess(process)) {
                System.out.println("Not enough space, cannot create file");
            } else {
                System.out.println("Output file created for process: " + process.getName());
            }
        }
        Operations.clearRegisters();
    }

    private boolean createOutputFileForProcess(Process process) {
        String fileName = process.getProcessName() + "_output.txt";
        String content = "Process " + process.getProcessName() + " has completed execution. Result: "+Operations.R4.value;
        return FileSystem.createFile(fileName, content.getBytes());
    }

    public static void blockProcess(Integer pid) {
        if (pid < allProcesses.size()) {
            allProcesses.get(pid).setState(ProcessState.BLOCKED);
            return;
        }
        System.out.println("Process with PID " + pid + " doesn't exist, check and try again");
    }

    public static void unblockProcess(Integer pid) {
        if (pid < allProcesses.size()) {
            allProcesses.get(pid).setState(ProcessState.READY);
            readyQueue.add(allProcesses.get(pid));
            return;
        }
        System.out.println("Process with PID " + pid + " doesn't exist, check and try again");
    }

    public void terminateProcess(Integer pid) {
        if (pid < allProcesses.size()) {
            allProcesses.get(pid).setState(ProcessState.TERMINATED);
            MemoryManager.removeProcess(allProcesses.get(pid));
            return;
        }
        System.out.println("Process with PID " + pid + " doesn't exist, check and try again");
    }

    public int getNewPID() {
        return nextPID++;
    }

    public static void addProcess(Process process) {
        allProcesses.add(process);
        readyQueue.add(process);
    }

    public void listOfProcesses() {
        System.out.println("PID\tProgram\t\tSize\tState\t\tCurrent occupation of memory");
        for (Process process : allProcesses) {
            System.out.println(process.getProcessId() + "\t" + process.getProcessName() + "\t " + process.getMemoryRequirement() + "\t"
                    + process.getProcessState()
                    + (process.getProcessState().toString().length() > 8
                    ? "\t " + MemoryManager.memoryOccupiedByProcess(process)
                    : "\t\t " + MemoryManager.memoryOccupiedByProcess(process)));
        }
    }

    public void executeSpecificProcess(int pid) {
        Process processToExecute = null;
        for (Process process : readyQueue) {
            if (process.getProcessId() == pid) {
                processToExecute = process;
                break;
            }
        }

        if (processToExecute != null) {
            readyQueue.remove(processToExecute);
            executeProcess(processToExecute);
        } else {
            System.out.println("Process with PID " + pid + " doesn't exist in ready queue.");
        }
    }


    public Queue<Process> getReadyQueue() {
        return readyQueue;
    }
}
