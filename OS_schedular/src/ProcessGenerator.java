import java.util.*;
import java.util.concurrent.Semaphore;
// runnable  در اصل یک متد دارد که run ست و الان تمامی ترد ها باید این  را اجرا کنند
class ProcessGenerator implements Runnable {
    private final List<Process> inputQueue;//تمامی فرایند هایی که یک تولیید کننده تولید میکند باید وارد این صف شود و ظرفیت ان نامحدود است .
    private final Random random = new Random();
    private int processCount = 0;
    private final Semaphore inputSemaphore;

    public ProcessGenerator(List<Process> inputQueue, Semaphore inputSemaphore) {
        this.inputQueue = inputQueue;
        this.inputSemaphore = inputSemaphore;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep( 1000); // فاصله زمانی بین تولید فرایندها با توجه به شبیه سازی های مختلف احتمالا انرا تغییر بدم .

                int batchSize = random.nextInt(6) + 5; // این در اصل تعداد فرایند هایی که در هر زمان که این فعال میشود  باید تولید کندحداکثر گفته شده است 10 تا باشد.
                inputSemaphore.acquire();
                long currentTime = System.currentTimeMillis();// در هر بار که این تولید کننده فعال میشود و میخواهد چند تا نخ تولید کند زمانیکه اینکه تولید میشود ذخیره میشود

                for (int i = 0; i < batchSize; i++) { // برای تمامی فرایند هایی که تولید میشند باید مقادیر این فرایند ها مقدار دهی شود البته به جز زمان ورود انها که به صورت رندوم نیست .
                    long arrivalTime = currentTime;
                    int executionTime = random.nextInt(5) + 1;
                    long startDeadline = arrivalTime + random.nextInt(5000) + 5000;
                    long endDeadline = startDeadline + random.nextInt(5000) + 5000;
                    int value = random.nextInt(100) + 1;

                    Process process = new Process(++processCount, arrivalTime, executionTime, startDeadline, endDeadline, value);
                    inputQueue.add(process);// اندازه ی صف ورودی ما بینهایت است  و در اصل این تولید کننده ی ما باید با این صف ورودی کار کند در ابتدا تمامی فرایند ها باید به این صف منتقل شود.
                    System.out.println("Generated Process: " + process.id + " at " + arrivalTime);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // اگر هر اتفاقی افتاد که به ارور منجر شد در ترد در اصل انرا متوقف نمی کنیم تنها یک فلگ برای ان میگذاریم. البته تصمیم نهایی برای خود ترد میگذارد .
               //در اصل به ترد میگوید متوقف شود اما تصمیم  نهایی راا برای خود ترد میکذارد.
                break;
            } finally {
                inputSemaphore.release();
            }
        }
    }
}
