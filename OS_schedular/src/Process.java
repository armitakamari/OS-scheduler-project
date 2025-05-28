// این کلاس مریوط به هر فرایند است و طبق صورت پروژه هر فرایند دارای ویژگی هایی است مثلا اینکه باید بوسیله
class Process {
    int id;// identifying each process with it's id
    long arrivalTime;//the time that the process enter to system  . we can't identify it random
    int executionTime;//that's cpu burst
    long startDeadline;//process should start at most this time
    long endDeadline;//process should end at this time
    // if it miss it's deadline we abort it from aour system .
    int value;//random in range of 1 to 100

    public Process(int id, long arrivalTime, int executionTime, long startDeadline, long endDeadline, int value) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.executionTime = executionTime;
        this.startDeadline = startDeadline;
        this.endDeadline = endDeadline;
        this.value = value;
    }
}
