package edu.si.ossearch.reports.util;

import edu.si.ossearch.reports.controller.ReportsController;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author jbirkhimer
 */
public class SortOrderParser {

    private static final String IGNORECASE = "ignorecase";

    private final String[] elements;
    private final int lastIndex;
    private final Optional<Sort.Direction> direction;
    private final Optional<Boolean> ignoreCase;

    private SortOrderParser(String[] elements) {
        this(elements, elements.length, Optional.empty(), Optional.empty());
    }

    private SortOrderParser(String[] elements, int lastIndex, Optional<Sort.Direction> direction,
                            Optional<Boolean> ignoreCase) {
        this.elements = elements;
        this.lastIndex = Math.max(0, lastIndex);
        this.direction = direction;
        this.ignoreCase = ignoreCase;
    }

    /**
     * Parse the raw sort string delimited by {@code delimiter}.
     *
     * @param part      sort part to parse.
     * @param delimiter the delimiter to be used to split up the source elements, will never be {@literal null}.
     * @return the parsing state object.
     */
    public static SortOrderParser parse(String part, String delimiter) {

        String[] elements = Arrays.stream(part.split(delimiter)) //
                .filter(SortOrderParser::notOnlyDots) //
                .toArray(String[]::new);

        return new SortOrderParser(elements);
    }

    /**
     * Parse the {@code ignoreCase} portion of the sort specification.
     *
     * @return a new parsing state object.
     */
    public SortOrderParser parseIgnoreCase() {

        Optional<Boolean> ignoreCase = lastIndex > 0 ? fromOptionalString(elements[lastIndex - 1]) : Optional.empty();

        return new SortOrderParser(elements, lastIndex - (ignoreCase.isPresent() ? 1 : 0), direction, ignoreCase);
    }

    /**
     * Parse the {@link Sort.Order} portion of the sort specification.
     *
     * @return a new parsing state object.
     */
    public SortOrderParser parseDirection() {

        Optional<Sort.Direction> direction = lastIndex > 0 ? Sort.Direction.fromOptionalString(elements[lastIndex - 1])
                : Optional.empty();

        return new SortOrderParser(elements, lastIndex - (direction.isPresent() ? 1 : 0), direction, ignoreCase);
    }

    /**
     * Notify a {@link Consumer callback function} for each parsed {@link Sort.Order} object.
     *
     * @param callback block to be executed.
     */
    public void forEachOrder(Consumer<? super Sort.Order> callback) {

        for (int i = 0; i < lastIndex; i++) {
            toOrder(elements[i]).ifPresent(callback);
        }
    }

    private Optional<Boolean> fromOptionalString(String value) {
        return IGNORECASE.equalsIgnoreCase(value) ? Optional.of(true) : Optional.empty();
    }

    private Optional<Sort.Order> toOrder(String property) {

        if (!StringUtils.hasText(property)) {
            return Optional.empty();
        }

        Sort.Order order = direction.map(it -> new Sort.Order(it, property)).orElseGet(() -> Sort.Order.by(property));

        if (ignoreCase.isPresent()) {
            return Optional.of(order.ignoreCase());
        }

        return Optional.of(order);
    }

    static boolean notOnlyDots(String source) {
        return StringUtils.hasText(source.replace(".", ""));
    }
}
