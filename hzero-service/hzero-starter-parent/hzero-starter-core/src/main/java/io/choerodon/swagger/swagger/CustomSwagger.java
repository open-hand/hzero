package io.choerodon.swagger.swagger;

import io.swagger.models.Swagger;

import io.choerodon.swagger.swagger.extra.ExtraData;

import java.util.Objects;

/**
 * @author wuguokai
 */
public class CustomSwagger extends Swagger {

    private ExtraData extraData;

    public ExtraData getExtraData() {
        return extraData;
    }

    public void setExtraData(ExtraData extraData) {
        this.extraData = extraData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CustomSwagger that = (CustomSwagger) o;
        return Objects.equals(extraData, that.extraData);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), extraData);
    }
}
