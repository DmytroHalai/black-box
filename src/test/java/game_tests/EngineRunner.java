// src/test/java/game_tests/EngineRunner.java
package game_tests;

import org.example.test_runner.GameEngineFactory;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class EngineRunner {

    private static final String SUMMARY_FILE = "tests_summary.txt";

    public static void main(String[] args) {
        Launcher launcher = LauncherFactory.create();

        List<String> implNames = GameEngineFactory.getImplementationNames();
        int total = implNames.size();

        System.out.println("Found " + total + " implementations of GameEngine\n");

        try {
            Files.writeString(Paths.get(SUMMARY_FILE), "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error occurred during creating " + SUMMARY_FILE);
        }

        for (int i = 0; i < total; i++) {
            String name = implNames.get(i);
            System.setProperty("engine.index", String.valueOf(i));

            System.out.printf("[%d/%d] Testing %s... ", i + 1, total, name);

            TestResultListener listener = new TestResultListener();
            launcher.registerTestExecutionListeners(listener);

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectClass(EngineTest.class))
                    .build();

            launcher.execute(request);

            if (listener.isAllPassed()) {
                appendToSummary(name);
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED");
            }
        }

        System.out.println("\nDone. Passed: " + SUMMARY_FILE);
    }

    private static void appendToSummary(String name) {
        try {
            Files.writeString(
                    Paths.get(SUMMARY_FILE),
                    name + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Error occurred during writing: " + name);
        }
    }

    private static class TestResultListener implements TestExecutionListener {
        private boolean allPassed = true;

        @Override
        public void executionFinished(TestIdentifier id, TestExecutionResult result) {
            if (id.isTest() && result.getStatus() == TestExecutionResult.Status.FAILED) {
                allPassed = false;
            }
        }

        public boolean isAllPassed() {
            return allPassed;
        }
    }
}