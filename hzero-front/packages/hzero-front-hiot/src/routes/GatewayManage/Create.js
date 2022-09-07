/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-10 20:05:28
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 创建网关-页面
 */
import React from 'react';
import {
  Form,
  TextField,
  Row,
  Col,
  TextArea,
  DataSet,
  Lov,
  Select,
  IntlField,
} from 'choerodon-ui/pro';
import { Tabs, Icon, Tooltip } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { gatewayManageDS } from '@/stores/gatewayManageDS';

const { TabPane } = Tabs;
@formatterCollections({ code: ['hiot.dataPointTemplate', 'hiot.gatewayManage', 'hiot.common'] })
export default class Create extends React.Component {
  constructor(props) {
    super(props);
    this.gatewayManageDS = new DataSet(gatewayManageDS());
    this.state = {
      isDisabled: false,
      isPrivateCloud: false,
      isJWT: false,
      isRedis: false,
    };
  }

  componentDidMount() {
    this.gatewayManageDS.create({}, 0);
  }

  /**
   * 保存网关信息
   */
  @Bind()
  async handleDeviceManageSave() {
    const res = await this.gatewayManageDS.submit();
    if (!res) {
      const gatewayCode = this.gatewayManageDS.current.get('gatewayCode');
      const gatewayName = this.gatewayManageDS.current.get('gatewayName');
      const thingGroupId = this.gatewayManageDS.current.get('thingGroupId');
      if (!this.gatewayManageDS.current.get('configName')) {
        notification.error({
          message: intl.get('hiot.common.view.validation.authInfoMsg').d('请填写认证信息'),
        });
      }
      if (!gatewayCode || !gatewayName || !thingGroupId) {
        notification.error({
          message: intl
            .get('hiot.common.view.validation.baseInfoMsg')
            .d('请填写基本信息的必输信息'),
        });
      }
      return false;
    }
    this.props.history.push('/hiot/gateway/manage/list');
  }

  @Bind()
  lovChange(val) {
    let id;
    let name;
    let platformMeaning;
    let currentPlatform;
    const isDisabled = !!val;
    if (val) {
      const { configId, configName, platformName, platform } = val;
      id = configId;
      name = configName;
      platformMeaning = platformName;
      currentPlatform = platform;
    }
    this.gatewayManageDS.records[0].set('configId', id);
    this.gatewayManageDS.records[0].set('configName', name);
    this.gatewayManageDS.records[0].set('platformMeaning', platformMeaning);
    this.gatewayManageDS.records[0].set('platform', currentPlatform);
    this.setState({
      isDisabled,
      isPrivateCloud: currentPlatform === 'OWN',
    });
  }

  /**
   * 下拉框变化时触发
   */
  @Bind()
  onSelectChange(value) {
    const isJWT = value ? value === 'JWT' : false;
    const isRedis = value ? value === 'REDIS' : false;
    this.setState({
      isJWT,
      isRedis,
    });
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const { isDisabled, isPrivateCloud, isJWT, isRedis } = this.state;
    return (
      <>
        <Header
          title={intl.get('hiot.gatewayManage.view.title.header.new').d('创建网关')}
          backPath="/hiot/gateway/manage/list"
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '创建网关-保存',
              },
            ]}
            color="primary"
            icon="save"
            onClick={this.handleDeviceManageSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Tabs defaultActiveKey="baseInfo">
            <TabPane tab={intl.get('hiot.common.view.baseInfo').d('基本信息')} key="baseInfo">
              <Row>
                <Col span={16}>
                  <Form dataSet={this.gatewayManageDS} columns={2}>
                    <TextField name="gatewayCode" required />
                    <IntlField name="gatewayName" required />
                    <Lov name="gateway" onChange={this.lovChange} />
                    <Lov name="project" required />
                    <TextField name="model" />
                    <TextField name="gatewayIp" />
                    <TextArea name="description" />
                  </Form>
                </Col>
              </Row>
            </TabPane>
            <TabPane
              tab={intl.get('hiot.gatewayManage.view.title.authInfo').d('认证信息')}
              key="authInfo"
            >
              <Row>
                <Col span={16}>
                  <Form dataSet={this.gatewayManageDS} columns={2} labelWidth={110}>
                    <Lov
                      name="configLov"
                      disabled={isDisabled}
                      onChange={(value) => {
                        this.lovChange(value);
                      }}
                    />
                    <TextField name="configName" disabled />
                    {isPrivateCloud && <Select name="authType" onChange={this.onSelectChange} />}
                    {isJWT && <TextField name="secret" />}
                    {isJWT && <Select name="from" />}
                    {isRedis && (
                      <TextField
                        name="username"
                        disabled
                        label={
                          <React.Fragment>
                            {intl.get('hiot.common.model.common.username').d('用户名')}
                            <Tooltip
                              title={intl
                                .get('hiot.gatewayManage.view.title.autoGeneration')
                                .d('自动生成')}
                            >
                              <Icon type="help_outline" style={{ marginTop: -2, marginLeft: 2 }} />
                            </Tooltip>
                          </React.Fragment>
                        }
                      />
                    )}
                    {isRedis && (
                      <TextField
                        name="password"
                        disabled
                        label={
                          <React.Fragment>
                            {intl.get('hiot.common.model.common.password').d('密码')}
                            <Tooltip
                              title={intl
                                .get('hiot.gatewayManage.view.title.autoGeneration')
                                .d('自动生成')}
                            >
                              <Icon type="help_outline" style={{ marginTop: -2, marginLeft: 2 }} />
                            </Tooltip>
                          </React.Fragment>
                        }
                      />
                    )}
                  </Form>
                </Col>
              </Row>
            </TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
