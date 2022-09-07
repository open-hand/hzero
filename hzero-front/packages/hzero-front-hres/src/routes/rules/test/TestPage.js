/*
 * 规则引擎测试界面
 * @date: 2019-10-28 09:52:36
 * @author: FQL <qilin.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
// import { observer } from 'mobx-react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import {
  DataSet,
  Table,
  Button,
  Form,
  TextArea,
  TextField,
  Lov,
  Tabs,
  NumberField,
  DatePicker,
  Select,
  DateTimePicker,
} from 'choerodon-ui/pro';
import { Collapse, Row, Col } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import querystring from 'querystring';

import { Header, Content } from 'components/Page';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { HZERO_HRES } from 'utils/config';
import formatterCollections from 'utils/intl/formatterCollections';
import { getColumnList, executeTest } from '@/services/ruleTestService';

import RuleHeaderTestDS from '../stores/RuleHeaderTestDS';
import RuleTestDS from '../stores/RulesTestDS';
import './index.less';

const CollapsePanel = Collapse.Panel;
const { TabPane } = Tabs;

@connect()
// @observer
@formatterCollections({ code: ['hres.rule', 'hres.common'] })
export default class TestPage extends Component {
  ruleHeaderTestDS = new DataSet({
    ...RuleHeaderTestDS(),
    queryParameter: {
      ruleCode: this.props.match.params.code,
      tenantId: getCurrentOrganizationId(),
    },
    events: {
      load: ({ dataSet }) => {
        dataSet.current.set('serviceCode', 'hzero-rule-engine');
      },
    },
  });

  ruleTestDS = new DataSet({
    ...RuleTestDS,
    transport: {
      submit: ({ data, params }) => {
        const headerData = this.ruleHeaderTestDS.current.toData();
        const list = data.map(item => {
          if (item) {
            const temp = item;
            delete temp._status;
            delete temp.__id;
            return temp;
          }
          return item;
        });
        const submitData = {
          ruleCode: headerData && headerData.ruleCode,
          serviceCode: headerData && headerData.serviceCode,
          dataList: JSON.stringify(list),
        };
        return {
          url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/test-rule`,
          data: submitData,
          params: { tenantId: getCurrentOrganizationId(), ...params },
          method: 'POST',
        };
      },
    },
  });

  constructor(props) {
    super(props);
    this.state = {
      columnList: [], // 存储动态列名
    };
  }

  componentDidMount() {
    const {
      location: { state = {} },
      history,
    } = this.props;
    const { prePage } = state;
    history.push({
      state: { prePage },
    });
    getColumnList({
      ruleCode: this.props.match.params.code,
      tenantId: getCurrentOrganizationId(),
    }).then(res => {
      if (res && Array.isArray(res) && res.length > 0) {
        this.setState({
          columnList: res,
        });
      }
    });
  }

  get buttons() {
    return [
      <Button
        key="create"
        icon="playlist_add"
        color="primary"
        funcType="flat"
        onClick={this.addLine}
      >
        {intl.get('hres.rule.view.button.addLine').d('新增行')}
      </Button>,
    ];
  }

  @Bind()
  addLine() {
    this.ruleTestDS.create({}, 0);
  }

  /**
   * 获取table列
   */
  get columns() {
    const { columnList } = this.state;
    const list = columnList.map(item => {
      this.ruleTestDS.addField(item.fieldCode, {
        name: item.fieldCode,
        type: item.fieldType,
        label: item.fieldName,
        required: item.isRequired === 'Y',
        format: item.maskCode && item.maskCode.replace('yyyy-MM-dd', 'YYYY-MM-DD'),
      });
      return {
        name: item.fieldCode,
        editor: () => {
          switch (item.fieldType) {
            case 'DATE':
              if (item.maskCode === 'yyyy-MM-dd HH:mm:ss') {
                return <DateTimePicker format={item.maskCode} />;
              }
              return <DatePicker format={item.maskCode} />;
            case 'STRING':
              return <TextField />;
            case 'NUMBER':
              return <NumberField step={1} />;
            default:
              return <TextField />;
          }
        },
      };
    });
    return [
      ...list,
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        command: this.commands,
        align: 'center',
      },
    ];
  }

  /**
   * 行内操作按钮组
   */
  @Bind()
  commands({ record }) {
    return [
      <span className="action-link">
        <a onClick={() => this.handleDelete(record)}>
          {intl.get('hzero.common.button.delete').d('删除')}
        </a>
      </span>,
    ];
  }

  @Bind()
  handleDelete(record) {
    this.ruleTestDS.remove([record]);
  }

  /**
   * 执行调用
   */
  @Bind()
  async handleExecute() {
    if (this.ruleTestDS.length > 0 && this.ruleHeaderTestDS.current.get('jsonInput')) {
      notification.warning({
        message: intl
          .get('hres.rule.view.message.both.entry')
          .d('无法通过参数和json格式同时录入数据'),
      });
      return;
    }
    const isValidate = await this.ruleHeaderTestDS.validate(false, true);
    if (!isValidate) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      return;
    }
    if (this.ruleTestDS.length > 0) {
      const res = await this.ruleTestDS.submit();
      if (res && res.success) {
        const obj = res.content && res.content[0];
        this.setResponseData(obj);
      }
    } else {
      const headerData = this.ruleHeaderTestDS.current.toData();
      const submitData = {
        ruleCode: headerData && headerData.ruleCode,
        serviceCode: headerData && headerData.serviceCode,
        dataList: headerData.jsonInput,
      };
      const result = await executeTest(submitData);
      if (result) {
        this.setResponseData(result);
      }
    }
  }

  /**
   * 设置执行调用的返回值
   * @param {object} obj - 返回数据
   */
  setResponseData(obj) {
    this.ruleHeaderTestDS.current.set('runId', obj.runId);
    this.ruleHeaderTestDS.current.set('outParam', obj.outParam);
    this.ruleHeaderTestDS.current.set('errorMessage', obj.errorMessage);
    this.ruleHeaderTestDS.current.set('status', obj.status);
  }

  /**
   * 流程详情页
   */
  @Bind()
  goToFlow(type) {
    if (isEmpty(this.ruleHeaderTestDS.current.get('runId'))) {
      notification.warning({
        message: intl.get('hres.rule.view.message.execute.failure').d('执行失败，无流程'),
      });
      return;
    }
    const { dispatch } = this.props;
    const pathname =
      type === 'detail'
        ? `/hres/execute/detail/${this.ruleHeaderTestDS.current.get('runId')}/code`
        : `/hres/execute/flow/${this.ruleHeaderTestDS.current.get(
            'ruleCode'
          )}/${this.ruleHeaderTestDS.current.get('runId')}`;
    dispatch(
      routerRedux.push({
        pathname,
      })
    );
  }

  render() {
    const {
      location: { search },
      match,
    } = this.props;
    const { code } = match.params;
    const { isFlow, pageType, ruleName } = querystring.parse(search.substring(1));
    const flowPath = `/hres/rules/flow/detail/${code}?isFlow=true&pageType=${pageType}&ruleName=${ruleName}`;
    const backPath = isFlow === 'true' ? flowPath : '/hres/rules/list';
    return (
      <React.Fragment>
        <Header
          title={intl.get('hres.rule.view.title.test').d('规则引擎测试界面')}
          backPath={backPath}
        >
          <Button icon="" color="primary" onClick={this.handleExecute}>
            {intl.get('hres.rule.view.button.execute').d('执行调用')}
          </Button>
        </Header>
        <Content>
          <Form dataSet={this.ruleHeaderTestDS} columns={3}>
            <TextField name="ruleCode" disabled />
            <TextField name="ruleName" disabled />
            <Lov name="serviceRouteLov" />
          </Form>
          <Collapse defaultActiveKey={['invoke', 'return', 'exception']}>
            <CollapsePanel
              header={intl.get('hres.rule.model.rule.executeData').d('调用数据')}
              key="invoke"
            >
              <Tabs animated={false}>
                <TabPane tab={intl.get('hres.rule.view.tab.param').d('参数录入')} key="param">
                  <Table
                    buttons={this.buttons}
                    pagination={false}
                    dataSet={this.ruleTestDS}
                    columns={this.columns}
                  />
                </TabPane>
                <TabPane tab={intl.get('hres.rule.view.tab.json').d('json格式录入')} key="json">
                  <Form dataSet={this.ruleHeaderTestDS} columns={1}>
                    <TextArea name="jsonInput" rows={6} resize="vertical" />
                  </Form>
                </TabPane>
              </Tabs>
            </CollapsePanel>
            <CollapsePanel
              header={intl.get('hres.rule.view.rule.responseData').d('返回数据')}
              key="return"
            >
              <Form dataSet={this.ruleHeaderTestDS} columns={4}>
                <TextArea rows={6} name="outParam" resize="vertical" disabled colSpan={4} />
                <Row className="response-data" colSpan={4}>
                  <Col span={2}>
                    <span>{intl.get('hres.rule.model.rule.executeStatus').d('执行状态')}:</span>
                  </Col>
                  <Col span={4}>
                    <Select disabled name="status" />
                  </Col>
                  <Col span={2}>
                    <span>{intl.get('hres.rule.model.rule.executeID').d('执行ID')}:</span>
                  </Col>
                  <Col span={4}>
                    <TextField disabled name="runId" />
                  </Col>
                  <Col offset={8} span={2} style={{ textAlign: 'right' }}>
                    <Button color="primary" onClick={() => this.goToFlow('status')}>
                      {intl.get('hres.rule.view.button.flowStatus').d('流程状态')}
                    </Button>
                  </Col>
                  <Col span={2} style={{ textAlign: 'right' }}>
                    <Button color="primary" onClick={() => this.goToFlow('detail')}>
                      {intl.get('hres.rule.view.button.flowDetail').d('流程详情')}
                    </Button>
                  </Col>
                </Row>
              </Form>
            </CollapsePanel>
            <CollapsePanel
              header={intl.get('hres.rule.model.rule.exception').d('异常信息')}
              key="exception"
            >
              <Form dataSet={this.ruleHeaderTestDS} columns={1}>
                <TextArea name="errorMessage" rows={6} resize="vertical" disabled />
              </Form>
            </CollapsePanel>
          </Collapse>
        </Content>
      </React.Fragment>
    );
  }
}
