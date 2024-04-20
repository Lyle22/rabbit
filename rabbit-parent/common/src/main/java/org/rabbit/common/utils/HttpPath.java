package org.rabbit.common.utils;

import lombok.experimental.UtilityClass;

/**
 * HTTP paths
 */
@UtilityClass
public class HttpPath {
    /**
     * The path separator character
     */
    public final Character PATH_DELIMITER_CHAR = '/';

    /**
     * THe path separator string
     */
    public final String PATH_DELIMITER = "/";

    /**
     * HTTP
     */
    public final String HTTP_START = "http://";

    /**
     * HTTPS
     */
    public final String HTTPS_START = "https://";
}
