import java.util.*;
import java.util.concurrent.Semaphore;

// ناحیه ی بحرانی ما در اصل در این مسیله دوتا صف های ما هستند.
class Scheduler {
    private final List<Process> inputQueue; //generator ر اصل تمامی فرایند هایی که تولید میکند  داخل این صف میگذارد
    private final List<Process> readyQueue;// صفیکه این زمانبند ما تصمیم میگیرد که که کدام فرایند ها را از صف ورودی به صف اماده منتقل کند.
    private final int readyQueueSize;// اندازه ی صف اماده که گفته شده است باید 20 تا ظرفیت داشته باشد .
    private final Semaphore inputSemaphore;
    private final Semaphore readySemaphore;

    public Scheduler(List<Process> inputQueue, List<Process> readyQueue, int readyQueueSize, Semaphore inputSemaphore, Semaphore readySemaphore) {
        this.inputQueue = inputQueue;
        this.readyQueue = readyQueue;
        this.readyQueueSize = readyQueueSize;
        this.inputSemaphore = inputSemaphore;
        this.readySemaphore = readySemaphore;
    }

    public void schedule() {
        while (true) {
            try {
                Thread.sleep(100); // Reduce delay between scheduling cycles////؟؟؟؟؟؟؟؟؟
                inputSemaphore.acquire();
                long currentTime = System.currentTimeMillis();
                Iterator<Process> iterator = inputQueue.iterator();// این روی صف ورودی یک iteration میزند.
                while (iterator.hasNext()) {
                    Process process = iterator.next();

                    // فرایندیکه زمان شروع ان یعنی ددلاین شروع ان از زمان فعلی گذشته شده باشد باید از سیستم حذف شود.
                    if (process.startDeadline < currentTime) {
                        System.out.println("Process " + process.id + " missed start deadline and was removed.");
                        iterator.remove();
                        continue;
                    }

                    readySemaphore.acquire();
                    if (readyQueue.size() < readyQueueSize) {// اگر هنوز صف ورودی ما پر نشده باشد میتوانیم از صف ورودی بازم وارد کنیم و نیازی نیست تصمیم بگیریم.
                        readyQueue.add(process);
                        iterator.remove();
                        System.out.println("Scheduled Process: " + process.id);// وقتیکه یک فرایند وارد صف امائه میشود این پیام چاپ میشود.
                    } else { // در غیر این صورت یعنی صف ما پر است پس باید بین دوتا صف جا به جایی داشته باشیم و از صف ورودی به صف اماده  انتقال بدهیم و یا  فرایندیکه از این صف بوده است را ببرین داخل صف ورودی.
                        Process lowestPriorityProcess = readyQueue.stream()
                                .min(Comparator.comparingInt((Process p) -> p.value).thenComparingLong(p -> p.endDeadline))
                                .orElse(null); // ابتدا فرایندیکه کمترین مقدار را دارد را پیدا میکنیم و اگر ئ. تا فرایند اینطوری دداشتیم اونی را انتخاب میکنیم که ددلاین ان دیر تر باشد

                        if (lowestPriorityProcess != null && (process.value > lowestPriorityProcess.value ||
                                (process.value == lowestPriorityProcess.value && process.endDeadline < lowestPriorityProcess.endDeadline))) {
                            readyQueue.remove(lowestPriorityProcess);
                            readyQueue.add(process);
                           // inputQueue.add(lowestPriorityProcess);/// ????????????????????????????????????????????????????????????????????????????? testing
                            iterator.remove();
                            System.out.println("Replaced Process " + lowestPriorityProcess.id + " with Process " + process.id);// وقتیکه دوتا فرایند را باهم جابه جا میکنیم این پیام چاپ میشود.
                        }
                    }
                    readySemaphore.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                inputSemaphore.release();
                readySemaphore.release();
            }
        }
    }
}