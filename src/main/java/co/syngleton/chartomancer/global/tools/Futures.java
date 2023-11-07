package co.syngleton.chartomancer.global.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public final class Futures {

    private Futures() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * Lists the results of completable futures after waiting for their completion
     * @param futureList the list of completable futures to pass using supplyAsync()
     * @return a list of the result of completed calculations
     * @param <T> the type of return of the individual completable futures result
     * @throws ExecutionException the exception thrown if an error happens during a thread execution
     * @throws InterruptedException the exception thrown if an error happens if a thread is interrupted
     */
    public static <T> List<T> listCompleted(List<CompletableFuture<T>> futureList) throws ExecutionException, InterruptedException {

        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(futureList.toArray(new CompletableFuture[futureList.size()]));

        CompletableFuture<List<T>> futureComputedList = allFutures.thenApply(pattern ->
                futureList.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        return new ArrayList<>(futureComputedList.get());
    }

    /**
     * Waits for a list of completable futures to complete
     * @param futureList the list of completable futures to pass using runAsync()
     * @throws ExecutionException the exception thrown if an error happens during a thread execution
     * @throws InterruptedException the exception thrown if an error happens if a thread is interrupted
     */
    public static void waitCompleted(List<CompletableFuture<Void>> futureList) throws ExecutionException, InterruptedException {

        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
        allFutures.get();
    }
}
