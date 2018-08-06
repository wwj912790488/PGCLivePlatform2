package com.arcvideo.pgcliveplatformserver.service.task;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class TaskQueueHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskQueueHandler.class);

    private final DelayQueue<DelayedTaskAttribute> delayedTaskAttributeDelayQueue = new DelayQueue<>();
    private Thread delayConsumer;
    private volatile boolean shouldConsumeQueue = true;

    protected void initTaskQueue(String consumerName) {
        logger.info("enter TaskQueueHandler thread");
        if (delayConsumer == null) {
            delayConsumer = new Thread(this);
            delayConsumer.setName(consumerName);
        }
        shouldConsumeQueue = true;
        delayConsumer.start();
    }

    protected void unInitTaskQueue() {
        this.shouldConsumeQueue = false;
        if (delayConsumer != null && !delayConsumer.isInterrupted()) {
            delayConsumer.interrupt();
        }
        delayConsumer = null;
        delayedTaskAttributeDelayQueue.clear();
        logger.info("unInitTaskQueue TaskQueueHandler thread");
    }

    public void addTask(String key, Object object, long delayMilliSeconds) {
        DelayedTaskAttribute delayedTaskAttribute = new DelayedTaskAttribute(key, object, delayMilliSeconds);
        delayedTaskAttributeDelayQueue.put(delayedTaskAttribute);
    }

    public void addTask(Object object) {
        DelayedTaskAttribute delayedTaskAttribute = new DelayedTaskAttribute(object);
        delayedTaskAttributeDelayQueue.put(delayedTaskAttribute);
    }

    public void removeTask(String key, Object object) {
        DelayedTaskAttribute delayedTaskAttribute = new DelayedTaskAttribute(key, object, 0);
        delayedTaskAttributeDelayQueue.remove(delayedTaskAttribute);
    }

    protected abstract void taskActionCallback(Object object);

    @Override
    public void run() {
        logger.info("enter Delay TaskQueueHandler consumer---" + delayConsumer.getName());
        while (!delayConsumer.isInterrupted() && shouldConsumeQueue) {
            final DelayedTaskAttribute delayedTaskAttribute;
            try {
                delayedTaskAttribute = delayedTaskAttributeDelayQueue.take();
            } catch (InterruptedException e) {
                logger.error("Delay TaskQueueHandler consumer exit cause by InterruptedException:{} : {}", delayConsumer.getName(), e);
                continue;
            }
            logger.debug("get a TaskQueueHandler consumer:{} : {}", delayConsumer.getName(), delayedTaskAttribute);
            taskActionCallback(delayedTaskAttribute.getTask());
        }
        logger.info("leave Delay TaskQueueHandler consumer---" + delayConsumer.getName());
    }

    private class DelayedTaskAttribute<T> implements Delayed {
        private final String key;
        private final T task;
        private final long startTime;

        public DelayedTaskAttribute(T task) {
            this.key = null;
            this.task = task;
            this.startTime = System.currentTimeMillis();
        }

        public DelayedTaskAttribute(String key, T task, long delayMilliSeconds) {
            this.key = key;
            this.task = task;
            this.startTime = System.currentTimeMillis() + delayMilliSeconds;
        }

        public T getTask() {
            return task;
        }

        @SuppressWarnings("all")
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @SuppressWarnings("all")
        @Override
        public int compareTo(Delayed o) {
            return new CompareToBuilder()
                    .append(getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS))
                    .build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DelayedTaskAttribute<?> attribute = (DelayedTaskAttribute<?>) o;

            if (key == null || !key.equals(attribute.key)) return false;
            return task.getClass() == attribute.task.getClass();
        }

        @Override
        public int hashCode() {
            int result = (key != null ? key.hashCode() : 0);
            result = 31 * result + task.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "DelayedTaskAttribute{" +
                    "task=" + ((task != null) ? task.toString() : "null") +
                    ", startTime=" + startTime +
                    '}';
        }
    }
}
