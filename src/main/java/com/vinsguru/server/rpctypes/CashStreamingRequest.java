package com.vinsguru.server.rpctypes;

import com.vinsguru.models.Balance;
import com.vinsguru.models.DepositRequest;
import io.grpc.stub.StreamObserver;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {
    private StreamObserver<Balance> balanceStreamObserver;
    private int accountBalance;

    public CashStreamingRequest(StreamObserver<Balance> balanceStreamObserver) {
        this.balanceStreamObserver = balanceStreamObserver;
    }

    @Override
    public void onNext(DepositRequest value) {
        int accountNumber = value.getAccountNumber();
        int amount = value.getAmount();
        this.accountBalance = AccountDatabase.addBalance(accountNumber, amount);
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onCompleted() {
        Balance balance = Balance.newBuilder().setAmount(this.accountBalance).build();
        this.balanceStreamObserver.onNext(balance);
        this.balanceStreamObserver.onCompleted();
    }
}
