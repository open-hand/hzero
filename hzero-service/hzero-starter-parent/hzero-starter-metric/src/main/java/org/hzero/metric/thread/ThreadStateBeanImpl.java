package org.hzero.metric.thread;

public class ThreadStateBeanImpl implements ThreadStateBean {

    public int getThreadStatusNEWCount() {
        return getStatusCount(Thread.State.NEW);
    }

    public int getThreadStatusRUNNABLECount() {
        return getStatusCount(Thread.State.RUNNABLE);
    }

    public int getThreadStatusBLOCKEDCount() {
        return getStatusCount(Thread.State.BLOCKED);
    }

    public int getThreadStatusWAITINGCount() {
        return getStatusCount(Thread.State.WAITING);
    }

    public int getThreadStatusTIMEDWAITINGCount() {
        return getStatusCount(Thread.State.TIMED_WAITING);
    }

    public int getThreadStatusTERMINATEDCount() {
        return getStatusCount(Thread.State.TERMINATED);
    }

    private int getStatusCount(Thread.State state) {
        Thread[] threads = ThreadUtilities.getAllThreads(state);

        return threads.length;
    }
}
