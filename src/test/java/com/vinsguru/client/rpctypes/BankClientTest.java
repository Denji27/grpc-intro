package com.vinsguru.client.rpctypes;

import com.vinsguru.models.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;
    @BeforeAll
    public void setup(){
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void balanceTest() throws InterruptedException {
        Thread.sleep(5000);
        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(2)
                .build();
        Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
        System.out.println(
                "Received: " + balance.getAmount()
        );
    }

    @Test
    public void withdrawTest(){
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(9).setAmount(50).build();
        this.blockingStub.withdraw(withdrawRequest)
                .forEachRemaining(money -> System.out.println("Received: " + money.getValue()));
    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(5).setAmount(50).build();
        this.bankServiceStub.withdraw(withdrawRequest, new MoneyStreamingResponse(latch));
        latch.await();
    }

    @Test
    public void cashStreamingRequest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<DepositRequest> streamObserver = this.bankServiceStub.cashDeposit(new BalanceStreamObserver(latch));
        for (int i = 0; i < 10; i++) {
            DepositRequest depositRequest = DepositRequest.newBuilder().setAccountNumber(8).setAmount(10).build();
            streamObserver.onNext(depositRequest);
        }
        streamObserver.onCompleted();
        latch.await();
    }
}
