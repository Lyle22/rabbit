package org.rabbit.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jetbrains.annotations.NotNull;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.entity.form.CaseTable;
import org.rabbit.service.form.models.CaseTableResponseDTO;
import org.rabbit.service.form.models.CmmnPlanFormDTO;
import org.rabbit.service.jooq.MTFieldInfo;
import org.rabbit.service.jooq.models.CaseTableRequestDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * case table 服务类
 * </p>
 *
 * @author nine
 * @since 2024-07-14
 */
public interface ICaseTableService extends IService<CaseTable>, AbstractCaseTableService {

    String generateTableName(CmmnPlanFormDTO cmmnPlanFormDTO);

    /**
     * Create a new case table
     *
     * @param request the request object
     * @return case table
     */
    CaseTable create(CaseTableRequestDTO request);

    /**
     * Save a case table.
     * <p>
     * If it exists already then update it
     * </p>
     *
     * @param request the request object
     * @return case table
     */
    CaseTable save(CaseTableRequestDTO request);

    /**
     * Retrieve a case table structure
     *
     * @param id the id of the case table
     * @return CaseTableResponseDTO
     */
    CaseTableResponseDTO getTableStructure(@NotNull String id);

    /**
     * Multiple column has been added to the case table.
     *
     * @param request the request object
     * @return case table
     */
    CaseTableResponseDTO addFields(CaseTableRequestDTO request);

    /**
     * A new column has been added to the case table.
     *
     * @param caseTableId the id of the case table
     * @param fieldInfo   the column
     * @return boolean return true if successful otherwise return false
     */
    boolean addSingleField(String caseTableId, MTFieldInfo fieldInfo);

    boolean dropTable(String caseTableId);

    /**
     * Drop a column of the case table
     *
     * @param caseTableId the id of the case table
     * @param columnName  the column name
     * @return return true if successful otherwise return false
     */
    boolean dropColumn(String caseTableId, String columnName);

    CaseTable getById(String id);

    /**
     * Retrieve a case table from business label
     *
     * @param tableLabel the business label
     * @return CaseTable
     */
    CaseTable getByTableName(String tableLabel);

    /**
     * Retrieve a list of case tables that belong to the specified case type
     *
     * @param caseTypeId the id of the case type
     * @return list of case tables
     */
    List<CaseTable> findAllByCaseTypeId(String caseTypeId);

    List<CaseTable> findByIds(Set<String> caseTableIds);

    Boolean deleteByIds(List<String> caseTableIds);

    boolean disableById(String caseTableId);

    boolean insertRecord(CaseTableRequestDTO request);

    boolean insertRecord(String tableName, List<Map<String, Object>> data);

    boolean updateRecord(String tableName, Map<String, Object> dataMap);

    PaginationDTO<Map<String, Object>> recordPage(CaseTableRequestDTO request);

    List<LinkedHashMap<String, Object>> findCaseRecords(CaseTableRequestDTO caseTableRequest);

    /**
     * Retrieve data records through table label
     *
     * @param tableLabel the label of case table
     * @return List<LinkedHashMap < String, Object>>
     */
    List<LinkedHashMap<String, Object>> findTableRecord(String tableLabel, Map<String, Object> where);
}
