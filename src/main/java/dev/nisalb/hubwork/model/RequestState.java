package dev.nisalb.hubwork.model;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public enum RequestState {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED;

    private final static Map<RequestState, Set<RequestState>> validJobStateTransitions = Map.ofEntries(
            entry(PENDING, Set.of(ACCEPTED, REJECTED, CANCELLED)),
            entry(ACCEPTED, Set.of(CANCELLED)),
            entry(REJECTED, Set.of(ACCEPTED, CANCELLED)),
            entry(CANCELLED, Set.of())
    );

    public static boolean isValidTransition(RequestState from, RequestState to) {
        return validJobStateTransitions.containsKey(from) &&
                validJobStateTransitions.get(from).contains(to);
    }
}
