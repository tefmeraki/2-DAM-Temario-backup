package edu.thepower.psp.u2programacionmultihilo;

public class U2E10DaemonDemo {
    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) throws InterruptedException {
        Thread heartbeat = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("[daemon]   ♥ tick");
                    sleep(200);
                }
            } finally {
                // This will likely NOT print: JVM halts daemons without running finally.
                System.out.println("[daemon]   shutting down");
            }
        }, "heartbeat");
        heartbeat.setDaemon(true); // <-- make it a daemon

        Thread workerA = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                System.out.println("[workerA]  step " + i);
                sleep(300);
            }
            System.out.println("[workerA]  done");
        }, "workerA"); // non-daemon by default

        Thread workerB = new Thread(() -> {
            for (int i = 1; i <= 6; i++) {
                System.out.println("[workerB]  step " + i);
                sleep(450);
            }
            System.out.println("[workerB]  done");
        }, "workerB"); // non-daemon by default

        System.out.println("[main]     starting threads");
        heartbeat.start();
        workerA.start();
        workerB.start();

        sleep(900);
        System.out.println("[main]     exiting main now"); // main ends here

        // Optional: if you join one worker, you’ll still see the daemon run until BOTH finish.
        // workerA.join();
        // workerB.join();
        // Program ends; the daemon is terminated by the JVM at this point.
    }
}
