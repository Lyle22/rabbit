package org.rabbit.service.system;

import org.rabbit.entity.system.SystemConfig;

import java.util.List;

/**
 * The interface of system configuration.
 *
 * @author nine rabbit
 */
public interface ISystemConfigService {

    /**
     * Find by id list.
     *
     * @param id the id
     * @return the list
     */
    List<SystemConfig> findById(String id);

    /**
     * Find by system id list.
     *
     * @param id the id
     * @return the list
     */
    List<SystemConfig> findBySystemId(String id);

    /**
     * Find by system type list.
     *
     * @param systemIdType the system type
     * @return the list
     */
    List<SystemConfig> findBySystemType(String systemIdType);

    /**
     * Find by system type and system id list.
     *
     * @param systemIdType the system type
     * @param systemId     the system id
     * @return the list
     */
    List<SystemConfig> findBySystemTypeAndSystemId(String systemIdType, String systemId);

    /**
     * Save system configuration.
     *
     * @param systemConfig the system configuration
     * @return the system configuration
     **/
    boolean save(SystemConfig systemConfig);

}
