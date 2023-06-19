package com.vinsguru.client.rpctypes;

import com.vinsguru.models.Balance;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceStreamObserver implements StreamObserver<Balance> {
    private CountDownLatch latch;

    public BalanceStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Balance value) {
        System.out.println(
                "Final Balance: " + value.getAmount()
        );
    }

    @Override
    public void onError(Throwable t) {
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server is done!");
        this.latch.countDown();
    }
}
