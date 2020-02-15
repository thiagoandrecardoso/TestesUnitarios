package br.ce.wcaquino.runners;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelRunner extends BlockJUnit4ClassRunner {
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    public ParallelRunner(Class<?> klass) throws InitializationError {
        super(klass);
        setScheduler(new ThreadPol());
    }

    private static class ThreadPol implements RunnerScheduler {
        private ExecutorService executor;

        public ThreadPol() {
            executor = Executors.newFixedThreadPool(3);
        }

        @Override
        public void schedule(Runnable run) {
            executor.submit(run);
        }

        @Override
        public void finished() {
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
