import React, { PureComponent } from 'react';
import { Form, TextField, DataSet, Select } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { isUndefined } from 'lodash';
import { mappingLineFormDS } from '@/stores/Orchestration/DataTransformDS';
import DATA_TRANSFORM_LANG from '@/langs/orchDataTransformLang';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.mappingLineFormDS = new DataSet({
      ...mappingLineFormDS(),
    });
  }

  componentDidMount() {
    this.init();
    this.handleUpdateModalProp();
  }

  init() {
    const { currentRecord, tenantId, castField } = this.props;
    if (isUndefined(currentRecord)) {
      this.mappingLineFormDS.create({ tenantId, targetField: castField });
      return;
    }
    const data = currentRecord.toData();
    if (currentRecord.status === 'add') {
      // 新建再编辑情况
      this.mappingLineFormDS.create(data);
    } else {
      this.mappingLineFormDS.loadData([data]);
    }
  }

  /**
   * 更新当前Modal的属性
   */
  @Bind()
  handleUpdateModalProp() {
    const { modal, path, disabledFlag } = this.props;
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
            disabled={disabledFlag}
            onClick={this.handleSave}
          >
            {DATA_TRANSFORM_LANG.SAVE}
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
    const { modal, currentRecord, mappingLineTableDS } = this.props;
    const validate = await this.mappingLineFormDS.validate();
    if (validate) {
      const result = this.mappingLineFormDS.current.toData();
      if (isUndefined(currentRecord)) {
        mappingLineTableDS.create(result);
      } else {
        const { targetValue, fieldType } = result;
        currentRecord.set('targetValue', targetValue);
        currentRecord.set('fieldType', fieldType);
      }
      modal.close();
    } else {
      notification.error({
        message: DATA_TRANSFORM_LANG.SAVE_VALIDATE,
      });
    }
  }

  render() {
    const { disabledFlag } = this.props;
    return (
      <Form
        labelLayout="horizontal"
        dataSet={this.mappingLineFormDS}
        columns={1}
        disabled={disabledFlag}
      >
        <TextField name="targetValue" />
        <Select name="fieldType" />
        {/* <Select name="conjunction" /> */}
      </Form>
    );
  }
}
