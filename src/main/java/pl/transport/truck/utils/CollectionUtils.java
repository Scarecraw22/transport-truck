package pl.transport.truck.utils;

import lombok.experimental.UtilityClass;

import java.util.Collection;

@UtilityClass
public class CollectionUtils {

    public <T> boolean isNotEmpty(Collection<T> collection) {
        return collection != null && collection.size() > 0;
    }
}
