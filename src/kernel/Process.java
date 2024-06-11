package kernel;

public class Process {
    private int processId;
    private String processName;
    private int totalBurstTime;
    private int remainingBurstTime;
    private int memoryRequirement;
    private ProcessState processState;

    public Process(int processId, String processName, int totalBurstTime, int memoryRequirement) {
        this.processId = processId;
        this.processName = processName;
        this.totalBurstTime = totalBurstTime;
        this.remainingBurstTime = totalBurstTime;
        this.memoryRequirement = memoryRequirement;
        this.processState = ProcessState.NEW;
    }

    // Getters and setters
    public int getProcessId() { return processId; }
    public String getProcessName() { return processName; }
    public int getTotalBurstTime() { return totalBurstTime; }
    public int getRemainingBurstTime() { return remainingBurstTime; }
    public void setRemainingBurstTime(int remainingBurstTime) { this.remainingBurstTime = remainingBurstTime; }
    public int getMemoryRequirement() { return memoryRequirement; }
    public ProcessState getProcessState() { return processState; }
    public void setProcessState(ProcessState processState) { this.processState = processState; }
}