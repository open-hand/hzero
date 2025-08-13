/**
 * RuleEngine - 规则引擎明细维护
 * @date: 2018-9-28
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Fragment, PureComponent } from 'react';
import { Col, Form, Input, Row, Select, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import classnames from 'classnames';

import Switch from 'components/Switch';
import { Content, Header } from 'components/Page';
import TLEditor from 'components/TLEditor';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import {
  DETAIL_EDIT_FORM_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
  ROW_HALF_WRITE_ONLY_CLASSNAME,
  ROW_READ_ONLY_CLASSNAME,
  ROW_READ_WRITE_CLASSNAME,
  ROW_WRITE_ONLY_CLASSNAME,
} from 'utils/constants';

import Drawer from './Drawer';

/**
 * 规则引擎明细组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} ruleEngine - 数据源
 * @reactProps {!Object} createRuleEngineLoading - 规则引擎创建是否完成
 * @reactProps {!Object} editRuleEngineLoading - 规则引擎编辑是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
@connect(({ ruleEngine, loading }) => ({
  ruleEngine,
  createRuleEngineLoading: loading.effects['ruleEngine/createRuleEngine'],
  editRuleEngineLoading: loading.effects['ruleEngine/editRuleEngine'],
  fetchDetailLoading: loading.effects['ruleEngine/fetchDetail'],
  testRuleEngineLoading: loading.effects['ruleEngine/testRuleEngine'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hpfm.ruleEngine'] })
export default class Detail extends PureComponent {
  state = {
    visible: false, // 测试模态框是否可见
  };

  /**
   * componentDidMount
   * render()调用后获取页面数据信息
   */
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ruleEngine/updateState',
      payload: {
        detail: {},
      },
    });
    this.handleSearch();
  }

  /**
   * 查询
   *
   * @memberof Detail
   */
  @Bind()
  handleSearch() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'ruleEngine/fetchDetail',
        payload: {
          ruleScriptId: id,
        },
      });
    } else {
      dispatch({
        type: 'ruleEngine/updateState',
        payload: {
          detail: {},
        },
      });
    }
    dispatch({
      type: 'ruleEngine/queryScriptTypeCode',
    });
  }

  /**
   * 保存
   */
  @Bind()
  handleSave() {
    const {
      form,
      dispatch,
      match,
      ruleEngine: { detail = {} },
    } = this.props;
    const { id } = match.params;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        // 校验通过，进行保存操作
        if (isUndefined(id)) {
          // 新建
          dispatch({
            type: 'ruleEngine/createRuleEngine',
            payload: {
              ...values,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              dispatch(
                routerRedux.push({
                  pathname: `/hpfm/rule-engine/detail/${res.ruleScriptId}`,
                })
              );
            }
          });
        } else {
          dispatch({
            type: 'ruleEngine/editRuleEngine',
            payload: {
              ...detail,
              ...values,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              this.handleSearch();
            }
          });
        }
      }
    });
  }

  /**
   * 打开测试模态框
   */
  @Bind()
  handleOpenTestModal() {
    this.setState({
      visible: true,
    });
  }

  /**
   * 关闭测试模态框
   *
   * @memberof Detail
   */
  @Bind()
  handleCloseTestModal() {
    const { dispatch } = this.props;
    this.setState({
      visible: false,
    });
    dispatch({
      type: 'ruleEngine/updateState',
      payload: { testContent: undefined },
    });
  }

  /**
   * 测试保存
   * @param {*} values
   */
  @Bind()
  handleTest(params) {
    const {
      dispatch,
      form: { getFieldValue },
    } = this.props;
    const tenantId = getFieldValue('tenantId') || getCurrentOrganizationId();
    const scriptCode = getFieldValue('scriptCode');
    dispatch({
      type: 'ruleEngine/testRuleEngine',
      payload: { tenantId, scriptCode, params },
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form: { getFieldDecorator },
      ruleEngine,
      fetchDetailLoading = false,
      createRuleEngineLoading = false,
      editRuleEngineLoading = false,
      testRuleEngineLoading = false,
      match: {
        params: { id },
      },
      match,
    } = this.props;
    const { detail = {}, scriptTypeCode = [], testContent, categoryList = [] } = ruleEngine;
    const { visible } = this.state;
    const drawerProps = {
      visible,
      testRuleEngineLoading,
      testContent,
      onCancel: this.handleCloseTestModal,
      onTest: this.handleTest,
    };
    return (
      <Fragment>
        <Header
          title={intl.get('hpfm.ruleEngine.view.message.title.detail').d('规则脚本明细')}
          backPath="/hpfm/rule-engine/list"
        >
          <ButtonPermission
            loading={createRuleEngineLoading || editRuleEngineLoading || fetchDetailLoading}
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '规则引擎明细-保存',
              },
            ]}
            onClick={this.handleSave}
            type="primary"
            icon="save"
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.check`,
                type: 'button',
                meaning: '规则引擎明细-校验',
              },
            ]}
            onClick={this.handleOpenTestModal}
            type="default"
            icon="check-circle"
            hidden={isUndefined(id)}
          >
            {intl.get('hpfm.ruleEngine.view.button.check').d('校验')}
          </ButtonPermission>
        </Header>
        <Content>
          <Spin spinning={fetchDetailLoading || createRuleEngineLoading || editRuleEngineLoading}>
            <Form className={DETAIL_EDIT_FORM_CLASSNAME}>
              {!isTenantRoleLevel() && (
                <Row
                  {...EDIT_FORM_ROW_LAYOUT}
                  className={classnames({
                    [ROW_WRITE_ONLY_CLASSNAME]: isUndefined(detail.tenantId),
                    [ROW_READ_ONLY_CLASSNAME]: !isUndefined(detail.tenantId),
                  })}
                >
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      required={isUndefined(detail.tenantId)}
                      label={intl.get('hpfm.ruleEngine.model.ruleEngine.tenantId').d('租户')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('tenantId', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl.get('hpfm.ruleEngine.model.ruleEngine.tenantId').d('租户'),
                            }),
                          },
                        ],
                        initialValue: detail.tenantId,
                      })(
                        isUndefined(detail.tenantId) ? (
                          <Lov code="HPFM.TENANT" textValue={detail.tenantName} />
                        ) : (
                          <React.Fragment>{detail.tenantName}</React.Fragment>
                        )
                      )}
                    </Form.Item>
                  </Col>
                </Row>
              )}
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={classnames({
                  [ROW_READ_WRITE_CLASSNAME]: !isUndefined(detail.scriptCode),
                  [ROW_WRITE_ONLY_CLASSNAME]: isUndefined(detail.scriptCode),
                })}
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hpfm.ruleEngine.model.ruleEngine.serverName').d('服务名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('serverName', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.ruleEngine.model.ruleEngine.serverName')
                              .d('服务名称'),
                          }),
                        },
                      ],
                      initialValue: detail.serverName,
                    })(
                      <Lov
                        code={
                          isTenantRoleLevel()
                            ? 'HADM.ROUTE.SERVICE_CODE.ORG'
                            : 'HADM.ROUTE.SERVICE_CODE'
                        }
                        textValue={detail.serverName}
                      />
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    required={isUndefined(detail.scriptCode)}
                    label={intl.get('hpfm.ruleEngine.model.ruleEngine.scriptCode').d('脚本编码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('scriptCode', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.ruleEngine.model.ruleEngine.scriptCode')
                              .d('脚本编码'),
                          }),
                        },
                        {
                          pattern: CODE_UPPER,
                          message: intl
                            .get('hzero.common.validation.codeUpper')
                            .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                        },
                        {
                          max: 30,
                          message: intl.get('hzero.common.validation.max', {
                            max: 30,
                          }),
                        },
                      ],
                      initialValue: detail.scriptCode,
                    })(
                      isUndefined(detail.scriptCode) ? (
                        <Input trim typeCase="upper" inputChinese={false} />
                      ) : (
                        <React.Fragment>{detail.scriptCode}</React.Fragment>
                      )
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_WRITE_ONLY_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hpfm.ruleEngine.model.ruleEngine.category').d('脚本分类')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('category', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.ruleEngine.model.ruleEngine.category')
                              .d('脚本分类'),
                          }),
                        },
                      ],
                      initialValue: detail.category,
                    })(
                      <Select allowClear>
                        {categoryList.map((item) => (
                          <Select.Option key={item.value} value={item.value}>
                            {item.meaning}
                          </Select.Option>
                        ))}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hpfm.ruleEngine.model.ruleEngine.scriptDescription').d('描述')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('scriptDescription', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.ruleEngine.model.ruleEngine.scriptDescription')
                              .d('描述'),
                          }),
                        },
                        {
                          max: 240,
                          message: intl.get('hzero.common.validation.max', {
                            max: 240,
                          }),
                        },
                      ],
                      initialValue: detail.scriptDescription,
                    })(
                      <TLEditor
                        label={intl
                          .get('hpfm.ruleEngine.model.ruleEngine.scriptDescription')
                          .d('描述')}
                        field="scriptDescription"
                        token={detail ? detail._token : null}
                      />
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_WRITE_ONLY_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hpfm.ruleEngine.model.ruleEngine.scriptTypeCode').d('类型')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('scriptTypeCode', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.ruleEngine.model.ruleEngine.scriptTypeCode')
                              .d('类型'),
                          }),
                        },
                      ],
                      initialValue: detail.scriptTypeCode,
                    })(
                      <Select allowClear>
                        {scriptTypeCode &&
                          scriptTypeCode.map((item) => (
                            <Select.Option key={item.value} value={item.value}>
                              {item.meaning}
                            </Select.Option>
                          ))}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hzero.common.status.enable').d('启用')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('enabledFlag', {
                      initialValue: isUndefined(detail.enabledFlag) ? 1 : detail.enabledFlag,
                    })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_HALF_WRITE_ONLY_CLASSNAME}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT_COL_2}
                    label={intl.get('hpfm.ruleEngine.model.ruleEngine.scriptContent').d('脚本内容')}
                  >
                    {getFieldDecorator('scriptContent', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.ruleEngine.model.ruleEngine.scriptContent')
                              .d('脚本内容'),
                          }),
                        },
                      ],
                      initialValue: detail.scriptContent,
                    })(<Input.TextArea autosize={{ minRows: 15 }} />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Spin>
        </Content>
        <Drawer {...drawerProps} />
      </Fragment>
    );
  }
}
