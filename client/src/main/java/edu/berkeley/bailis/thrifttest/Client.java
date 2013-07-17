package edu.berkeley.bailis.thrifttest;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Client {
    private static Random random = new Random();

    private static ByteBuffer getRandomPayload(int length) {
        ByteBuffer ret = ByteBuffer.allocate(length);
        random.nextBytes(ret.array());
        return ret;
    }

    public static void main(String[] args) {

        final AtomicLong totalLatency = new AtomicLong();
        final AtomicInteger numberOps = new AtomicInteger();
        long startTimeMs = System.currentTimeMillis();

        try {
            if(args.length != 4) {
                System.err.println("Usage: host numclients runtime(s) payloadsize");
                System.exit(-1);
            }

            final String serverHost = args[0];
            final int numClients = Integer.parseInt(args[1]);
            final int runTime = Integer.parseInt(args[2]);
            final int payloadSize = Integer.parseInt(args[3]);


            List<Thread> workers = new ArrayList<Thread>();

            for(int i = 0; i < numClients; ++i) {
                Thread worker = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TSocket socket = new TSocket(serverHost, 8080);
                            socket.open();
                            ThriftTestService.Client client = new ThriftTestService.Client(new TBinaryProtocol(socket));

                            while(true) {
                                long startTime = System.currentTimeMillis();
                                client.nop(payloadSize == 0 ? ByteBuffer.allocate(0) : getRandomPayload(payloadSize));
                                totalLatency.addAndGet(System.currentTimeMillis()-startTime);
                                numberOps.incrementAndGet();
                            }
                        } catch (Exception e) {
                            System.err.println(e);
                            e.printStackTrace();
                        }
                    }});
                worker.start();
                workers.add(worker);
            }

            Thread.sleep(1000*runTime);

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if(numberOps.get() > 0) {
                System.out.println(numberOps.get()+" total operations");
                System.out.println("Throughput: "+(float)numberOps.get()/(System.currentTimeMillis()-startTimeMs)*1000+" ops/s");
                System.out.println("Average latency: "+(float)numberOps.get()/totalLatency.get()+" ms");
            } else {
                System.err.println("No operations recorded!");
            }

            System.exit(0);
        }
    }
}