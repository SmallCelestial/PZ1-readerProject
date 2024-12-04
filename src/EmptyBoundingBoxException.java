public class EmptyBoundingBoxException extends RuntimeException {
    public EmptyBoundingBoxException() {
        super("Bounding box is empty");
    }
}
