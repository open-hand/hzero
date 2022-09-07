import React, { PureComponent } from 'react';
import { Spin } from 'choerodon-ui';
import { Form, TextField, Select, DataSet } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import { getResponse } from 'utils/utils';
import notification from 'utils/notification';
import { castLineFormDS } from '@/stores/DataMapping/DataMappingDS';
import DATA_MAPPING_LANG from '@/langs/dataMappingLang';

export default class CastLineDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
    };
    this.castLineFormDS = new DataSet({
      ...castLineFormDS(),
    });
  }

  componentDidMount() {
    const { isNew, castHeaderId, tenantId } = this.props;
    if (!isNew) {
      this.handleFetchDetail();
    } else {
      this.castLineFormDS.create({
        castHeaderId,
        tenantId,
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
    modal.update({
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.transform.line.save`,
                type: 'button',
                meaning: '转化行明细-保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={readOnly}
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
    const { onFetchLine } = this.props;
    const validate = await this.castLineFormDS.validate();
    if (validate) {
      const result = await this.castLineFormDS.submit();
      if (getResponse(result)) {
        onFetchLine();
        this.props.modal.close();
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
    const { castLineId, historyFlag, version } = this.props;
    this.setState({ detailLoading: true });
    this.castLineFormDS.setQueryParameter('castLineId', castLineId);
    if (historyFlag) {
      this.castLineFormDS.setQueryParameter('formerVersionFlag', historyFlag);
      this.castLineFormDS.setQueryParameter('version', version);
    }
    await this.castLineFormDS.query();
    this.setState({ detailLoading: false, castType: this.castLineFormDS.current.get('castType') });
  }

  render() {
    const { readOnly } = this.props;
    const { detailLoading, castType } = this.state;
    return (
      <Spin spinning={detailLoading}>
        <Form dataSet={this.castLineFormDS} columns={1} disabled={readOnly}>
          <TextField name="castRoot" restrict="a-zA-Z0-9-_./" />
          <TextField name="castField" restrict="a-zA-Z0-9-_./" />
          <Select name="castType" onChange={(val) => this.setState({ castType: val })} />
          {castType === 'LOV' && <TextField name="castLovCode" />}
          {castType === 'LOV' && <TextField name="castLovField" />}
          {castType === 'LOV' && <Select name="castLovLang" />}
        </Form>
      </Spin>
    );
  }
}
