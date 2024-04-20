package org.rabbit.mail.repository;

import org.rabbit.entity.system.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface System setting repository.
 * @author nine
 */
@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    /**
     * Find by id list.
     *
     * @param id the id
     * @return the list
     */
    List<SystemSetting> findById(String id);

    /**
     * Find by system id list.
     *
     * @param id the id
     * @return the list
     */
    List<SystemSetting> findBySystemId(String id);

    /**
     * Find by system id type list.
     *
     * @param systemIdType the system id type
     * @return the list
     */
    List<SystemSetting> findBySystemIdType(String systemIdType);

    /**
     * Find by system id type and system id list.
     *
     * @param systemIdType the system id type
     * @param Id           the id
     * @return the list
     */
    List<SystemSetting> findBySystemIdTypeAndSystemId(String systemIdType, String Id);
}
