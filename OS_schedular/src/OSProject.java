import java.util.*;
import java.util.concurrent.Semaphore;

public class OSProject {
    public static void main(String[] args) {
        List<Process> inputQueue = new ArrayList<>();
        List<Process> readyQueue = new ArrayList<>();
        int[] systemScore = {0};
        Semaphore inputSemaphore = new Semaphore(1);
        Semaphore readySemaphore = new Semaphore(1);
        Semaphore scoreSemaphore = new Semaphore(1);

        ProcessGenerator generator = new ProcessGenerator(inputQueue, inputSemaphore);
        Scheduler scheduler = new Scheduler(inputQueue, readyQueue, 20, inputSemaphore, readySemaphore);

        Thread generatorThread = new Thread(generator);
        generatorThread.start();

        Thread schedulerThread = new Thread(scheduler::schedule);
        schedulerThread.start();

        List<Thread> cpuThreads = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {// سه تا سی پی یو باید تولید
            Thread cpuThread = new Thread(new CPU(i, readyQueue, systemScore, readySemaphore, scoreSemaphore));
            cpuThreads.add(cpuThread);
            cpuThread.start();
        }

        try {
            Thread.sleep(30000); // Run the simulation for 30 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        generatorThread.interrupt();
        schedulerThread.interrupt();
        cpuThreads.forEach(Thread::interrupt);

        System.out.println("Simulation ended.");
        System.out.println("Final System Score: " + systemScore[0]);
    }
}
