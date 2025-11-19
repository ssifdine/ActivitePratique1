package ma.saifdine.hd.billingservice.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer id " + id + " not found");
    }
}
