package edu.berkeley.bailis.thrifttest;

import java.lang.Override;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;

public class Server implements ThriftTestService.Iface {
    @Override
    public void nop(ByteBuffer data) throws TException {
        return;
    }

    public static void main(String[] args) {
        try {
            TServerTransport serverTransport = new TServerSocket(8080);
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(
                    new ThriftTestService.Processor<Server>(new Server())));
            server.serve();
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }

}