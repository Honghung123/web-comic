package com.group17.comic.dtos.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.group17.comic.models.Pagination;

import lombok.Getter;

/*
Two advantages of using generics type instead of using Object:
	- no need of casting (the compiler hides this from you)
	- compile time safety that works. If the Object version is used, you won't be sure
		that the method always returns expected class. So, you may have a ClassCastException at runtime.
*/

public class SuccessfulResponse<T> extends ResponseEntity<SuccessfulResponse.Payload<T>> {

    public SuccessfulResponse(HttpStatus statusCode, String message) {
        super(new Payload<>(statusCode.value(), message), statusCode);
    }

    public SuccessfulResponse(HttpStatus statusCode, String message, T data) {
        super(new Payload<>(statusCode.value(), message, data), statusCode);
    }

    public SuccessfulResponse(HttpStatus statusCode, String message, Pagination<?> pagination, T data) {
        super(new Payload<>(statusCode.value(), message, pagination, data), statusCode);
    }

    public SuccessfulResponse(HttpStatus statusCode, String message, Pagination<?> pagination, T data, Object others) {
        super(new Payload<>(statusCode.value(), message, pagination, data, others), statusCode);
    }

    @Getter
    public static class Payload<T> {
        private int statusCode;
        private String message;
        private Pagination<?> pagination;
        private T data;
        private Object others;

        public Payload(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        public Payload(int statusCode, String message, T data) {
            this.statusCode = statusCode;
            this.message = message;
            this.data = data;
        }

        public Payload(int statusCode, String message, Pagination<?> pagination, T data) {
            this(statusCode, message, data);
            this.pagination = pagination;
        }

        public Payload(int statusCode, String message, Pagination<?> pagination, T data, Object others) {
            this(statusCode, message, pagination, data);
            this.others = others;
        }
    }
}
