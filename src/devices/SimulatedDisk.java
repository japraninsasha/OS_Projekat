package devices;

import java.util.ArrayList;
import java.util.List;

public class SimulatedDisk {
    private int currentPosition;
    private List<DiskRequest> requestQueue;

    public SimulatedDisk() {
        this.currentPosition = 0;
        this.requestQueue = new ArrayList<>();
    }

    public void addRequest(DiskRequest request) {
        requestQueue.add(request);
    }

    public void processNextRequest() {
        if (requestQueue.isEmpty()) {
            System.out.println("No pending disk requests.");
            return;
        }

        // Find the request with the shortest seek time
        DiskRequest nextRequest = findShortestSeekTimeRequest();
        requestQueue.remove(nextRequest);

        // Move the disk head to the request position
        currentPosition = nextRequest.getPosition();
        System.out.println("Processing request at position: " + currentPosition);

        if (nextRequest.isRead()) {
            // Simulate reading data from disk
            System.out.println("Read operation completed.");
        } else {
            // Simulate writing data to disk
            System.out.println("Write operation completed.");
        }
    }

    private DiskRequest findShortestSeekTimeRequest() {
        DiskRequest shortestSeekTimeRequest = null;
        int shortestSeekTime = Integer.MAX_VALUE;

        for (DiskRequest request : requestQueue) {
            int seekTime = Math.abs(currentPosition - request.getPosition());
            if (seekTime < shortestSeekTime) {
                shortestSeekTime = seekTime;
                shortestSeekTimeRequest = request;
            }
        }

        return shortestSeekTimeRequest;
    }

    public void printRequestQueue() {
        System.out.println("Current request queue:");
        for (DiskRequest request : requestQueue) {
            System.out.println("Position: " + request.getPosition() + ", Type: " + (request.isRead() ? "Read" : "Write"));
        }
    }
}

