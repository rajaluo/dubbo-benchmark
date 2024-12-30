package org.apache.dubbo.benchmark;

import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.benchmark.service.DubboRestUserServiceFeignClient;
import org.apache.dubbo.benchmark.service.SpringCloudUserServiceFeignClient;
import org.apache.dubbo.benchmark.service.UserService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
@Threads(32)
@Fork(1)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 10)
@Measurement(iterations = 1, time = 30)
public class BenchmarkTest {
    private static final int CONCURRENCY = 32;

    private final ClassPathXmlApplicationContext context;
    private final SpringCloudUserServiceFeignClient springCloudUserService;
    private final DubboRestUserServiceFeignClient dubboRestUserService;
    private final UserService restUserService;
    private final UserService triUserService;
    private final UserService dubboUserService;
    private final Random random;
    private final AtomicInteger counter;

    public BenchmarkTest() {
        context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        springCloudUserService = context.getBean(SpringCloudUserServiceFeignClient.class);
        dubboRestUserService = context.getBean(DubboRestUserServiceFeignClient.class);
        restUserService = (UserService) context.getBean("restUserService");
        triUserService = (UserService) context.getBean("triUserService");
        dubboUserService = (UserService) context.getBean("dubboUserService");
        random = new Random();
        counter = new AtomicInteger(0);
    }

    @TearDown(Level.Trial)
    public void close() throws IOException {
        if (context != null) {
            context.close();
        }
    }

    private User generateUser() {
        User user = new User();
        user.setId(counter.getAndIncrement());
        user.setName("user-" + UUID.randomUUID().toString().substring(0, 8));
        user.setEmail(UUID.randomUUID().toString().substring(0, 8) + "@example.com");
        return user;
    }

    private String generateEmail() {
        return UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    private Long generateId() {
        return (long) counter.getAndIncrement();
    }

    private int generatePage() {
        return random.nextInt(100) + 1;
    }

    // Task 1: Spring Cloud REST tests
    @Benchmark
    public void springcloud_rest_existUser() {
        springCloudUserService.existUser(generateEmail());
    }

    @Benchmark
    public void springcloud_rest_createUser() {
        springCloudUserService.createUser(generateUser());
    }

    @Benchmark
    public void springcloud_rest_getUser() {
        springCloudUserService.getUser(generateId());
    }

    @Benchmark
    public void springcloud_rest_listUser() {
        springCloudUserService.listUser(generatePage());
    }

    // Task 2: Feign to Dubbo REST tests
    @Benchmark
    public void feign_dubborest_existUser() {
        dubboRestUserService.existUser(generateEmail());
    }

    @Benchmark
    public void feign_dubborest_createUser() {
        dubboRestUserService.createUser(generateUser());
    }

    @Benchmark
    public void feign_dubborest_getUser() {
        dubboRestUserService.getUser(generateId());
    }

    @Benchmark
    public void feign_dubborest_listUser() {
        dubboRestUserService.listUser(generatePage());
    }

    // Task 3: Dubbo REST tests
    @Benchmark
    public void dubbo_rest_existUser() {
        restUserService.existUser(generateEmail());
    }

    @Benchmark
    public void dubbo_rest_createUser() {
        restUserService.createUser(generateUser());
    }

    @Benchmark
    public void dubbo_rest_getUser() {
        restUserService.getUser(generateId());
    }

    @Benchmark
    public void dubbo_rest_listUser() {
        restUserService.listUser(generatePage());
    }

    // Task 4: Dubbo Triple tests
    @Benchmark
    public void dubbo_triple_existUser() {
        triUserService.existUser(generateEmail());
    }

    @Benchmark
    public void dubbo_triple_createUser() {
        triUserService.createUser(generateUser());
    }

    @Benchmark
    public void dubbo_triple_getUser() {
        triUserService.getUser(generateId());
    }

    @Benchmark
    public void dubbo_triple_listUser() {
        triUserService.listUser(generatePage());
    }

    // Task 5: Dubbo RPC tests
    @Benchmark
    public void dubbo_rpc_existUser() {
        dubboUserService.existUser(generateEmail());
    }

    @Benchmark
    public void dubbo_rpc_createUser() {
        dubboUserService.createUser(generateUser());
    }

    @Benchmark
    public void dubbo_rpc_getUser() {
        dubboUserService.getUser(generateId());
    }

    @Benchmark
    public void dubbo_rpc_listUser() {
        dubboUserService.listUser(generatePage());
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("请指定要运行的压测任务：");
            System.out.println("1. springcloud - 测试 Spring Cloud REST 服务");
            System.out.println("2. feigndubbo - 测试 Feign 调用 Dubbo REST 服务");
            System.out.println("3. dubborest - 测试 Dubbo REST 协议");
            System.out.println("4. dubbotriple - 测试 Dubbo Triple 协议");
            System.out.println("5. dubborpc - 测试 Dubbo RPC 协议");
            System.out.println("\n运行示例：");
            System.out.println("java -Xms3g -Xmx3g -XX:+UseG1GC -jar benchmark.jar springcloud");
            return;
        }

        String pattern;
        switch (args[0].toLowerCase()) {
            case "springcloud":
                pattern = "springcloud_rest";
                break;
            case "feigndubbo":
                pattern = "feign_dubborest";
                break;
            case "dubborest":
                pattern = "dubbo_rest";
                break;
            case "dubbotriple":
                pattern = "dubbo_triple";
                break;
            case "dubborpc":
                pattern = "dubbo_rpc";
                break;
            default:
                System.out.println("无效的任务名称。请使用以下之一：springcloud, feigndubbo, dubborest, dubbotriple, dubborpc");
                return;
        }

        Options opt = new OptionsBuilder()
                .include(BenchmarkTest.class.getSimpleName() + "." + pattern + ".*")
                .threads(CONCURRENCY)
                .forks(1)
                .resultFormat(ResultFormatType.JSON)
                .result("benchmark-" + pattern + ".json")
                .build();
        new Runner(opt).run();
    }
} 