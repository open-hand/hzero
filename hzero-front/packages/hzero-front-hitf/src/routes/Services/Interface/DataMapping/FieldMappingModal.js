import React, { PureComponent } from 'react';
import { Card } from 'choerodon-ui';
import { DataSet, Form, TextField } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { getResponse } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { fieldMappingTableDS, onlyReadFormDS } from '@/stores/DataMapping/DataMappingDS';
import getLang from '@/langs/dataMappingLang';
import LogicOperation from '@/components/LogicOperation';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.fieldMappingTableDS = new DataSet({
      ...fieldMappingTableDS(),
    });
    this.onlyReadFormDS = new DataSet({
      ...onlyReadFormDS(),
    });
  }

  componentDidMount() {
    this.handleUpdateModalProp();
    this.init();
  }

  init() {
    const { fieldMappingData = {} } = this.props;
    this.onlyReadFormDS.loadData([fieldMappingData]);
    this.fieldMappingTableDS.loadData([fieldMappingData]);
  }

  /**
   * 更新当前Modal的属性
   */
  @Bind()
  handleUpdateModalProp() {
    const { modal, path, readOnly } = this.props;
    modal.update({
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            type="c7n-pro"
            color="primary"
            permissionList={[
              {
                code: `${path}.button.mapping.filed.save`,
                type: 'button',
                meaning: '字段映射配置-确定',
              },
            ]}
            disabled={readOnly}
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
    const validate = await this.fieldMappingTableDS.validate();
    if (validate) {
      const result = await this.fieldMappingTableDS.submit();
      if (getResponse(result)) {
        this.props.modal.close();
        this.props.onFetchLine();
      }
    } else {
      notification.error({
        message: getLang('SAVE_VALIDATE'),
      });
    }
  }

  @Bind()
  getFormat(format) {
    const { jsonLogicFormat: conditionJson, stringFormat: evaluateExpression } = format;
    const { current } = this.fieldMappingTableDS;
    current.set('conditionJson', conditionJson);
    current.set('evaluateExpression', evaluateExpression);
  }

  render() {
    const { logicValue, readOnly } = this.props;
    const logicOperationProps = {
      readOnly,
      value: logicValue,
      onGetFormat: this.getFormat,
    };
    return (
      <>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{getLang('BASIC_INFO')}</h3>}
        >
          <Form dataSet={this.onlyReadFormDS} columns={2} disabled>
            <TextField name="castField" />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{getLang('CONDITION_MAINTAIN')}</h3>}
        >
          <LogicOperation {...logicOperationProps} />
        </Card>
      </>
    );
  }
}
