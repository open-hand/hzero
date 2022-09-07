import React, { Component } from 'react';
import { DataSet, Form, TextField, Select, Lov, Button, Spin } from 'choerodon-ui/pro';
import { Divider } from 'choerodon-ui';
import { isEmpty, isUndefined, omit } from 'lodash';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { getResponse } from 'utils/utils';
import { basicFormDS } from '@/stores/Interfaces/interfaceDS';
import DynamicForm from '@/components/DynamicForm';
import getLang from '@/langs/interfacesLang';
import { testAuth } from '@/services/interfacesService';

export default class AuthModal extends Component {
  constructor(props) {
    super(props);
    this.basicFormDS = new DataSet(
      basicFormDS({
        onFieldUpdate: this.handleFieldUpdate,
      })
    );
    this.state = {
      authLevel: null,
      authType: 'NONE',
      formData: {},
      noContent: true,
    };
  }

  async componentDidMount() {
    const { interfaceId, interfaceAuthId } = this.props;
    if (!isUndefined(interfaceId) && !isUndefined(interfaceAuthId)) {
      this.basicFormDS.setQueryParameter('interfaceId', interfaceId);
      this.basicFormDS.setQueryParameter('interfaceAuthId', interfaceAuthId);
      await this.basicFormDS.query();
      const data = this.basicFormDS.current.toData();
      /**
       * 两个authType，一个和httpAuthorization同级
       * 另一个在httpAuthorization里面
       * 不一样的值，保存的时候都需要
       */
      const {
        authLevel,
        roleId,
        authType: headerAuthType,
        authLevelValue,
        authLevelValueMeaning,
        httpAuthorization = {},
      } = data;
      const { authType, authJson = '{}' } = httpAuthorization;
      const httpAuthData = JSON.parse(authJson);
      this.basicFormDS.current.set('authType', authType);
      this.basicFormDS.current.set('headerAuthType', headerAuthType);
      if (authLevel === 'TENANT') {
        this.basicFormDS.current.set('authLevelValueTenant', authLevelValue);
        this.basicFormDS.current.set('authLevelValueMeaningTenant', authLevelValueMeaning);
      } else if (authLevel === 'ROLE') {
        this.basicFormDS.current.set('authLevelValueRole', roleId);
        this.basicFormDS.current.set('authLevelValueMeaningRole', authLevelValueMeaning);
      } else if (authLevel === 'CLIENT') {
        this.basicFormDS.current.set('authLevelValueClient', authLevelValue);
        this.basicFormDS.current.set('authLevelValueMeaningClient', authLevelValueMeaning);
      }
      this.updateModalFooter(authType || '');
      this.setState({ authLevel, formData: httpAuthData });
    }
    this.props.modal.update({
      onOk: this.handleSave,
    });
  }

  /**
   * 更新按钮
   */
  @Bind()
  updateModalFooter(type) {
    const tempType = isEmpty(type) ? '' : type;
    this.props.modal.update({
      footer: (okBtn, cancelBtn) => (
        <>
          {cancelBtn}
          {tempType.startsWith('OAUTH2', 0) && (
            <Button onClick={this.handleTest}>{getLang('TEST')}</Button>
          )}
          {okBtn}
        </>
      ),
    });
  }

  @Bind()
  handleFieldUpdate({ name, value, record }) {
    if (name === 'authLevel') {
      record.set('authLevelValue', null);
      record.set('authLevelValueMeaning', null);
      record.set('roleId', null);
      record.set('authLevelValueTenantLov', undefined);
      record.set('authLevelValueRoleLov', undefined);
      record.set('authLevelValueClientLov', undefined);
      this.setState({ authLevel: value });
    }
    if (name === 'authType') {
      this.updateModalFooter(value);
      this.setState({ authType: isEmpty(value) ? 'NONE' : value });
    }
  }

  @Bind()
  async handleSave() {
    const { onOk = () => {} } = this.props;
    const values = await this.validateValue();
    if (!values) {
      return false;
    }
    return new Promise((resolve, reject) => {
      onOk(values, () => this.props.modal.close()).then((result) => {
        if (result && !result.failed) {
          resolve();
        } else {
          reject();
        }
      });
    });
  }

  @Bind()
  async validateValue() {
    const results = await Promise.all([this.basicFormDS.validate(), this.saveForm()]);
    if (results.includes(false)) {
      notification.error({
        message: getLang('SAVE_VALIDATE'),
      });
      return undefined;
    }
    const basicData = omit(this.basicFormDS.current.toData(), [
      '__dirty',
      'authLevelValueTenantLov',
      'authLevelValueRoleLov',
      'authLevelValueClientLov',
    ]);
    const {
      headerAuthType,
      authType,
      authLevelValueRole,
      authLevelValueMeaningRole,
      authLevelValueClient,
      authLevelValueMeaningClient,
      authLevelValueTenant,
      authLevelValueMeaningTenant,
      ...otherBasicData
    } = basicData;
    const values = {
      ...otherBasicData,
      authType: headerAuthType,
      authLevelValue: authLevelValueRole || authLevelValueClient || authLevelValueTenant,
      authLevelValueMeaning:
        authLevelValueMeaningRole || authLevelValueMeaningClient || authLevelValueMeaningTenant,
      httpAuthorization: {
        authType,
        authJson: JSON.stringify(results[1] || {}),
      },
    };
    return values;
  }

  @Bind()
  async handleTest() {
    const values = await this.validateValue();
    if (!values) {
      return false;
    }
    const { httpAuthorization } = values;
    return testAuth(httpAuthorization).then((res) => {
      getResponse(res);
    });
  }

  render() {
    const { isNew } = this.props;
    const { authLevel, authType, formData, noContent } = this.state;
    return (
      <Spin dataSet={this.basicFormDS}>
        <Form dataSet={this.basicFormDS} columns={2} labelWidth={155}>
          <Select name="authLevel" />
          {authLevel === 'TENANT' && <Lov name="authLevelValueTenantLov" />}
          {authLevel === 'ROLE' && <Lov name="authLevelValueRoleLov" />}
          {authLevel === 'CLIENT' && <Lov name="authLevelValueClientLov" />}
          <Select name="authType" />
          <TextField name="remark" />
        </Form>
        {!noContent && <Divider>{getLang('AUTH_INFO')}</Divider>}
        <DynamicForm
          isNew={isNew}
          columns={2}
          labelWidth={155}
          formCode={authType}
          formData={formData}
          onRef={(ref) => {
            this.saveForm = ref;
          }}
          onGetEmpty={(flag = true) => {
            this.setState({ noContent: flag });
          }}
        />
      </Spin>
    );
  }
}
