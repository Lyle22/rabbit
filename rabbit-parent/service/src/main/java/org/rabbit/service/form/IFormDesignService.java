package org.rabbit.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.entity.form.FormDesign;
import org.rabbit.service.form.models.FormDesignDataDTO;
import org.rabbit.service.form.models.FormDesignRequestDTO;
import org.rabbit.service.form.models.FormDesignResponseDTO;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author nine rabbit
 **/
public interface IFormDesignService extends IService<FormDesign> {

    public FormDesign findById(String id);

    public FormDesignResponseDTO getReleaseById(String id);

    public List<FormDesignResponseDTO> findAll(String name);

    /**
     * Submit Form Data
     * <p/>Stored into database table
     * <p>Note that starting a process instance if the form design is bound to a process definition</p>
     *
     * @param formDataDTO the form data
     * @return whether successfully updated
     */
    public Boolean submitFormData(FormDesignDataDTO formDataDTO);

    public PaginationDTO<List<LinkedHashMap<String, Object>>> recordPage(FormDesignRequestDTO request);
}
