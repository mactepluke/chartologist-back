package co.syngleton.chartomancer.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Check {

    private Check() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static <T> boolean notNullNotEmpty(Collection<T> collection)    {
        return collection != null && !collection.isEmpty();
    }

    public static <K, V> boolean notNullNotEmpty(Map<K, V> collection)    {
        return collection != null && !collection.isEmpty();
    }

    public static <T> boolean executeIfTrue(boolean condition, Predicate<T> function, T param)    {
        if (condition)  {
            return function.test(param);
        }
        return false;
    }

    public static <T> void executeIfTrue(boolean condition, Consumer<T> function, T param)    {
        if (condition)  {
            function.accept(param);
        }
    }

    public static boolean passwordIsValid(String password) {
        if (password == null) {
            return false;
        }
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$");
    }

    public static boolean emailIsValid(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    }
}
