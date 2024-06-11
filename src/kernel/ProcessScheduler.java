package kernel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ProcessScheduler {
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

    public synchronized void schedule() {
        isRunning = true;
        while (isRunning && !processQueue.isEmpty()) {
            Process p = processQueue.poll();
            p.setProcessState(ProcessState.RUNNING);
            System.out.println("Running process: " + p.getProcessName());
            // Simulate process running
            int timeSlice = Math.min(10, p.getRemainingBurstTime()); // Run for a maximum of 10 time units
            try {
                Thread.sleep(timeSlice * 100); // Simulating time taken by process
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            p.setRemainingBurstTime(p.getRemainingBurstTime() - timeSlice);
            if (p.getRemainingBurstTime() > 0) {
                // Process is not finished, add it back to the queue
                processQueue.add(p);
            } else {
                p.setProcessState(ProcessState.TERMINATED);
                System.out.println("Process " + p.getProcessName() + " finished.");
            }
        }
        isRunning = false;
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
            // Remove the process from the queue if it's still there
            processQueue.remove(p);
        } else {
            System.out.println("Process not found or already terminated.");
        }
    }

    public synchronized void stopScheduler() {
        isRunning = false;
    }
}