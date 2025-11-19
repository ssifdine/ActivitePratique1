package ma.saifdine.hd.billingservice.exception;

public class ProductItemNotFoundException extends RuntimeException {
    public ProductItemNotFoundException(String message) {
        super(message);
    }
}
