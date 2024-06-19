package devices;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

public class SimulatedDisk {
    private Queue<DiskRequest> requestQueue;

    public SimulatedDisk() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(DiskRequest request) {
        requestQueue.add(request);
        System.out.println("Added request: " + request);
    }

    public void processNextRequest() {
        if (!requestQueue.isEmpty()) {
            DiskRequest request = requestQueue.poll();
            System.out.println("Processing request: " + request);
            writeToFile(request);
        }
    }

    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }

    public void printRequestQueue() {
        System.out.println("Current request queue: " + requestQueue);
    }

    private void writeToFile(DiskRequest request) {
        if (request != null && request.getContent() != null) {
            Path path = Paths.get("output_files", "file_" + request.getStartBlock() + ".txt");
            try {
                Files.createDirectories(path.getParent());
                try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                    fos.write(request.getContent());
                    System.out.println("File written to: " + path.toAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
