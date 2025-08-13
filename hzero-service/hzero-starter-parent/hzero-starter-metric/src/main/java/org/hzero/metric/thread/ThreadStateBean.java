package org.hzero.metric.thread;

public interface ThreadStateBean {

    int getThreadStatusNEWCount();

    int getThreadStatusRUNNABLECount();

    int getThreadStatusBLOCKEDCount();

    int getThreadStatusWAITINGCount();

    int getThreadStatusTIMEDWAITINGCount();

    int getThreadStatusTERMINATEDCount();
}
