/**
 * ProcessStart - 流程设置/流程启动
 * @date: 2018-8-21
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
// FIXME: withRouter 看 withRouter 是否是必需的
import { withRouter } from 'dva/router';
import { Button, Form, Input, Row, Col, Card } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import Lov from 'components/Lov';

import notification from 'utils/notification';
import uuid from 'uuid/v4';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';

import {
  EDIT_FORM_ITEM_LAYOUT,
  FORM_COL_3_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  DETAIL_CARD_CLASSNAME,
} from 'utils/constants';

import ListTable from './ListTable';
import Drawer from './Drawer';

/**
 * 流程启动组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} processVariable - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ processStart, loading }) => ({
  processStart,
  tenantId: getCurrentOrganizationId(),
  listLoading: loading.effects['processStart/getVariableList'],
  startLoading: loading.effects['processStart/startProcess'],
}))
@withRouter
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hwfp.processStart', 'hwfp.common'] })
export default class ProcessStart extends PureComponent {
  state = {
    visible: false, // 模态框是否可见
  };

  componentDidMount() {
    this.props.dispatch({
      type: 'processStart/updateState',
      payload: { variables: [] },
    });
  }

  // 打开新增模态框
  @Bind()
  showModal(flag) {
    this.setState({
      visible: flag,
      activeParamter: {},
    });
  }

  // 关闭模态框
  @Bind()
  handleCancel() {
    this.setState({
      visible: false,
    });
  }

  @Bind()
  handleEdit(record) {
    // TODO: 后台无法提供唯一标示
    this.setState({ activeParamter: record, visible: true });
  }

  // 保存模态框
  @Bind()
  handleAdd(values) {
    const {
      dispatch,
      processStart: { variables = [] },
    } = this.props;
    const { activeParamter = {} } = this.state;
    if (activeParamter.variableId) {
      const newVariables = variables.map((item) => {
        if (item.variableId === activeParamter.variableId) {
          return { ...item, ...values };
        } else {
          return item;
        }
      });
      dispatch({
        type: 'processStart/updateState',
        payload: { variables: newVariables },
      });
    } else {
      const newValue = {
        ...values,
        variableId: uuid(),
      };
      dispatch({
        type: 'processStart/updateState',
        payload: { variables: [...variables, newValue] },
      });
    }
    this.setState({
      visible: false,
    });
  }

  // 启动
  @Bind()
  processStart() {
    const {
      dispatch,
      tenantId,
      form,
      processStart: { variables = [] },
    } = this.props;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        // FIXME: 充当表单校验，后续table改为行内编辑
        const isEmptyFlag = variables.find((item) => item.requiredFlag && !item.value);
        if (isEmptyFlag) {
          return notification.warning({
            message: intl.get('hwfp.processStart.view.message.setParam').d('请设置参数值'),
          });
        }
        dispatch({
          type: 'processStart/startProcess',
          payload: { ...values, tenantId, variables },
        }).then((response) => {
          if (response) {
            notification.success();
          }
        });
      }
    });
  }

  // 删除
  @Bind()
  handleDelete(record) {
    const {
      dispatch,
      processStart: { variables = [] },
    } = this.props;
    const newVariables = variables.filter((item) => item.variableId !== record.variableId);
    dispatch({
      type: 'processStart/updateState',
      payload: { variables: newVariables },
    });
  }

  @Bind()
  handleProcessChange(value) {
    const { dispatch, tenantId } = this.props;
    if (value) {
      dispatch({
        type: 'processStart/getVariableList',
        payload: { processDefinitionKey: value, tenantId },
      });
    }
  }

  renderHeaderForm() {
    const {
      form: { getFieldDecorator },
      tenantId,
    } = this.props;
    return (
      <Card
        key="lov-setting-header"
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={<h3>{intl.get('hwfp.processStart.view.title.process').d('流程')}</h3>}
      >
        <Form>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get('hwfp.processStart.model.processStart.employeeNum').d('模拟用户')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator(
                  'employeeNum',
                  {}
                )(<Lov code="HPFM.EMPLOYEE" queryParams={{ tenantId, enabledFlag: 1 }} />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get('hwfp.processStart.model.processStart.businessKey').d('业务主键')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('businessKey', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hwfp.processStart.model.processStart.businessKey')
                          .d('业务主键'),
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get('hwfp.common.model.process.define').d('流程定义')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('processDefinitionKey', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hwfp.common.model.process.define').d('流程定义'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HWFP.PROCESS_DEFINITION"
                    queryParams={{ tenantId }}
                    onChange={this.handleProcessChange}
                  />
                )}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>
    );
  }

  render() {
    const {
      tenantId,
      form,
      listLoading,
      startLoading = false,
      processStart: { variables },
    } = this.props;
    const { visible, activeParamter } = this.state;
    const tableProps = {
      variables,
      loading: listLoading,
      onDelete: this.handleDelete,
      onEdit: this.handleEdit,
    };
    const drawerProps = {
      visible,
      variables,
      tenantId,
      anchor: 'right',
      initData: activeParamter,
      onCancel: this.handleCancel,
      onAdd: this.handleAdd,
    };
    return (
      <>
        <Header title={intl.get('hwfp.processStart.view.message.title.processStart').d('流程启动')}>
          <Button
            icon="caret-right"
            type="primary"
            onClick={this.processStart}
            loading={startLoading}
            disabled={isEmpty(form.getFieldValue('processDefinitionKey'))}
          >
            {intl.get('hwfp.processStart.view.button.start').d('启动')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">{this.renderHeaderForm()}</div>
          <Card
            key="lov-setting-header"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hwfp.processStart.view.title.params').d('参数')}</h3>}
          >
            <div className="table-list-operator" style={{ textAlign: 'right' }}>
              <Button onClick={this.showModal} icon="plus">
                {intl.get('hzero.common.button.create').d('新建')}
              </Button>
            </div>
            <ListTable {...tableProps} />
          </Card>
        </Content>
        <Drawer {...drawerProps} />
      </>
    );
  }
}
