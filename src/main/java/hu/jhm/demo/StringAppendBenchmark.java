package hu.jhm.demo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Run with: java -jar target/benchmarks.jar StringAppendBenchmark -t 1 -f 2
 */
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10)
@Fork(value = 2, jvmArgsPrepend = {})
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(MILLISECONDS)
@State(Scope.Benchmark)
public class StringAppendBenchmark {

    @State(Scope.Benchmark)
    public static class AppendableState {
        public String appendPlusEquals(int size) {
            String tmp = "";
            for (int i = 0; i < size; i++) {
                tmp += i;
            }
            return tmp;
        }

        public String appendAppend(int size) {
            StringBuilder tmp = new StringBuilder();
            for (int i = 0; i < size; i++) {
                tmp.append(i);
            }
            return tmp.toString();
        }
    }

    @Param({"10", "10000"})
    int size;

    @Benchmark
    public void testAppend(AppendableState appendableState, Blackhole blackhole) {
        blackhole.consume(appendableState.appendAppend(size));
    }

    @Benchmark
    public void testPlusEquals(AppendableState appendableState, Blackhole blackhole) {
        blackhole.consume(appendableState.appendPlusEquals(size));
    }
}
