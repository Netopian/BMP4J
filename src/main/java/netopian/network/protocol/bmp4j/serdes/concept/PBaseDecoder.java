package netopian.network.protocol.bmp4j.serdes.concept;

public interface PBaseDecoder {

    default void partialComplete(String extraInfo) {
        throw new PartialTranslateException(extraInfo);
    }

    class PartialTranslateException extends RuntimeException {

        private static final long serialVersionUID = -844135788375579631L;

        public PartialTranslateException(String message) {
            super(message);
        }

    }

}
