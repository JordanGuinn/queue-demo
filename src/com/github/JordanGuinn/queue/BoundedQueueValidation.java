package com.github.JordanGuinn.queue;

import com.github.JordanGuinn.queue.concurrent.QueueThreadFactory;
import com.github.JordanGuinn.queue.concurrent.QueueThreadType;
import com.github.JordanGuinn.queue.model.BoundedQueue;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

/**
 * This is the main class of the application, responsible for verifying the behavior of the
 * BoundedQueue implementation to any interested parties.
 */
public class BoundedQueueValidation {
    private static final Supplier<Double> doubleSupplier = () -> new Random().nextDouble();

    private static final int threadTypeCount = 5;
    private static final int elementCountPerThread = 1000;
    private static final int queueCapacity = 300;

    public static void main(String[] args) {
        System.out.println("The purpose of this class is to demonstrate the behavior of the thread-safe BoundedQueue implementation.");
        System.out.println("We will concurrently operate on a BoundedQueue instance based on the following parameters:");
        System.out.println();
        System.out.println("Thread Count (Per Type): " + threadTypeCount);
        System.out.println("Element Count Per Thread: " + elementCountPerThread);
        System.out.println("Total Queue Capacity: " + queueCapacity);
        System.out.println();
        System.out.println("Let's begin!");

        System.out.println();

        CountDownLatch latch = new CountDownLatch(threadTypeCount * 2);

        BoundedQueue<Double> mainQueue = new BoundedQueue<>(queueCapacity);
        QueueThreadFactory threadFactory = new QueueThreadFactory<>(mainQueue, doubleSupplier);

        for (int i = 0; i < threadTypeCount; i++) {
            threadFactory.getThread(QueueThreadType.Producer, elementCountPerThread, latch).start();
            threadFactory.getThread(QueueThreadType.Consumer, elementCountPerThread, latch).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("One or more threads interrupted while manipulating queue.  Exiting early...");
            System.exit(1);
        }

        System.out.println();
        System.out.println("All Producer & Consumer Threads have completed!  Queue size is " + mainQueue.size() + ".");
        System.exit(0);
    }
}
