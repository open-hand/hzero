/**
 * @date 2019-12-02
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 网关模板新增、详情、编辑
 */
import React, { Component } from 'react';
import {
  Lov,
  Spin,
  Button,
  DataSet,
  Form,
  TextField,
  Switch,
  TextArea,
  IntlField,
} from 'choerodon-ui/pro';
import { Tabs } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';

import { detailDS, registerInfoDS } from '@/stores/gatewayTemplateDS';

const prefix = 'hiot.gatewayTemplate';
const { TabPane } = Tabs;
@formatterCollections({ code: ['hiot.gatewayTemplate', 'hiot.common'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    const { operation, id } = props.match.params;
    this.state = {
      operation,
      id,
      platform: '',
      registerFlag: '',
      configId: '',
      thingModelId: '',
      loading: false,
    };
    this.detailDS = new DataSet(detailDS());
    this.registerInfoDs = new DataSet(registerInfoDS());
  }

  componentDidMount() {
    const { operation, id } = this.state;
    if (operation === 'new') {
      this.detailDS.create({ enabled: 1 }, 0);
    } else {
      this.detailDS.queryParameter = { id };
      this.detailDS.query().then((resp) => {
        const { registerFlag, platform, configId, thingModelId } = resp;
        this.setState({ platform, registerFlag, configId, thingModelId });
      });
    }
  }

  @Bind()
  handleSave() {
    const { operation } = this.state;
    if (operation === 'detail') {
      this.setState({ operation: 'edit' });
    } else {
      this.detailDS.validate().then((resp) => {
        if (resp) {
          this.detailDS.submit().then((submitResp) => {
            const { success } = submitResp;
            if (success) {
              const { history } = this.props;
              history.push('/hiot/gateway-temp');
            }
          });
        } else if (!this.detailDS.current.get('configName')) {
          notification.error({
            message: intl.get('hiot.common.view.validation.authInfoMsg').d('请填写认证信息'),
          });
        }
        if (
          !this.detailDS.current.get('thingModelCode') ||
          !this.detailDS.current.get('thingModelName')
        ) {
          notification.error({
            message: intl
              .get('hiot.common.view.validation.baseInfoMsg')
              .d('请填写基本信息的必输信息'),
          });
        }
      });
    }
  }

  @Bind()
  async handleRegister(thingId, hubType, isRegistered, config) {
    this.setState({ loading: true });
    this.registerInfoDs.queryParameter = {
      thingModelId: thingId,
      hubType,
      flag: isRegistered,
      configId: config,
    };
    // 执行注册或停止注册操作
    await this.registerInfoDs.query().then((res) => {
      if (res) {
        notification.success(intl.get('hzero.common.notification.success').d('操作成功'));
        this.detailDS.query().then((resp) => {
          const { registerFlag, platform, configId, thingModelId } = resp;
          this.setState({ platform, registerFlag, configId, thingModelId });
        });
      }
    });
    this.setState({ loading: false });
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const {
      operation,
      platform,
      thingModelId,
      registerFlag,
      configId,
      loading = false,
    } = this.state;
    let title;
    let buttonName;
    if (operation === 'new') {
      title = intl.get(`${prefix}.view.header.new`).d('新建网关模型');
      buttonName = intl.get('hzero.common.button.save').d('保存');
    } else if (operation === 'detail') {
      title = intl.get(`${prefix}.view.header.detail`).d('网关模型详情');
      buttonName = intl.get('hzero.common.button.edit').d('编辑');
    } else {
      title = intl.get(`${prefix}.view.header.edit`).d('网关模型编辑');
      buttonName = intl.get('hzero.common.button.save').d('保存');
    }
    return (
      <>
        <Header title={title} backPath="/hiot/gateway-temp">
          <Button icon="save" color="primary" type="submit" onClick={this.handleSave}>
            {buttonName}
          </Button>
          {operation !== 'new' && (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}.register`,
                  type: 'button',
                  meaning: '网关模板-注册',
                },
              ]}
              loading={loading}
              onClick={() => this.handleRegister(thingModelId, platform, !registerFlag, configId)}
            >
              {!registerFlag
                ? intl.get('hiot.gatewayTemplate.view.button.startRegister').d('启用注册')
                : intl.get('hiot.gatewayTemplate.view.button.stopRegister').d('停用注册')}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Tabs defaultActiveKey="baseInfo">
            <TabPane key="baseInfo" tab={intl.get('hiot.common.view.baseInfo').d('基本信息')}>
              <Spin dataSet={this.detailDS}>
                <Form columns={3} dataSet={this.detailDS}>
                  <TextField
                    label={intl.get('hiot.common.code').d('编码')}
                    name="thingModelCode"
                    disabled={operation !== 'new'}
                    required
                  />
                  <IntlField name="thingModelName" disabled={operation !== 'new'} required />
                  <Switch
                    newLine
                    name="enabled"
                    label={intl.get('hzero.common.status.enable').d('启用')}
                    disabled={operation === 'detail'}
                  />
                  <TextArea
                    rows={1}
                    label={intl.get('hzero.common.explain').d('说明')}
                    name="description"
                    disabled={operation === 'detail'}
                  />
                  {operation === 'new' ? '' : <TextField name="cloudModelName" disabled newLine />}
                </Form>
              </Spin>
            </TabPane>
            <TabPane
              key="authInfo"
              tab={intl.get('hiot.gatewayTemplate.view.title.authInfo').d('认证信息')}
            >
              <Spin dataSet={this.detailDS}>
                <Form columns={3} dataSet={this.detailDS}>
                  <Lov name="configLov" disabled={operation !== 'new'} />
                  <TextField name="configName" disabled />
                </Form>
              </Spin>
            </TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
