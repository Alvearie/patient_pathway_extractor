package com.ibm.research.drl.deepguidelines.pathways.extractor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);
    
    private FileUtils() {}

    /**
     * Given an absolute/relative path, this method returns the corresponding
     * {@link File}.
     * 
     * <p>
     * Such dynamic behavior ensures correctness in terms of the resources to load:
     * <ul>
     * <li>specifying an absolute path could be useful in development phase;</li>
     * <li>specifying a relative path is necessary for deployment.</li>
     * </ul>
     * 
     * @param path
     *            an absolute/relative path
     * @return the file corresponding to the absolute/relative path
     */
    public static File getFile(String path) {
        File file = new File(path);
        if (file.isAbsolute()) {
            return file;
        } else {
            try {
                // Calling Paths.get(URI).getFile seems to be the best way to convert a URL (the resource we are looking for) into a File (e.g. it is able to deal with white spaces in the resource path).
                // Beware of the following limitations:
                // - since we are using getResource(path), the file/folder MUST exist (e.g. even if the annotator cache is initially empty, the folder must be there);
                // - since we are using FileUtils.class.getClassLoader(), it may not work if smart-notes-commons is loaded just once (e.g. skinny WARs);
                // - since every application server uses a different delegation model for classloading, the resource may not be found.
                // The best way would probably require using the ServletContext (i.e. servletContext.getRealPath("WEB-INF/classes/config/application.yaml")).
                return Paths.get(FileUtils.class.getClassLoader().getResource(path).toURI()).toFile();
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Unable to get File object for: " + path, e);
            }
        }
    }
    
    public static Optional<Long> computeNumberOfLines(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(FileUtils.getFile(filePath)))) {
            long count = 0;
            String line = br.readLine();
            while (line != null) {
                count++;
                line = br.readLine();
            }
            return Optional.of(count);
        } catch (Exception e) {
            LOG.error("error while counting lines in file " + filePath, e);
            return Optional.empty();
        }
    }
    
    public static Path writeToTempFile(String content, String prefix, String suffix) throws IOException {
        Path path = Files.createTempFile(prefix, suffix);
        Files.write(path, content.getBytes());
        return path;
    }
}
