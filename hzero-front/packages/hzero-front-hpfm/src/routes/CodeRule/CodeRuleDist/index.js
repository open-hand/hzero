/**
 * CodeRuleDist - 编码规则分发层
 * @date: 2018-6-29
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Button, Card, Col, Form, Input, Modal, Row, Select, Table } from 'hzero-ui';
import { Bind, Debounce } from 'lodash-decorators';
import { connect } from 'dva';
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
import { createPagination } from 'utils/utils';
import notification from 'utils/notification';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
  ROW_HALF_WRITE_ONLY_CLASSNAME,
  ROW_LAST_CLASSNAME,
  ROW_READ_ONLY_CLASSNAME,
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

  return (
    <Modal
      destroyOnClose
      width={500}
      visible={editModalVisible}
      confirmLoading={loading}
      onCancel={() => cancelHandle()}
      footer={[
        <Button key="cancel" onClick={() => cancelHandle()}>
          {intl.get('hzero.common.button.cancel').d('取消')}
        </Button>,
        <Button key="on" type="primary" loading={loading} onClick={okHandle}>
          {intl.get('hzero.common.button.ok').d('确定')}
        </Button>,
      ]}
    >
      <Form>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
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
        <Row {...EDIT_FORM_ROW_LAYOUT}>
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
                      queryParams={{
                        ruleId,
                        tenantId,
                        lovCode: 'HPFM.COMPANY',
                        enabledFlag: 1,
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
        <Row {...EDIT_FORM_ROW_LAYOUT}>
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
              })(<Input />)}
            </FormItem>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col span={24}>
            <FormItem {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
              {form.getFieldDecorator('enabledFlag', {
                initialValue: enabledFlag === undefined ? 1 : enabledFlag,
              })(<Switch />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
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
@connect(({ codeRule, loading }) => ({
  codeRule,
  fetchDistLoading: loading.effects['codeRule/fetchDist'],
  addCodeRuleLoading: loading.effects['codeRule/addCodeRule'],
  fetchDetailLoading: loading.effects['codeRule/fetchDetail'],
  addDistLoading: loading.effects['codeRule/addDist'],
  deleteLoading: loading.effects['codeRule/removeDist'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hpfm.codeRule'],
})
export default class CodeRuleDist extends React.Component {
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
    const {
      dispatch,
      match,
      codeRule: { organizationId },
    } = this.props;
    const data = {
      ruleId: match.params.id,
      organizationId,
    };
    dispatch({
      type: 'codeRule/fetchDist',
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
        width: 200,
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
      codeRule: { keyValue, organizationId },
      form,
    } = this.props;
    const { editRecordData } = this.state;
    dispatch({
      type: 'codeRule/addDist',
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
    const {
      dispatch,
      codeRule: { organizationId },
      deleteLoading,
    } = this.props;
    const { selectedRows = {} } = this.state;
    const onOk = () => {
      dispatch({
        type: 'codeRule/removeDist',
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
    const { dispatch } = this.props;
    dispatch({
      type: 'codeRule/updateState',
      payload: {
        detail: {
          data: {},
        },
      },
    });
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
    const {
      dispatch,
      codeRule: { organizationId },
    } = this.props;
    this.setState(
      {
        modalVisible: !!flag,
      },
      () => {
        this.setState({ editRecordData: record || {} });
        dispatch({
          type: 'codeRule/updateState',
          payload: {
            detail: {
              data: {},
            },
          },
        });
      }
    );
    if (record && record.ruleDistId) {
      const data = {
        ruleDistId: record.ruleDistId,
        organizationId,
      };
      dispatch({
        type: 'codeRule/fetchDetail',
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
      codeRule: {
        organizationId,
        dist: { head = {}, line = {} },
      },
    } = this.props;
    const { ruleId } = head;
    const { pagination = {} } = line;
    if (ruleId) {
      dispatch({
        type: 'codeRule/fetchDist',
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
      codeRule: {
        organizationId,
        dist: { head = {} },
      },
    } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        dispatch({
          type: 'codeRule/addCodeRule',
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
      codeRule: { organizationId, keyValue },
    } = this.props;
    const { formValues } = this.state;
    const params = {
      page: pagination,
      formValues,
      organizationId,
      ruleId: keyValue.ruleId,
    };
    dispatch({
      type: 'codeRule/fetchDist',
      payload: params,
    });
  }

  /**
   * 渲染方法
   * @returns
   */
  render() {
    const {
      codeRule: {
        organizationId,
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
    } = this.props;
    const { selectedRows, modalVisible, editRecordData, editModalVisible, company } = this.state;
    const {
      params: { id: ruleId },
      path,
    } = match;
    const { getFieldDecorator } = this.props.form;
    const {
      ruleCode,
      ruleName,
      _token,
      tenantName,
      description,
      /* meaning, */ level,
      tenantId,
    } = head;
    const basePath = match.path.substring(0, match.path.indexOf('/dist'));
    const dataSource = line.list;
    const columns = this.handlecolumns();
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.ruleDistId),
      onChange: this.onSelectChange,
      getCheckboxProps: (record) => ({
        disabled: record.levelValue === 'GLOBAL' || record.enabledFlag === 1,
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
          <>
            <ButtonPermission
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
              icon="save"
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          </>
        </Header>
        <Content>
          <Card
            key="code-rule-header"
            title={intl.get('hpfm.codeRule.view.title.codeRule').d('编码规则')}
            bordered={false}
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
                    {ruleCode}
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
              {!organizationId && (
                <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_ONLY_CLASSNAME}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <FormItem
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.codeRule.model.codeRule.tenantName').d('租户')}
                    >
                      {tenantName}
                    </FormItem>
                  </Col>
                </Row>
              )}
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={classnames(ROW_LAST_CLASSNAME, ROW_HALF_WRITE_ONLY_CLASSNAME)}
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
                    })(<Input />)}
                  </FormItem>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="code-rule-header"
            title={intl.get('hpfm.codeRule.view.message.title.dist').d('规则明细')}
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
          >
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
            <Table
              bordered
              rowKey="ruleDistId"
              loading={fetchDistLoading}
              dataSource={dataSource}
              columns={columns}
              pagination={createPagination(line)}
              rowSelection={rowSelection}
              onChange={this.handleStandardTableChange}
            />
          </Card>
          <EditModal
            {...editModalMethods}
            wrapClassName="ant-modal-sidebar-right"
            transitionName="move-right"
            editModalVisible={editModalVisible}
            editRecordData={editRecordData}
            codes={code}
            company={company}
            tenantId={tenantId}
            loading={addDistLoading}
            ruleId={ruleId}
          />
          <CodeRuleDetail
            {...parentMethods}
            match={match}
            visible={modalVisible}
            fetchLoading={fetchDetailLoading}
            deleteLoading={deleteLoading}
            editRecordData={editRecordData}
          />
        </Content>
      </>
    );
  }
}
