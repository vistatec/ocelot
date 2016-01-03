package com.vistatec.ocelot.config;

import com.vistatec.ocelot.config.xml.RootConfig;

/**
 * Data transfer service for reading and persisting Ocelot's configuration.
 */
public interface ConfigTransferService {
    public RootConfig parse() throws TransferException;
    public void save(RootConfig cfg) throws TransferException;

    public class TransferException extends Exception {
        private static final long serialVersionUID = 1L;

        public TransferException(Throwable cause) {
            super(cause);
        }

    }
}
