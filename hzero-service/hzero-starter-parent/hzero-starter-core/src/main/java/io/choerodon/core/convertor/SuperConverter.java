package io.choerodon.core.convertor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

/**
 * @author superlee
 */
public interface SuperConverter<T, R> extends Function<T, R> {

    default List<R> convertToList(final List<T> input) {
        Assert.notNull(input, "error.converter.illegal.input");
        return input.stream().map(this::apply).collect(Collectors.toList());
    }

}
