import React, { PureComponent } from 'react';
import { Spin } from 'choerodon-ui';
import { Form, TextField, DataSet, Select } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { getResponse } from 'utils/utils';
import { mappingLineFormDS } from '@/stores/DataMapping/DataMappingDS';
import DATA_MAPPING_LANG from '@/langs/dataMappingLang';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
    };
    this.mappingLineFormDS = new DataSet({
      ...mappingLineFormDS(),
    });
  }

  componentDidMount() {
    const { isNew, tenantId, castLineData } = this.props;
    const { castLineId, castField } = castLineData;
    if (!isNew) {
      this.handleFetchDetail();
    } else {
      this.mappingLineFormDS.create({
        tenantId,
        castLineId,
        mappingField: castField,
      });
    }
    this.handleUpdateModalProp();
  }

  /**
   * 更新当前Modal的属性
   */
  @Bind()
  handleUpdateModalProp() {
    const { modal, path, readOnly } = this.props;
    const { detailLoading } = this.state;
    modal.update({
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.mapping.detail.save`,
                type: 'button',
                meaning: '转化映射-行明细-保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={detailLoading || readOnly}
            onClick={this.handleSave}
          >
            {DATA_MAPPING_LANG.SAVE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 保存
   */
  @Bind()
  async handleSave() {
    const { modal, onFetchLine } = this.props;
    const validate = await this.mappingLineFormDS.validate();
    if (validate) {
      const result = await this.mappingLineFormDS.submit();
      if (getResponse(result)) {
        onFetchLine();
        modal.close();
      }
    } else {
      notification.error({
        message: DATA_MAPPING_LANG.SAVE_VALIDATE,
      });
    }
  }

  /**
   * 明细查询
   */
  async handleFetchDetail() {
    const { mappingTargetId, historyFlag, version } = this.props;
    this.setState({ detailLoading: true });
    this.mappingLineFormDS.setQueryParameter('mappingTargetId', mappingTargetId);
    if (historyFlag) {
      this.mappingLineFormDS.setQueryParameter('formerVersionFlag', historyFlag);
      this.mappingLineFormDS.setQueryParameter('version', version);
    }
    await this.mappingLineFormDS.query();

    this.setState({ detailLoading: false });
  }

  render() {
    const { readOnly } = this.props;
    const { detailLoading } = this.state;
    return (
      <Spin spinning={detailLoading}>
        <Form
          labelLayout="horizontal"
          dataSet={this.mappingLineFormDS}
          columns={1}
          disabled={readOnly}
        >
          <TextField name="targetValue" />
          <Select name="fieldType" />
          {/* <Select name="conjunction" /> */}
        </Form>
      </Spin>
    );
  }
}
