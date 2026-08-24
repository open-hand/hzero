package org.hzero.platform.app.assembler;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.FileUtils;
import org.hzero.platform.api.dto.CompanyDTO;
import org.hzero.platform.api.dto.CountryRegionDTO;
import org.hzero.platform.app.service.CountryRegionService;
import org.hzero.platform.domain.entity.Company;
import org.springframework.beans.BeanUtils;

/**
 * 公司DTO组装器
 *
 * @author gaokuo.dai@hand-china.com 2018年6月22日上午11:55:30
 */
public class CompanyAssembler {

    private CompanyAssembler() {
    }

    /**
     * 组装完整的公司信息
     *
     * @param company
     * @param countryRegionService
     * @return
     */
    public static CompanyDTO assembleCompanyInfo(Company company, CountryRegionService countryRegionService) {
        if (company == null) {
            return null;
        }
        CompanyDTO dto = CompanyAssembler.entityToDto(company);
        if (company.getRegisteredCountryId() != null && company.getRegisteredRegionId() != null) {
            CountryRegionDTO countryRegionDTO = countryRegionService.queryCountryRegion(company.getRegisteredCountryId(), company.getRegisteredRegionId());
            if (countryRegionDTO != null) {
                dto.setRegisteredCountryName(countryRegionDTO.getCountryName());
                dto.setRegisteredRegionName(countryRegionDTO.getRegionPathName());
            }
        }
        if (StringUtils.isNotEmpty(company.getLicenceUrl())) {
            dto.setLicenceFileName(FileUtils.getFileName(company.getLicenceUrl()));
        }
        return dto;
    }

    /**
     * 实体转换为DTO
     *
     * @param entity
     * @return
     */
    private static CompanyDTO entityToDto(Company entity) {
        if (entity == null) {
            return null;
        }
        CompanyDTO dto = new CompanyDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }


}
