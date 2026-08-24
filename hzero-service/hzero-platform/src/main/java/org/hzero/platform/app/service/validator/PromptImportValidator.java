package org.hzero.platform.app.service.validator;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.domain.entity.Language;
import org.hzero.platform.domain.entity.Prompt;
import org.hzero.platform.domain.repository.HpfmLanguageRepository;
import org.hzero.platform.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description
 *
 * @author dingzf 2019/09/26 14:48
 */

@ImportValidators({
        @ImportValidator(templateCode = Constants.ImportTemplateCode.PROMPT_TEMP)}
)
public class PromptImportValidator extends ValidatorHandler {

    @Autowired
    private HpfmLanguageRepository languageRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean validate(String data) {

        Prompt prompt;
        List<Language> languages = languageRepository.selectAll();
        // 语言编码
        List<String> languageCodes = languages.stream().map(Language::getCode).collect(Collectors.toList());

        try {
            prompt = objectMapper.readValue(data, Prompt.class);
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }

        if(!languageCodes.contains(prompt.getLang())) {
            return false;
        }
        return true;
    }


}
