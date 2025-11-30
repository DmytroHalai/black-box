package org.example.logic.api;

import org.example.runner.GameEngineFactory;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class EngineRunner {

    private static final Path SUMMARY_FILE_PATH = Path.of("tests_summary.txt");

    public static void main(String[] args) {
        Launcher launcher = LauncherFactory.create();

        List<String> implNames = GameEngineFactory.getImplementationNames();
        int total = implNames.size();

        System.out.println("Found " + total + " implementations of GameEngine\n");

        try {
            Files.writeString(SUMMARY_FILE_PATH, "", StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error occurred during creating " + SUMMARY_FILE_PATH);
        }

        for (int i = 0; i < total; i++) {
            String name = implNames.get(i);
            System.setProperty("engine.index", String.valueOf(i));

            System.out.printf("[%d/%d] Testing %s... ", i + 1, total, name);

            TestResultListener listener = new TestResultListener();
            launcher.registerTestExecutionListeners(listener);

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectClass(GameEngineTest.class))
                    .build();

            launcher.execute(request);

            if (listener.areAllTestsPassed()) {
                appendToSummary(name);
                System.out.println("<- PASSED");
            } else System.out.println();
        }

        System.out.println("\nDone. Passed: " + SUMMARY_FILE_PATH);
    }

    private static void appendToSummary(String name) {
        try {
            Files.writeString(SUMMARY_FILE_PATH, name + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error occurred during writing: " + name);
        }
    }

    private static class TestResultListener implements TestExecutionListener {
        private boolean allTestsPassed = true;

        @Override
        public void executionFinished(TestIdentifier id, TestExecutionResult result) {
            if (id.isTest() && result.getStatus() == TestExecutionResult.Status.FAILED) {
                allTestsPassed = false;
            }
        }

        public boolean areAllTestsPassed() {
            return allTestsPassed;
        }
    }
}