import React, { PureComponent } from 'react';
import { Spin } from 'choerodon-ui';
import { Form, TextField, Select, DataSet, Lov } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import { getResponse } from 'utils/utils';
import { isUndefined } from 'lodash';
import notification from 'utils/notification';
import { castLineFormDS } from '@/stores/DataMapping/DataMappingDS';
import getLang from '@/langs/dataMappingLang';
import { saveHeader } from '@/services/dataMappingService';

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
                meaning: '转化行明细-确定',
              },
            ]}
            type="c7n-pro"
            color="primary"
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
    const {
      castHeaderId,
      headerData,
      castHeaderIdName,
      onFetchLine,
      onWriteBack,
      onFetchDetail,
    } = this.props;
    const validate = await this.castLineFormDS.validate();
    if (validate) {
      let id = null;
      if (isUndefined(castHeaderId)) {
        await saveHeader(headerData).then((res) => {
          if (res) {
            const { castHeaderId: tempId, castName: name } = res;
            id = tempId;
            this.castLineFormDS.current.set('castHeaderId', id);
            onWriteBack(castHeaderIdName, id, name);
          }
        });
      }
      const result = await this.castLineFormDS.submit();
      if (getResponse(result)) {
        if (isUndefined(castHeaderId)) {
          onFetchDetail(id);
        } else {
          onFetchLine();
        }
        this.props.modal.close();
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
    const { castLineId } = this.props;
    this.setState({ detailLoading: true });
    this.castLineFormDS.setQueryParameter('castLineId', castLineId);
    await this.castLineFormDS.query();
    this.setState({ detailLoading: false, castType: this.castLineFormDS.current.get('castType') });
  }

  render() {
    const { detailLoading, castType } = this.state;
    const { readOnly } = this.props;
    return (
      <Spin spinning={detailLoading}>
        <Form dataSet={this.castLineFormDS} columns={1} disabled={readOnly}>
          <TextField name="castRoot" restrict="a-zA-Z0-9-_./" />
          <TextField name="castField" restrict="a-zA-Z0-9-_./" />
          <Select name="castType" onChange={(val) => this.setState({ castType: val })} />
          {castType === 'LOV' && <TextField name="castLovCode" />}
          {castType === 'LOV' && <TextField name="castLovField" />}
          {castType === 'LOV' && <Lov name="langLov" />}
        </Form>
      </Spin>
    );
  }
}
