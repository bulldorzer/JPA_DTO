package com.korea.shop.exception;

// RuntimeException : 실행 중(Runtime)에서 발생하는 예외를 나타냄.
public class NotEnoughStockException extends RuntimeException{

    public NotEnoughStockException() {
        super();
    }
    public NotEnoughStockException(String message) {
        super(message);
    }
    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }
    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}
