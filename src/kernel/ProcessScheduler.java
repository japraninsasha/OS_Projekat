package kernel;

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
        p.setProcessState(ProcessState.RUNNING);
        System.out.println("Running process: " + p.getProcessName());
        int timeSlice = Math.min(10, p.getRemainingBurstTime());
        try {
            Thread.sleep(timeSlice * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        p.setRemainingBurstTime(p.getRemainingBurstTime() - timeSlice);
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

    public synchronized void stopScheduler() {
        isRunning = false;
        this.interrupt();
    }
}
