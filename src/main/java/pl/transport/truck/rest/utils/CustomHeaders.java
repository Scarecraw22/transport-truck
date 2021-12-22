package pl.transport.truck.rest.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomHeaders {
    REQUEST_ID("X-RequestId");

    private final String value;
}
