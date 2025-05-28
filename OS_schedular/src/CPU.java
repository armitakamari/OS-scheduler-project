// runnable یک interface است
//thread یک کلاس  است
// چونکه بعدا بتوانیم از بقیه ی کلاس ها ارث بری کنیم که حالا در این مسیله نکردم از اینترفیس runnable استفاده میکنیم
import java.util.*;
import java.util.concurrent.Semaphore;

class CPU implements Runnable {
    private final int id;// هر سی پی یو ای یک ای دی دارد که تشخیص دهیم الان کدام سی پی یو است .
    private final List<Process> readyQueue;
    private final int[] systemScore;
    private final Semaphore readySemaphore;
    private final Semaphore scoreSemaphore;

    public CPU(int id, List<Process> readyQueue, int[] systemScore, Semaphore readySemaphore, Semaphore scoreSemaphore) {
        this.id = id;
        this.readyQueue = readyQueue;
        this.systemScore = systemScore;
        this.readySemaphore = readySemaphore;
        this.scoreSemaphore = scoreSemaphore;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Process process = null;
                readySemaphore.acquire();

                synchronized (readyQueue) { // جلوگیری از Race Condition
                    if (!readyQueue.isEmpty()) {
                        process = readyQueue.remove(0); //  فرایندهاراازصف اماده خارج میکند
                    }
                }
                readySemaphore.release();

                if (process != null) {
                    executeProcess(process);// نبوده باشد ا اجرا میکند  فرایند خارج شده را در صورت اینکه null
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void executeProcess(Process process) throws InterruptedException {
        long currentTime = System.currentTimeMillis();// زمان فعلی سیستم ذخیره میشود

        if (process.startDeadline < currentTime) { // اگر از زمانیکه فرایند باید شروع میشد گذشته شده باشد باید گفته شود که ددلاینش میس شده است و امتیاز ان از امتیاز سیستم کم شود
            System.out.println("Process " + process.id + " missed start deadline. Deducting score.");
            updateScore(-process.value);
            //readyQueue.remove(process);////////??????????????????????????????????????????????
            System.out.println("Process "+ process.id + " removed from readyQueue");
            return;
        }

        System.out.println("CPU " + id + " executing Process: " + process.id);
        Thread.sleep(process.executionTime * 400); // زمان شبیه‌سازی بهینه‌تر شد

        currentTime = System.currentTimeMillis();
        if (currentTime > process.endDeadline) {
            System.out.println("Process " + process.id + " missed end deadline. Deducting score.");
            updateScore(-process.value);
            //readyQueue.remove(process);//??????????????????????????????????????????????????
            System.out.println("Process "+ process.id + " removed from readyQueue");
        } else {
            System.out.println("CPU " + id + " completed Process: " + process.id);
            updateScore(process.value);
        }
    }

    private void updateScore(int delta) throws InterruptedException {
        scoreSemaphore.acquire();
        try {
            systemScore[0] += delta;
        } finally {
            scoreSemaphore.release();
        }
    }
}
