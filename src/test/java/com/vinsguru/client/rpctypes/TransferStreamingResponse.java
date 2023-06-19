package com.vinsguru.client.rpctypes;

import com.vinsguru.models.TransferResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class TransferStreamingResponse implements StreamObserver<TransferResponse> {
    private CountDownLatch latch;

    public TransferStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(TransferResponse value) {
        System.out.println("Status: " + value.getStatus());
        value.getAccountsList()
                .stream()
                .map(account -> account.getAccountNumber() + ": " + account.getAmount())
                .forEach(System.out::println);
        System.out.println("--------------------------");
    }

    @Override
    public void onError(Throwable t) {
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("All transfers done");
        this.latch.countDown();

    }
}
