package org.hzero.metric.metrics;

import static java.util.Collections.emptyList;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.lang.NonNullApi;
import io.micrometer.core.lang.NonNullFields;

import org.hzero.metric.thread.ThreadStateBean;
import org.hzero.metric.thread.ThreadStateBeanImpl;

@NonNullApi
@NonNullFields
public class ThreadMetrics implements MeterBinder {
    private final Iterable<Tag> tags;

    public ThreadMetrics() {
        this(emptyList());
    }

    public ThreadMetrics(Iterable<Tag> tags) {
        this.tags = tags;
    }

    private final ThreadStateBean threadStateBean = new ThreadStateBeanImpl();

    @Override
    public void bindTo(MeterRegistry registry) {

        Gauge.builder("jvm.thread.NEW.sum", threadStateBean, ThreadStateBean::getThreadStatusNEWCount)
                .tags(tags)
                .description("thread state NEW count")
                .register(registry);
        Gauge.builder("jvm.thread.RUNNABLE.sum", threadStateBean, ThreadStateBean::getThreadStatusRUNNABLECount)
                .tags(tags)
                .description("thread state RUNNABLE count")
                .register(registry);

        Gauge.builder("jvm.thread.BLOCKED.sum", threadStateBean, ThreadStateBean::getThreadStatusBLOCKEDCount)
                .tags(tags)
                .description("thread state BLOCKED count")
                .register(registry);

        Gauge.builder("jvm.thread.WAITING.sum", threadStateBean, ThreadStateBean::getThreadStatusWAITINGCount)
                .tags(tags)
                .description("thread state WAITING count")
                .register(registry);

        Gauge.builder("jvm.thread.TIMEDWAITING.sum", threadStateBean, ThreadStateBean::getThreadStatusTIMEDWAITINGCount)
                .tags(tags)
                .description("thread state TIMED_WAITING count")
                .register(registry);

        Gauge.builder("jvm.thread.TERMINATED.sum", threadStateBean, ThreadStateBean::getThreadStatusTERMINATEDCount)
                .tags(tags)
                .description("thread state TERMINATED count")
                .register(registry);
    }
}
