
package com.amalitech.hospitalmanagementsystem.util;

public class QueryTimer {

    @FunctionalInterface
    public interface QueryBlock {
        void execute() throws Exception;
    }

    public static double measure(QueryBlock block) {
        long t0 = System.nanoTime();
        try {
            block.execute();
        } catch (Exception e) {
            throw new RuntimeException("QueryTimer execution failed", e);
        }
        long t1 = System.nanoTime();
        return (t1 - t0) / 1_000_000.0; // return ms
    }
}
