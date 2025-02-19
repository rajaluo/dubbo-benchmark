package org.apache.dubbo.benchmark;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.benchmark.rpc.AbstractClient;
import org.apache.dubbo.benchmark.service.UserService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)  //raja
@Warmup(iterations = 1, time = 10)      //raja
@Measurement(iterations = 1, time = 30) //raja
public class ClientSimple extends AbstractClient {
    private static final int CONCURRENCY = 32;

    private final ClassPathXmlApplicationContext context;
    private final UserService userService;

    public ClientSimple() {
        context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        userService = (UserService) context.getBean("userService");
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }

    @TearDown
    public void close() throws IOException {
        context.close();
    }

    @Benchmark
    @Override
    public boolean existUser() throws Exception {
        return super.existUser();
    }

    @Benchmark
    @Override
    public boolean createUser() throws Exception {
        return super.createUser();
    }

    @Benchmark
    @Override
    public User getUser() throws Exception {
        return super.getUser();
    }

    @Benchmark
    @Override
    public Page<User> listUser() throws Exception {
        return super.listUser();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        ClientHelper.Arguments arguments = ClientHelper.parseArguments(args);
        String format = arguments.getResultFormat();
        ChainedOptionsBuilder optBuilder = ClientHelper.newBaseChainedOptionsBuilder(arguments)
                .result(System.currentTimeMillis() + "." + format)
                .include(ClientSimple.class.getSimpleName())
                .exclude(ClientPb.class.getSimpleName())
                .exclude(ClientGrpc.class.getSimpleName())
                .exclude(ClientNativeGrpc.class.getSimpleName())
                .mode(Mode.Throughput)
                .mode(Mode.AverageTime)
                .mode(Mode.SampleTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .threads(CONCURRENCY)
                .forks(1);

        Options opt = optBuilder.build();
        new Runner(opt).run();

    }
}
