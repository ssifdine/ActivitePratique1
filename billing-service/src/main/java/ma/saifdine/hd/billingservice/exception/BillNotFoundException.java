package ma.saifdine.hd.billingservice.exception;

public class BillNotFoundException extends RuntimeException {
    public BillNotFoundException(Long id) {
        super("Bill id " + id + " not found");
    }
}
