package com.vistatec.ocelot.tm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;

/**
 * Manages importing TMs in Ocelot.
 */
public interface TmManager {

    /**
     * Fetch search ordered list of all configured TMs.
     * @return TM configurations
     */
    public List<TmConfig> fetchTms();

    /**
     * Fetch a TM configuration by the TM name.
     * @param tmName - Name of the TM
     * @return
     */
    public TmConfig fetchTm(String tmName);

    /**
     * Save the search ordering preference for TMs.
     * @param orderedTms - Ordered list where first element is search first
     * @throws com.vistatec.ocelot.config.ConfigTransferService.TransferException
     */
    public void saveTmOrdering(List<TmConfig> orderedTms) throws TransferException;

    /**
     * Perform all functionality required to setup a new TM for usage by the
     * TmManager and by a {@link TmService}
     * @param tmName - Label for the new TM
     * @param tmDataDir - Directory containing initial files to load in TM
     * @throws IOException
     * @throws com.vistatec.ocelot.config.ConfigTransferService.TransferException
     */
    public void initializeNewTm(String tmName, File tmDataDir) throws IOException, TransferException;
    
    /**
     * Perform all functionality required to setup a new TM for usage by the
     * TmManager and by a {@link TmService}
     * @param tmName - Label for the new TM
     * @param tmFiles - list of initial files to load in TM
     * @throws IOException
     * @throws com.vistatec.ocelot.config.ConfigTransferService.TransferException
     */
    public void initializeNewTm(String tmName, File[] tmFiles) throws IOException, TransferException;

    public void deleteTm(String tmName) throws IOException, TransferException;

    public void saveOpenFileAsTmx(File tmx) throws IOException;

    /**
     * Change where a TM fetches the data for their TM and regenerate the index.
     * @param tmName
     * @param tmDataDir
     * @throws IOException
     * @throws com.vistatec.ocelot.config.ConfigTransferService.TransferException
     */
    public void changeTmDataDir(String tmName, File tmDataDir) throws IOException, TransferException;

    /**
     * Parse the TMX file and associate the segments with the given {@code tmName}.
     * Creates a new TM if specified tmName does not already exist.
     * @param tmName - Name of the TM
     * @param tmx - TMX file to import
     * @throws IOException
     */
    public void importTmx(String tmName, File tmx) throws IOException;

    /**
     * Re-index the TM specified, using the current associated TM data directory.
     * @param tmName - Name of the TM
     * @throws java.io.IOException
     */
    public void regenerateTm(String tmName) throws IOException;
}
