import React, { PureComponent } from 'react';
import { Spin } from 'choerodon-ui';
import { Form, TextField, DataSet, Select } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { getResponse } from 'utils/utils';
import { mappingLineFormDS } from '@/stores/DataMapping/DataMappingDS';
import getLang from '@/langs/dataMappingLang';

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
                meaning: '转化映射-行明细-确定',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={detailLoading || readOnly}
            onClick={this.handleSave}
          >
            {getLang('SURE')}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 确定
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
        message: getLang('SAVE_VALIDATE'),
      });
    }
  }

  /**
   * 明细查询
   */
  async handleFetchDetail() {
    const { mappingTargetId } = this.props;
    this.setState({ detailLoading: true });
    this.mappingLineFormDS.setQueryParameter('mappingTargetId', mappingTargetId);
    await this.mappingLineFormDS.query();

    this.setState({ detailLoading: false });
  }

  render() {
    const { detailLoading } = this.state;
    const { readOnly } = this.props;
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
