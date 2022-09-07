import React, { Component } from 'react';
import {
  Lov,
  DataSet,
  Form,
  IntlField,
  Select,
  Switch,
  Tabs,
  Spin,
  TextArea,
  TextField,
} from 'choerodon-ui/pro';
import { Row, Col } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';

import {
  dataPointInfoTableDS,
  detailDS,
  earlyWarningTableDS,
  operatorDS,
  registerInfoDS,
  actionDataPointDS,
} from '@/stores/deviceTemplateDS';
import formatterCollections from 'utils/intl/formatterCollections';
import DataPointInfoTable from './DataPointInfoTable';

const prefix = 'hiot.deviceTemplate';

@formatterCollections({ code: ['hiot.deviceTemplate', 'hiot.common'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    const { operation, id } = props.match.params;
    this.state = {
      operation,
      isReadOnly: operation === 'detail',
      isEdit: operation === 'edit',
      deviceTempId: id,
      isReferred: 0,
      platform: '',
      registerFlag: '',
      configId: '',
      thingModelId: '',
      loading: false,
    };
    this.detailDS = new DataSet(detailDS());
    this.dataPointTableDS = new DataSet(dataPointInfoTableDS());
    this.earlyWarningTableDS = new DataSet(earlyWarningTableDS());
    this.operatorDS = new DataSet(operatorDS());
    this.registerInfoDs = new DataSet(registerInfoDS());
    this.actionDataPointDs = new DataSet(actionDataPointDS());
    this.backPath = '/hiot/device-temp';
  }

  componentDidMount() {
    const { deviceTempId, isReadOnly, isEdit } = this.state;
    if (isReadOnly || isEdit) {
      // 设备模板信息
      this.detailDS.setQueryParameter('id', deviceTempId);
      this.detailDS.query().then((resp) => {
        const { isReferred, registerFlag, platform, configId, thingModelId } = resp;
        this.setState({ isReferred, platform, registerFlag, configId, thingModelId });
      });
      // 数据点信息
      this.dataPointTableDS.setQueryParameter('thingTmpltId', deviceTempId);
      this.dataPointTableDS.query();
      // 告警规则信息
      this.earlyWarningTableDS.setQueryParameter('thingTmpltId', deviceTempId);
      this.earlyWarningTableDS.query();
    } else {
      this.detailDS.create({}, 0);
    }
    // this.handleEdit();
  }

  @Bind()
  handleBack() {
    const { history } = this.props;
    history.push(this.backPath);
  }

  @Bind()
  handleSave() {
    // 校验设备模板基本信息
    this.detailDS.validate().then((resp) => {
      if (resp) {
        // 获取设备模板基本信息数据
        const thingModel = this.detailDS.toData()[0];
        // 获取数据点基本信息
        const dpTableData = this.dataPointTableDS.toData();
        // if (isEmpty(dpTableData)) {
        //   notification.warning({
        //     message: intl.get(`${prefix}.message.atLeast.dataPoint`).d('请选择至少一条数据点！'),
        //   });
        //   return;
        // }
        const allDataPointIds = [];
        dpTableData.forEach((dataPoint) => {
          const { propertyModelId } = dataPoint;
          if (propertyModelId) {
            allDataPointIds.push(propertyModelId);
          }
        });
        // 校验预警公式中的数据点因子是否包含在数据点列表中
        const result = this.earlyWarningTableDS.toData().some((warning) => {
          const { formularJson } = warning;
          const dataPointIds = []; // 表达式中的数据点ID集合
          JSON.parse(formularJson).forEach((item) => {
            if (item.type === 'property') {
              dataPointIds.push(item.objectId);
            }
          });
          return dataPointIds.some((item) => allDataPointIds.indexOf(item) < 0);
        });
        if (result) {
          notification.warning({
            message: intl
              .get(`${prefix}.message.predict.mismatch`)
              .d('预警公式中的数据点与所选数据点不匹配！'),
          });
          return;
        }
        const propertyModelList = []; // 新增的数据点信息
        const deletePropertyModelList = []; // 删除的数据点信息
        const propGroupModelList = []; // 新增的数据点组模板
        const deletePropGroupModelList = []; // 删除的数据点组模板
        const { created: createdDP, destroyed: deleteDP } = this.dataPointTableDS;
        // 预警规则
        const {
          created: createdPR,
          destroyed: deletePR,
          updated: updatePR,
        } = this.earlyWarningTableDS;
        // 新增的数据点
        this.handleDPData(propGroupModelList, propertyModelList, createdDP);
        // 删除的数据点
        this.handleDPData(deletePropGroupModelList, deletePropertyModelList, deleteDP);
        const createPredictRuleList = this.handlePRData(createdPR, 'object'); // 新增的预警规则
        const deletePredictRuleList = this.handlePRData(deletePR, 'primaryKey'); // 删除的预警规则
        const updatePredictRuleList = this.handlePRData(updatePR, 'object'); // 更新的预警规则
        const createParams = {
          thingModel,
          propertyModelList,
          deletePropertyModelList,
          propGroupModelList,
          deletePropGroupModelList,
          createPredictRuleList,
          deletePredictRuleList,
          updatePredictRuleList,
        };
        this.sendRequest(createParams);
      } else if (!this.detailDS.current.get('configName')) {
        notification.error({
          message: intl.get('hiot.common.view.validation.authInfoMsg').d('请填写认证信息'),
        });
      }
      if (
        !this.detailDS.current.get('thingModelName') ||
        !this.detailDS.current.get('thingModelCode') ||
        !this.detailDS.current.get('category')
      ) {
        notification.error({
          message: intl
            .get('hiot.common.view.validation.baseInfoMsg')
            .d('请填写基本信息的必输信息'),
        });
      }
    });
  }

  /**
   * 处理数据点数据
   * @param groupArr 数据点模板数组
   * @param dotArr 数据点数据
   * @param records 数据点集合
   */
  @Bind()
  handleDPData(groupArr, dotArr, records) {
    records.forEach((record) => {
      const { data } = record;
      const { groupModelId, parentId, propertyModelId } = data;
      if (parentId === 0) {
        if (groupModelId) {
          groupArr.push(groupModelId);
        } else {
          dotArr.push(propertyModelId);
        }
      }
    });
  }

  /**
   * 处理预警规则数据
   * @param records 预警规则数据
   * @param returnType 处理后返回的类型 primaryKey object
   */
  @Bind()
  handlePRData(records, returnType) {
    const result = [];
    if (returnType === 'primaryKey') {
      // 返回主键数组
      records.forEach((record) => {
        const { data } = record;
        const { predictRuleId } = data;
        result.push(predictRuleId);
      });
    } else {
      // 返回对象数组
      records.forEach((record) => {
        const { data } = record;
        result.push(data);
      });
    }
    return result;
  }

  @Bind()
  async sendRequest(requestBody) {
    const { isEdit } = this.state;
    if (isEdit) {
      this.operatorDS.create(
        {
          method: 'put',
          ...requestBody,
        },
        0
      );
    } else {
      this.operatorDS.create(
        {
          method: 'post',
          ...requestBody,
        },
        0
      );
    }
    const resp = await this.operatorDS.submit();
    if (resp) {
      this.handleBack();
    }
  }

  @Bind()
  handleEdit() {
    this.setState({
      operation: 'edit',
      isReadOnly: false,
      isEdit: true,
    });
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
          const { isReferred, registerFlag, platform, configId, thingModelId } = resp;
          this.setState({ isReferred, platform, registerFlag, configId, thingModelId });
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
      isReadOnly,
      isEdit,
      operation,
      isReferred,
      platform,
      thingModelId,
      registerFlag,
      configId,
      loading,
    } = this.state;
    const { TabPane } = Tabs;
    const headers = {
      edit: (
        <Header
          title={intl.get(`${prefix}.view.header.editModel`).d('编辑设备模型')}
          backPath={this.backPath}
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '编辑设备模版-保存',
              },
            ]}
            icon="save"
            color="primary"
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.register`,
                type: 'button',
                meaning: '设备模板-注册',
              },
            ]}
            loading={loading}
            onClick={() => this.handleRegister(thingModelId, platform, !registerFlag, configId)}
          >
            {!registerFlag
              ? intl.get('hiot.deviceTemplate.view.button.startRegister').d('启用注册')
              : intl.get('hiot.deviceTemplate.view.button.stopRegister').d('停用注册')}
          </ButtonPermission>
        </Header>
      ),
      detail: (
        <Header
          title={intl.get(`${prefix}.view.header.detail`).d('设备模型详情')}
          backPath={this.backPath}
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.register`,
                type: 'button',
                meaning: '设备模板-注册',
              },
            ]}
            loading={loading}
            onClick={() => this.handleRegister(thingModelId, platform, !registerFlag, configId)}
          >
            {!registerFlag
              ? intl.get('hiot.deviceTemplate.view.button.startRegister').d('启用注册')
              : intl.get('hiot.deviceTemplate.view.button.stopRegister').d('停用注册')}
          </ButtonPermission>
        </Header>
      ),
      new: (
        <Header
          title={intl.get(`${prefix}.view.header.newModel`).d('新建设备模型')}
          backPath={this.backPath}
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '新建设备模版-保存',
              },
            ]}
            icon="save"
            color="primary"
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
      ),
    };
    return (
      <>
        {headers[operation || 'new']}
        <Content>
          <Tabs defaultActiveKey="baseInfo">
            <TabPane key="baseInfo" tab={intl.get('hiot.common.view.baseInfo').d('基本信息')}>
              <Row>
                <Col span={16}>
                  <Spin dataSet={this.detailDS}>
                    <Form columns={2} dataSet={this.detailDS}>
                      <TextField
                        label={intl.get('hiot.common.code').d('编码')}
                        name="thingModelCode"
                        disabled={isReadOnly || isEdit}
                        required
                      />
                      <IntlField
                        label={intl.get('hiot.common.name').d('名称')}
                        name="thingModelName"
                        disabled={isReadOnly || isEdit}
                        required
                      />
                      <Select
                        newLine
                        label={intl.get('hiot.common.device.type').d('设备类别')}
                        name="category"
                        disabled={(isReadOnly || !isEdit || isReferred === 1) && !!operation}
                        required
                      />
                      <Switch
                        name="enabled"
                        label={intl.get('hzero.common.status.enable').d('启用')}
                        disabled={(isReadOnly || !isEdit || isReferred === 1) && !!operation}
                      />
                      <TextArea
                        newLine
                        rows={1}
                        label={intl.get('hzero.common.explain').d('说明')}
                        name="description"
                        disabled={(isReadOnly || !isEdit) && !!operation}
                      />
                      {(operation === 'detail' || operation === 'edit') && (
                        <TextField name="cloudModelName" disabled />
                      )}
                    </Form>
                  </Spin>
                </Col>
              </Row>
            </TabPane>
            <TabPane
              key="authInfo"
              tab={intl.get('hiot.deviceTemplate.view.title.authInfo').d('认证信息')}
            >
              <Row>
                <Col span={16}>
                  <Spin dataSet={this.detailDS}>
                    <Form columns={2} dataSet={this.detailDS}>
                      <Lov name="configLov" disabled={isReadOnly || isEdit} />
                      <TextField name="configName" disabled />
                    </Form>
                  </Spin>
                </Col>
              </Row>
            </TabPane>
          </Tabs>
          {isEdit && (
            <Tabs defaultActiveKey="1">
              <TabPane key="1" tab={intl.get('hiot.common.view.data.point.info').d('数据点信息')}>
                <DataPointInfoTable
                  path={path}
                  DS={this.dataPointTableDS}
                  actionDs={this.actionDataPointDs}
                  registerFlag={registerFlag}
                  headDs={this.detailDS}
                  isReadOnly={isReadOnly}
                  isReferred={isReferred === 1}
                  thingModelId={thingModelId}
                />
              </TabPane>
            </Tabs>
          )}
        </Content>
      </>
    );
  }
}
