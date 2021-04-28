package hu.jhm.demo;

import jhm.LockHarnessTest;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Run with: java -jar target/benchmarks.jar LockBenchmark -t 4 -f 1
 * 1 fork and 4 threads
 */
//  Warmup period to calibrate the measurements and prevent startup anomalies.
@Warmup(iterations = 5)
//  Same as warmup but for the real-time mesaurements.
@Measurement(iterations = 5)
//  JVM optimizes an application by creating a profile of the application's behavior.
//  The fork is created to reset this profile.
@Fork(value = 1, jvmArgsPrepend = {})
//  Set mode of it: avgt, thrpt, sample, ...
@BenchmarkMode(Mode.AverageTime)
//  The output time unit to use.
@OutputTimeUnit(TimeUnit.MILLISECONDS)
//  A state object can be reused across multiple calls to your benchmark method.
//  All threads running the benchmark share the same state object: right now it is sut.
@State(Scope.Benchmark)
public class LockBenchmark {

    private LockHarnessTest sut = null;

    @Param({"100", "1000"})
    int size;

    @Setup(Level.Trial)
    public void doSetup() {
        sut = new LockHarnessTest(1000);
        System.out.println("Setup finished.");
    }

    @TearDown(Level.Trial)
    public void doTearDown() {
        System.out.println("TearDown finished.");
    }

    //  Like junits @Test methods, JHM generates bytecode around it.
    @Benchmark
    public void testWithLock(
            // To prevent dead code elimination.
            Blackhole blackhole
    ) {
        for (int c = 0; c < size; c++) {
            blackhole.consume(sut.computeWithLock(c));
        }
    }

    @Benchmark
    public void testWithIntrinsics(
            Blackhole blackhole
    ) {
        for (int c = 0; c < size; c++) {
            blackhole.consume(sut.computeWithIntrinsics(c));
        }
    }

}

/**
 * Understanding the results:
 * <p>
 * hu.jhm.demo.MyBenchmark.testWithIntrinsics:
 * With parameters n = 1000 testWithIntrinsics method took estimated = 0.180 ms to run on average 5 iterations.
 * Assuming the results are normally distributed one would expect that the "true" execution time
 * for that method has a 99.9% probability to be somewhere between 0.180 - 0.006 (0.174) milliseconds
 * and  0.180 + 0.006 (0.187)
 * Standard deviation (stdev) = a szórás azt mutatja meg, hogy az értékeink átlagosan mennyivel
 * térnek el az adatsorunk számtani átlagától.
 * <p>
 * # JMH version: 1.29
 * # VM version: JDK 11.0.1, OpenJDK 64-Bit Server VM, 11.0.1+13
 * # VM invoker: C:\Program Files\Java\jdk-11.0.1\bin\java.exe
 * # VM options: <none>
 * # Blackhole mode: full + dont-inline hint
 * # Warmup: 5 iterations, 10 s each
 * # Measurement: 5 iterations, 10 s each
 * # Timeout: 10 min per iteration
 * # Threads: 3 threads, will synchronize iterations
 * # Benchmark mode: Average time, time/op
 * # Benchmark: hu.jhm.demo.MyBenchmark.testWithIntrinsics
 * # Parameters: (size = 1000)
 * <p>
 * # Run progress: 25.00% complete, ETA 00:05:04
 * # Fork: 1 of 1
 * # Warmup Iteration   1: Setup finished.
 * 0.225 ▒(99.9%) 1.741 ms/op
 * # Warmup Iteration   2: 0.290 ▒(99.9%) 2.707 ms/op
 * # Warmup Iteration   3: 0.194 ▒(99.9%) 1.284 ms/op
 * # Warmup Iteration   4: 0.188 ▒(99.9%) 0.971 ms/op
 * # Warmup Iteration   5: 0.183 ▒(99.9%) 0.719 ms/op
 * Iteration   1: 0.177 ▒(99.9%) 0.178 ms/op
 * Iteration   2: 0.181 ▒(99.9%) 0.343 ms/op
 * Iteration   3: 0.180 ▒(99.9%) 0.342 ms/op
 * Iteration   4: 0.180 ▒(99.9%) 0.288 ms/op
 * Iteration   5: TearDown finished.
 * 0.181 ▒(99.9%) 0.294 ms/op
 * <p>
 * Result "hu.jhm.demo.MyBenchmark.testWithIntrinsics":
 * 0.180 ▒(99.9%) 0.006 ms/op [Average]
 * (min, avg, max) = (0.177, 0.180, 0.181), stdev = 0.002
 * CI (99.9%): [0.174, 0.187] (assumes normal distribution)
 * <p>
 * Benchmark                       (size)  Mode  Cnt  Score   Error  Units
 * MyBenchmark.testWithIntrinsics    1000  avgt    5  0.180 ▒ 0.006  ms/op
 */
