/**
 * CodeRuleDist - 编码规则分发层
 * @date: 2018-6-29
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Card, Col, Form, Input, Modal, Row, Select, Table } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { connect } from 'dva';
import { Bind, Debounce } from 'lodash-decorators';
import classnames from 'classnames';
import queryString from 'query-string';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
import { createPagination, getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
  ROW_HALF_READ_ONLY_CLASSNAME,
  ROW_HALF_WRITE_ONLY_CLASSNAME,
  ROW_LAST_CLASSNAME,
  ROW_READ_WRITE_CLASSNAME,
} from 'utils/constants';

import CodeRuleDetail from './CodeRuleDetail';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;
/**
 * 使用 Select 的 Option 组件
 */
const { Option } = Select;

/**
 *
 * @param {boolean} test
 * @param {React.CElement} component
 * @param {React.CElement} otherComponent
 * @returns {React.CElement}
 */
function optionComponent(test, component, otherComponent) {
  return test ? component : otherComponent;
}

/**
 * 编码规则编辑弹框
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} codes - 编码
 * @reactProps {Object} editRecordData - 当前编辑行数据
 * @reactProps {Object} editModalVisible - 控制modal显示/隐藏属性
 * @reactProps {Function} handleSave - 数据保存
 * @reactProps {Function} handleEditModal - 控制modal显示隐藏方法
 * @reactProps {Function} showCompany - 展示公司输入框
 * @reactProps {Object} company - 公司显隐标记
 * @reactProps {Object} tenantId - 租户编号
 * @return React.element
 */
const EditModal = Form.create({ fieldNameProp: null })((props) => {
  const {
    editModalVisible,
    codes,
    form,
    editRecordData,
    onHandleSaveCodeDist,
    onHandleEditModal,
    onShowCompany,
    company,
    tenantId,
    loading,
    isCurrentTenant,
    ruleId,
  } = props;
  // 当前租户是否和数据中的租户对应
  const { UNITTYPE } = codes;
  const { levelCode, levelValue, levelValueDescription, description, enabledFlag } = editRecordData;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      onHandleSaveCodeDist(fieldsValue);
    });
  };
  const cancelHandle = () => {
    onHandleEditModal(false);
    form.resetFields();
  };

  const formLayout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 15 },
  };
  const title = isEmpty(editRecordData)
    ? intl.get('hpfm.codeRule.view.title.addLevel').d('新增层级')
    : intl.get('hpfm.codeRule.view.title.editLevel').d('编辑层级');

  return (
    <Modal
      destroyOnClose
      title={title}
      visible={editModalVisible}
      confirmLoading={loading}
      width={500}
      onCancel={cancelHandle}
      footer={
        isCurrentTenant
          ? null
          : [
              <Button key="cancel" onClick={cancelHandle}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </Button>,
              <Button key="on" loading={loading} type="primary" onClick={okHandle}>
                {intl.get('hzero.common.button.ok').d('确定')}
              </Button>,
            ]
      }
    >
      <>
        <Form>
          <Row gutter={24}>
            <Col span={24}>
              <FormItem
                {...formLayout}
                label={intl.get('hpfm.codeRule.model.codeRule.meaning').d('层级')}
              >
                {form.getFieldDecorator('levelCode', {
                  initialValue: levelCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.codeRule.model.codeRule.meaning').d('层级'),
                      }),
                    },
                  ],
                })(
                  <Select
                    onChange={(value) => {
                      form.resetFields(['levelValue']);
                      onShowCompany(value);
                    }}
                    disabled={!!levelCode}
                  >
                    {UNITTYPE.map(
                      (c) =>
                        c.value !== 'GLOBAL' && (
                          <Option key={c.value} value={c.value}>
                            {c.meaning}
                          </Option>
                        )
                    )}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            {(company || levelCode === 'COM' || levelCode === 'CUSTOM') && (
              <Col span={24}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hpfm.codeRule.model.codeRule.levelValueDescription').d('值')}
                >
                  {form.getFieldDecorator('levelValue', {
                    initialValue: levelValue,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.codeRule.model.codeRule.levelValueDescription')
                            .d('值'),
                        }),
                      },
                    ],
                  })(
                    form.getFieldValue('levelCode') === 'COM' ? (
                      <Lov
                        disabled={isCurrentTenant}
                        queryParams={{
                          tenantId,
                          ruleId,
                          lovCode: 'HPFM.COMPANY',
                        }}
                        code="HPFM.CODE_RULE.COMPANY"
                        textValue={levelValueDescription}
                      />
                    ) : (
                      <Input />
                    )
                  )}
                </FormItem>
              </Col>
            )}
          </Row>
          <Row gutter={24}>
            <Col span={24}>
              <FormItem
                {...formLayout}
                label={intl.get('hpfm.codeRule.model.codeRule.description.dist').d('描述')}
              >
                {form.getFieldDecorator('description', {
                  initialValue: description,
                  rules: [
                    {
                      max: 80,
                      message: intl.get('hzero.common.validation.max', {
                        max: 80,
                      }),
                    },
                  ],
                })(<Input disabled={isCurrentTenant} />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={24}>
              <FormItem {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
                {form.getFieldDecorator('enabledFlag', {
                  initialValue: enabledFlag === undefined ? 1 : enabledFlag,
                })(<Switch disabled={isCurrentTenant} />)}
              </FormItem>
            </Col>
          </Row>
        </Form>
      </>
    </Modal>
  );
});

