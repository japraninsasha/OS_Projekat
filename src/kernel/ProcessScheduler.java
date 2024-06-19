package kernel;

import shell.Shell;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ProcessScheduler extends Thread {
    private PriorityQueue<Process> processQueue;
    private Map<Integer, Process> processTable;
    private int nextPid;
    private boolean isRunning;

    public ProcessScheduler() {
        processQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.getRemainingBurstTime()));
        processTable = new HashMap<>();
        nextPid = 1;
        isRunning = false;
    }

    public synchronized int getNewPid() {
        return nextPid++;
    }

    public synchronized void addProcess(Process process) {
        process.setProcessState(ProcessState.READY);
        processQueue.add(process);
        processTable.put(process.getProcessId(), process);
        System.out.println("Process added: " + process.getProcessName());
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            Process p = getNextProcess();
            if (p != null) {
                executeProcess(p);
            }
        }
    }

    private synchronized Process getNextProcess() {
        if (!processQueue.isEmpty()) {
            return processQueue.poll();
        }
        return null;
    }

    private void executeProcess(Process p) {
        Shell.currentlyExecuting = p;
        Shell.loadValues(); // Učitaj vrednosti registara i PC

        p.setProcessState(ProcessState.RUNNING);
        System.out.println("Running process: " + p.getProcessName());

        while (p.getRemainingBurstTime() > 0 && p.getProcessState() == ProcessState.RUNNING) {
            if (Shell.PC >= Shell.limit) {
                p.setProcessState(ProcessState.TERMINATED);
                System.out.println("Error: Program counter out of bounds in process " + p.getProcessName());
                break;
            }

            Shell.IR = Shell.memory.getInstruction(Shell.PC); // Pretpostavimo da postoji metoda getInstruction
            Shell.executeMachineInstruction();

            p.setRemainingBurstTime(p.getRemainingBurstTime() - 1);

            try {
                Thread.sleep(100); // Simulacija vremena izvršavanja
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Shell.saveValues(); // Sačuvaj trenutne vrednosti registara i PC
        }

        if (p.getRemainingBurstTime() > 0) {
            p.setProcessState(ProcessState.READY);
            addProcess(p);
        } else {
            p.setProcessState(ProcessState.TERMINATED);
            System.out.println("Process " + p.getProcessName() + " finished.");
        }
    }

    public void listProcesses() {
        for (Process p : processTable.values()) {
            System.out.println("PID: " + p.getProcessId() + ", Name: " + p.getProcessName() + ", State: " + p.getProcessState() + ", Memory: " + p.getMemoryRequirement() + ", Remaining Burst Time: " + p.getRemainingBurstTime());
        }
    }

    public synchronized void killProcess(int pid) {
        Process p = processTable.get(pid);
        if (p != null && p.getProcessState() != ProcessState.TERMINATED) {
            p.setProcessState(ProcessState.TERMINATED);
            System.out.println("Process " + p.getProcessName() + " killed.");
            processQueue.remove(p);
        } else {
            System.out.println("Process not found or already terminated.");
        }
    }

    public synchronized void blockProcess(int pid) {
        Process p = processTable.get(pid);
        if (p != null && p.getProcessState() == ProcessState.RUNNING) {
            p.setProcessState(ProcessState.BLOCKED);
            System.out.println("Process " + p.getProcessName() + " blocked.");
        } else {
            System.out.println("Process not found or not running.");
        }
    }

    public synchronized void unblockProcess(int pid) {
        Process p = processTable.get(pid);
        if (p != null && p.getProcessState() == ProcessState.BLOCKED) {
            p.setProcessState(ProcessState.READY);
            processQueue.add(p);
            System.out.println("Process " + p.getProcessName() + " unblocked.");
        } else {
            System.out.println("Process not found or not blocked.");
        }
    }
}
