package inc.visor.voom_service.osrm.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Service
public class OsrmQueueService {

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public OsrmQueueService() {
        Thread worker = new Thread(this::processQueue, "osrm-worker");
        worker.setDaemon(true);
        worker.start();
    }

    public void submit(Runnable task) {
        queue.offer(task);
    }

    private void processQueue() {
        while (true) {
            try {
                Runnable task = queue.take();
                task.run();
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