/**
 * 编码规则维护
 * @extends {Component} - React.Component
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} codeRule - 数据源
 * @reactProps {Object} fetchDistLoading - 数据加载是否完成
 * @reactProps {Object} addCodeRuleLoading - 数据添加加载是否完成
 * @reactProps {Object} fetchDetailLoading - 数据详情加载是否完成
 * @reactProps {Object} addDistLoading - 数据规则加载是否完成
 * @reactProps {Object} deleteLoading - 数据删除加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ codeRuleOrg, loading }) => ({
  codeRuleOrg,
  organizationId: getCurrentOrganizationId(),
  fetchDistLoading: loading.effects['codeRuleOrg/fetchDist'],
  addCodeRuleLoading: loading.effects['codeRuleOrg/addCodeRule'],
  fetchDetailLoading: loading.effects['codeRuleOrg/fetchDetail'],
  addDistLoading: loading.effects['codeRuleOrg/addDist'],
  deleteLoading: loading.effects['codeRuleOrg/removeDist'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hpfm.codeRule'],
})
export default class CodeRuleDist extends PureComponent {
  /**
   *内部状态
   */
  state = {
    selectedRows: [],
    modalVisible: false,
    editRecordData: {},
    editModalVisible: false,
    company: false,
  };

  /**
   *组件挂载后触发方法
   *
   */
  componentDidMount() {
    const { dispatch, match, organizationId } = this.props;
    const data = {
      ruleId: match.params.id,
      organizationId,
    };
    dispatch({
      type: 'codeRuleOrg/fetchDist',
      payload: data,
    });
  }

  /**
   * 生成表格头字段
   * @returns
   */
  @Bind()
  handlecolumns() {
    const { match } = this.props;
    return [
      {
        title: intl.get('hpfm.codeRule.model.codeRule.levelCodeDescription').d('层级'),
        dataIndex: 'levelCodeDescription',
        editable: true,
        required: true,
        width: 100,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.levelValueDescription').d('值'),
        dataIndex: 'levelValueDescription',
        editable: true,
        required: true,
        width: 200,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.description.dist').d('描述'),
        editable: true,
        dataIndex: 'description',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        type: 'checkbox',
        editable: true,
        render: enableRender,
        width: 80,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.usedFlag').d('是否已经使用'),
        dataIndex: 'usedFlag',
        type: 'checkbox',
        render: yesOrNoRender,
        width: 120,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 140,
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '编码规则明细-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleEditModal(true, record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'change',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.change`,
                      type: 'button',
                      meaning: '编码规则明细-修改编码段',
                    },
                  ]}
                  onClick={() => {
                    this.handleEditDetail(true, record);
                  }}
                >
                  {intl.get('hpfm.codeRule.view.option.change').d('修改编码段')}
                </ButtonPermission>
              ),
              len: 5,
              title: intl.get('hpfm.codeRule.view.option.change').d('修改编码段'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
  }

  /**
   *显示/隐藏公司选择框
   *
   * @param {Object} value 判断数据
   */
  @Bind()
  showCompany(value = {}) {
    const state = {
      company: false,
    };
    if (value === 'COM' || value === 'CUSTOM') {
      state.company = true;
    }
    this.setState(state);
  }

  /**
   * 保存编码规则
   * @param {Object} fieldsValue 表单数据
   */
  @Bind()
  handleSaveCodeDist(fieldsValue = {}) {
    const {
      dispatch,
      organizationId,
      codeRuleOrg: { keyValue },
      form,
    } = this.props;
    const { editRecordData } = this.state;
    dispatch({
      type: 'codeRuleOrg/addDist',
      payload: {
        ...editRecordData,
        ...fieldsValue,
        ...keyValue,
        organizationId,
      },
    }).then((response) => {
      if (response) {
        notification.success();
        this.handleEditModal(false);
        this.refreshLine();
        form.resetFields();
      }
    });
  }

  /**
   * 删除数据
   */
  @Bind()
  handleCodeRuleDist() {
    const { dispatch, organizationId, deleteLoading } = this.props;
    const { selectedRows = {} } = this.state;
    const onOk = () => {
      dispatch({
        type: 'codeRuleOrg/removeDist',
        payload: {
          selectedRows,
          organizationId,
        },
      }).then((response) => {
        if (response) {
          notification.success();
          this.refreshLine();
        }
      });
    };
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
      deleteLoading,
    });
  }

  /**
   * 控制编辑弹出层显示隐藏
   * @param {boolean} flag 显示/隐藏标记
   * @param {object} record 行数据
   * @param {object} form 表单
   */
  @Bind()
  handleEditModal(flag, record, form) {
    const state = {
      editModalVisible: flag,
      editRecordData: record || {},
      company: false,
    };
    if (form) {
      form.resetFields();
    }
    this.setState(state);
  }

  /**
   * 控制编码段详情modal弹出框显示隐藏
   * @param {boolean} flag 显/隐标记
   * @param {object} record 行数据
   */
  @Bind()
  handleEditDetail(flag, record) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'codeRuleOrg/updateState',
      payload: {
        detail: {
          data: {},
        },
      },
    });
    this.setState({
      modalVisible: !!flag,
      editRecordData: record || {},
    });
    if (record && record.ruleDistId) {
      const data = {
        ruleDistId: record.ruleDistId,
        organizationId,
      };
      dispatch({
        type: 'codeRuleOrg/fetchDetail',
        payload: data,
      });
    }
    if (!flag) {
      this.refreshLine();
    }
  }

  /**
   * 表格选中事件
   * @param {null} _ 占位符
   * @param {object} rows
   */
  @Bind()
  onSelectChange(_, rows) {
    this.setState({
      selectedRows: rows,
    });
  }

  /**
   * 刷新数据
   */
  @Bind()
  refreshLine() {
    const {
      dispatch,
      organizationId,
      codeRuleOrg: {
        dist: { head = {}, line = {} },
      },
    } = this.props;
    const { ruleId } = head;
    const { pagination = {} } = line;
    if (ruleId) {
      dispatch({
        type: 'codeRuleOrg/fetchDist',
        payload: {
          ruleId,
          organizationId,
          page: pagination,
        },
      });
    }
    this.setState({
      selectedRows: [],
    });
  }

  /**
   * 保存头数据
   */
  @Debounce(500)
  saveCodeRuleHead() {
    const {
      dispatch,
      form,
      organizationId,
      codeRuleOrg: {
        dist: { head = {} },
      },
    } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        dispatch({
          type: 'codeRuleOrg/addCodeRule',
          payload: {
            ...head,
            ...fieldsValue,
            organizationId,
          },
        }).then((response) => {
          if (response) {
            this.refreshLine();
            notification.success();
          }
        });
      }
    });
  }

  /**
   * 分页点击事件
   * @param {object} pagination 分页信息
   */
  @Bind()
  handleStandardTableChange(pagination = {}) {
    const {
      dispatch,
      organizationId,
      codeRuleOrg: { keyValue },
    } = this.props;
    const { formValues } = this.state;
    const params = {
      page: pagination,
      formValues,
      organizationId,
      ruleId: keyValue.ruleId,
    };
    dispatch({
      type: 'codeRuleOrg/fetchDist',
      payload: params,
    });
  }

  /**
   * 渲染方法
   * @returns
   */
  render() {
    const {
      codeRuleOrg: {
        dist: { head = {}, line = {} },
        code,
      },
      match,
      location: { search },
      fetchDistLoading,
      addCodeRuleLoading,
      fetchDetailLoading,
      addDistLoading,
      deleteLoading,
      organizationId,
    } = this.props;
    const {
      params: { id: ruleId },
      path,
    } = match;
    const { getFieldDecorator } = this.props.form;
    const { ruleCode, ruleName, description, level, tenantId, _token } = head;
    const basePath = match.path.substring(0, match.path.indexOf('/dist'));
    const { selectedRows, modalVisible, editRecordData, editModalVisible, company } = this.state;
    // 当前租户是否和数据中的租户对应
    const isCurrentTenant = tenantId !== undefined ? tenantId !== organizationId : false;
    const dataSource = line.list;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const columns = this.handlecolumns();
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.ruleDistId),
      onChange: this.onSelectChange,
      getCheckboxProps: (record) => ({
        disabled: isCurrentTenant || record.levelValue === 'GLOBAL' || record.enabledFlag === 1,
      }),
    };
    const parentMethods = {
      handleEditDetail: this.handleEditDetail,
    };
    const editModalMethods = {
      onHandleEditModal: this.handleEditModal,
      onHandleSaveCodeDist: this.handleSaveCodeDist,
      onShowCompany: this.showCompany,
    };
    return (
      <>
        <Header
          title={intl.get('hpfm.codeRule.view.message.title.dist').d('规则明细')}
          backPath={
            path.indexOf('/private') === 0
              ? `${basePath}/list?access_token=${accessToken}`
              : `${basePath}/list`
          }
        >
          {isCurrentTenant ? null : (
            <>
              <ButtonPermission
                icon="save"
                type="primary"
                loading={addCodeRuleLoading}
                permissionList={[
                  {
                    code: `${match.path}.button.save`,
                    type: 'button',
                    meaning: '编码规则明细-保存',
                  },
                ]}
                onClick={this.saveCodeRuleHead.bind(this)}
              >
                {intl.get('hzero.common.button.save').d('保存')}
              </ButtonPermission>
            </>
          )}
        </Header>
        <Content>
          <Card
            key="code-rule-header"
            bordered={false}
            title={<h3>{intl.get('hpfm.codeRule.view.title.codeRule').d('编码规则')}</h3>}
            className={DETAIL_CARD_CLASSNAME}
            loading={fetchDistLoading}
          >
            <Form>
              <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.codeRule.model.codeRule.ruleCode').d('规则代码')}
                  >
                    {getFieldDecorator('ruleCode', {
                      initialValue: ruleCode,
                    })(optionComponent(true, <>{ruleCode}</>, null))}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.codeRule.model.codeRule.ruleName').d('规则名称')}
                  >
                    {getFieldDecorator('ruleName', {
                      initialValue: ruleName,
                    })(
                      <TLEditor
                        label={intl.get('hpfm.codeRule.model.codeRule.ruleName').d('规则名称')}
                        field="ruleName"
                        token={_token}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={classnames(ROW_LAST_CLASSNAME, {
                  [ROW_HALF_READ_ONLY_CLASSNAME]: isCurrentTenant,
                  [ROW_HALF_WRITE_ONLY_CLASSNAME]: !isCurrentTenant,
                })}
              >
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    {...EDIT_FORM_ITEM_LAYOUT_COL_2}
                    label={intl.get('hpfm.codeRule.model.codeRule.description').d('规则描述')}
                  >
                    {getFieldDecorator('description', {
                      initialValue: description,
                      rules: [
                        {
                          max: 80,
                          message: intl.get('hzero.common.validation.max', {
                            max: 80,
                          }),
                        },
                      ],
                    })(<Input disabled={isCurrentTenant} />)}
                  </FormItem>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="code-rule-line"
            bordered={false}
            title={<h3>{intl.get('hpfm.codeRule.view.title.codeRuleLine').d('层级')}</h3>}
            className={DETAIL_CARD_TABLE_CLASSNAME}
          >
            {isCurrentTenant ? null : (
              <div className="table-list-operator">
                {level !== 'P' && (
                  <ButtonPermission
                    permissionList={[
                      {
                        code: `${match.path}.button.add`,
                        type: 'button',
                        meaning: '编码规则明细-新增层级',
                      },
                    ]}
                    onClick={() => this.handleEditModal(true)}
                  >
                    {intl.get('hpfm.codeRule.view.detail.button.create').d('新增层级')}
                  </ButtonPermission>
                )}
                {level !== 'P' && (
                  <ButtonPermission
                    permissionList={[
                      {
                        code: `${match.path}.button.delete`,
                        type: 'button',
                        meaning: '编码规则明细-删除层级',
                      },
                    ]}
                    onClick={this.handleCodeRuleDist}
                    disabled={selectedRows.length <= 0}
                  >
                    {intl.get('hpfm.codeRule.view.detail.button.delete').d('删除层级')}
                  </ButtonPermission>
                )}
              </div>
            )}
            <Table
              loading={fetchDistLoading}
              rowKey="ruleDistId"
              dataSource={dataSource}
              columns={columns}
              pagination={createPagination(line)}
              rowSelection={rowSelection}
              onChange={this.handleStandardTableChange}
              bordered
            />
          </Card>
          <EditModal
            {...editModalMethods}
            editModalVisible={editModalVisible}
            editRecordData={editRecordData}
            codes={code}
            company={company}
            tenantId={tenantId}
            isCurrentTenant={isCurrentTenant}
            loading={addDistLoading}
            ruleId={ruleId}
          />
          <CodeRuleDetail
            {...parentMethods}
            match={match}
            visible={modalVisible}
            isCurrentTenant={isCurrentTenant}
            fetchLoading={fetchDetailLoading}
            deleteLoading={deleteLoading}
            editRecordData={editRecordData}
          />
        </Content>
      </>
    );
  }
}
