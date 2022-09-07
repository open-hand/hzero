/**
 * 规则组件页面
 * @Author: zhangzhicen <zhicen.zhang@hand-china.com>
 * @Date: 2019/10/17 14:40
 * @LastEditTime: 2019/10/25 9:37
 * @Copyright: Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import { DataSet, Form, TextField, Tabs, Button } from 'choerodon-ui/pro';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HRES } from 'utils/config';
import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import querystring from 'querystring';
import { saveNode } from '@/utils/saveNode';
import RuleFieldDetailPage from '../detail/RuleFieldDetailPage';
import RuleMaintenanceDetailPage from '../detail/RuleMaintenanceDetailPage';
import { HeaderDS, RuleFieldDS, RuleMaintenanceDS } from '../stores';

const { TabPane } = Tabs;
@connect()
@formatterCollections({ code: ['hres.ruleComponent', 'hres.common'] })
export default class ListPage extends Component {
  RuleFieldDS = new DataSet({
    ...RuleFieldDS(),
    queryParameter: {
      tenantId: getCurrentOrganizationId(),
    },
    events: {
      query: () => {
        this.setState({
          renderForce: false,
        });
      },
      indexChange: () => {
        this.setState({
          headerDisabled: true,
        });
      },
    },
  });

  RuleMaintenanceDS = new DataSet({
    ...RuleMaintenanceDS(),
    queryParameter: {
      tenantId: getCurrentOrganizationId(),
    },
    autoQueryAfterSubmit: false,
    transport: {
      read: ({ data, params }) => ({
        url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule-component-data`,
        method: 'GET',
        params: { ...params, ...data },
      }),
      destroy: ({ data, params, dataSet }) => {
        const destroyData = this.cleanDirty(data, dataSet);
        return {
          url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule-component-data/delete`,
          data: destroyData,
          params,
        };
      },
      submit: ({ data, params, dataSet }) => {
        const submitData = this.cleanDirty(data, dataSet);
        if (submitData.length === 0) {
          notification.info({
            message: intl
              .get('hres.ruleComponent.view.message.validate.nodata')
              .d('沒有需要保存的内容'),
          });
          return;
        }
        return {
          url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule-component-data/submit`,
          data: submitData,
          params,
        };
      },
    },
  });

  HeaderDS = new DataSet({
    ...HeaderDS(),
  });

  constructor(props) {
    super(props);
    this.state = {
      renderForce: false,
      activeKey: 'field',
      headerDisabled: false,
    };
  }

  async componentDidMount() {
    const {
      location: { search },
      match,
    } = this.props;
    const { id, code } = match.params;
    const { componentName } = querystring.parse(search.substring(1));
    this.RuleFieldDS.setQueryParameter('ruleCode', code);
    this.RuleMaintenanceDS.setQueryParameter('ruleCode', code);
    this.HeaderDS.removeAll();
    if (!isEmpty(componentName)) {
      this.setState({
        headerDisabled: true,
      });
      this.RuleFieldDS.setQueryParameter('ruleComponentName', componentName);
      this.RuleMaintenanceDS.setQueryParameter('ruleComponentName', componentName);
      this.RuleFieldDS.query();
      this.HeaderDS.create({
        ruleComponentName: componentName,
        ruleCode: code,
        tenantId: getCurrentOrganizationId(),
      });
    } else {
      this.HeaderDS.create({ id, ruleCode: code, tenantId: getCurrentOrganizationId() });
    }
  }

  /**
   * tab 切换重渲染明细table
   */
  @Bind()
  handelTabChange(key) {
    if (key === 'field') {
      this.setState({
        activeKey: key,
      });
    }
    if (key !== 'field') {
      this.setState({
        renderForce: true,
        activeKey: key,
      });
    }
  }

  /**
   * 提交和删除请求时删除多余数据
   * @param data
   * @param dataSet
   * @returns {*}
   */
  @Bind()
  cleanDirty(data, dataSet) {
    const cleanData = [];
    data.map(item => {
      const updateItem = item;
      if (updateItem.data) {
        const deleteObjList = Object.keys(updateItem.data).filter(m => /Lov$/.test(m));
        deleteObjList.forEach(m => {
          if (dataSet.getField(`data.${m}`).type === 'object') {
            delete updateItem.data[m];
          }
        });
        cleanData.push(updateItem);
      }
      return updateItem;
    });
    return cleanData;
  }

  /**
   * 保存按钮
   */
  @Bind()
  async handleSubmit() {
    const {
      dispatch,
      location: { search },
      match,
    } = this.props;
    const { id, code } = match.params;
    const { componentName, pageType, ruleName } = querystring.parse(search.substring(1));
    if (isEmpty(componentName) && (await this.HeaderDS.validate(false, false))) {
      const res = await this.HeaderDS.submit();
      if (!isEmpty(res) && !res.status) {
        await saveNode(res.content[0].processNode, id);
        const pathname = `/hres/rules/flow/rule-component/detail/${code}/${id}`;
        dispatch(
          routerRedux.push({
            pathname,
            search: querystring.stringify({
              componentName: res.content[0].ruleComponentList[0].ruleComponentName,
              pageType,
              ruleName,
            }),
          })
        );
        this.setState({
          headerDisabled: true,
        });
      }
      this.RuleMaintenanceDS.setQueryParameter(
        'ruleComponentName',
        res.content[0].ruleComponentList[0].ruleComponentName
      );
      return false;
    }
    if (
      !(await this.HeaderDS.validate(false, false)) ||
      !(await this.RuleMaintenanceDS.validate(false, false))
    ) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      return false;
    }
    if (this.RuleFieldDS.length === 0) {
      notification.info({
        message: intl.get('hres.ruleComponent.view.message.validate.submit').d('请创建通用字段'),
      });
      return;
    }
    if (!this.RuleMaintenanceDS.isModified()) {
      notification.info({
        message: intl.get('hres.rule.view.message.validate.submitError').d('表单未做修改'),
      });
      return;
    }
    if (this.RuleMaintenanceDS.isModified()) {
      await this.RuleMaintenanceDS.submit();
      await this.RuleMaintenanceDS.query();
    }
  }

  render() {
    const {
      location: { search },
      match,
    } = this.props;
    const { code, id } = match.params;
    const { pageType, componentName, ruleName } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    return (
      <React.Fragment>
        <Header
          title={`${intl
            .get('hres.ruleComponent.model.ruleCmp.ruleComponent')
            .d('规则组件')}_${ruleName}`}
          backPath={`/hres/rules/flow/detail/${code}?pageType=${pageType}&ruleName=${ruleName}`}
        >
          {!frozenFlag && (
            <React.Fragment>
              <Button
                color="primary"
                icon="save"
                onClick={this.handleSubmit}
                style={{ marginRight: 8 }}
              >
                {intl.get('hzero.common.button.save').d('保存')}
              </Button>
            </React.Fragment>
          )}
        </Header>
        <Content>
          <Form columns={3} dataSet={this.HeaderDS} labelWidth={120}>
            <TextField
              restrict="\S"
              name="ruleComponentName"
              disabled={this.state.headerDisabled}
            />
          </Form>
          <Tabs activeKey={this.state.activeKey} onChange={this.handelTabChange} animated={false}>
            <TabPane
              tab={intl.get('hres.ruleComponent.model.ruleCmp.ruleField').d('规则字段')}
              key="field"
            >
              <RuleFieldDetailPage
                dataSet={this.RuleFieldDS}
                headerDataSet={this.HeaderDS}
                maintenanceDataSet={this.RuleMaintenanceDS}
                frozenFlag={frozenFlag}
                ruleCode={code}
                id={id}
                componentName={componentName}
              />
            </TabPane>
            <TabPane
              tab={intl.get('hres.ruleComponent.model.ruleCmp.ruleMaintenance').d('规则维护')}
              key="maintenance"
              disabled={this.RuleFieldDS.length === 0}
            >
              {this.state.renderForce && (
                <RuleMaintenanceDetailPage
                  dataSet={this.RuleMaintenanceDS}
                  headerDataSet={this.HeaderDS}
                  fieldDataSet={this.RuleFieldDS}
                  frozenFlag={frozenFlag}
                  code={code}
                />
              )}
            </TabPane>
          </Tabs>
        </Content>
      </React.Fragment>
    );
  }
}
