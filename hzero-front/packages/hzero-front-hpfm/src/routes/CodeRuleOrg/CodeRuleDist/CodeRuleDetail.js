/**
 * CodeRuleDetail - 编码规则详情层
 * @date: 2019-1-11
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Form, Input, InputNumber, Modal, Row, Select, Table, Col } from 'hzero-ui';
import { connect } from 'dva';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId, tableScrollWidth } from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;
/**
 * 使用 Select 的 Option 组件
 */
const { Option } = Select;
/**
 * modal的侧滑属性
 */
const otherProps = {
  wrapClassName: 'ant-modal-sidebar-right',
  transitionName: 'move-right',
};

/**
 * 段码编辑弹框
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} codes - 编码
 * @reactProps {Object} editDetailData - 当前编辑行数据
 * @reactProps {Object} formVisible - 控制modal显示/隐藏属性
 * @reactProps {Object} handleVisible - 控制段值是否显示对象
 * @reactProps {Function} handleAdd - 数据保存
 * @reactProps {Function} showModal - 控制modal显示隐藏方法
 * @reactProps {Function} changeVisible - 修改段值输入框是否可见方法
 * @return React.element
 */
const AddForm = Form.create({ fieldNameProp: null })((props) => {
  const {
    form,
    formVisible,
    showModal,
    handleVisible,
    changeVisible,
    editDetailData = {},
    onHandleAddCodeDetail,
    codes,
    detailDatas = [],
    loading,
  } = props;
  const {
    orderSeq,
    fieldType,
    fieldValue,
    dateMask,
    resetFrequency,
    seqLength,
    startValue,
  } = editDetailData;
  const { FieldType, ResetFrequency, DateMask, Variable } = codes;
  const uuidDigit = [
    { value: 8, meaning: intl.get('hpfm.codeRule.model.codeRule.eight').d('8位') },
    { value: 16, meaning: intl.get('hpfm.codeRule.model.codeRule.sixteen').d('16位') },
    { value: 22, meaning: intl.get('hpfm.codeRule.model.codeRule.twentyTwo').d('22位') },
    { value: 32, meaning: intl.get('hpfm.codeRule.model.codeRule.thirtyTwo').d('32位') },
  ];
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      const arrDatas = detailDatas.filter((arrData) => arrData.orderSeq === fieldsValue.orderSeq);
      if (arrDatas.length > 0 && !editDetailData.orderSeq) {
        notification.error({
          message: intl.get('hpfm.codeRule.view.message.error').d('序号不能重复'),
        });
      } else {
        onHandleAddCodeDetail(fieldsValue, form, editDetailData);
      }
    });
  };
  const cancelHandle = () => {
    form.resetFields();
    showModal(false);
  };
  const changeFiledType = (value) => {
    changeVisible(value);
  };

  const formLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 },
  };
  const title = isEmpty(editDetailData)
    ? intl.get('hpfm.codeRule.view.title.createField').d('新建编码段')
    : intl.get('hpfm.codeRule.view.title.editField').d('编辑编码段');

  return (
    <Modal
      title={title}
      visible={formVisible}
      confirmLoading={loading}
      onOk={okHandle}
      width={500}
      onCancel={() => cancelHandle()}
    >
      <React.Fragment>
        <FormItem
          {...formLayout}
          label={intl.get('hpfm.codeRule.model.codeRule.orderSeq').d('序号')}
        >
          {form.getFieldDecorator('orderSeq', {
            initialValue: orderSeq,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.codeRule.view.codeRule.minOrderSeq').d('序号需大于1'),
                }),
              },
            ],
          })(<InputNumber min={1} style={{ width: '300px' }} />)}
        </FormItem>
        <FormItem
          {...formLayout}
          label={intl.get('hpfm.codeRule.model.codeRule.fieldTypeDescription').d('段类型')}
        >
          {form.getFieldDecorator('fieldType', {
            initialValue: fieldType,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.codeRule.model.codeRule.fieldTypeDescription').d('段类型'),
                }),
              },
            ],
          })(
            <Select
              style={{ width: '300px' }}
              onChange={(value) => {
                changeFiledType(value);
              }}
              disabled={!!fieldType}
            >
              {FieldType.map((code) => {
                return (
                  <Option key={code.value} value={code.value}>
                    {code.meaning}
                  </Option>
                );
              })}
            </Select>
          )}
        </FormItem>
        {handleVisible.fieldValue && (
          <FormItem
            {...formLayout}
            label={intl.get('hpfm.codeRule.model.codeRule.fieldValue').d('段值')}
          >
            {form.getFieldDecorator('fieldValue', {
              initialValue: fieldValue,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.codeRule.model.codeRule.fieldValue').d('段值'),
                  }),
                },
                {
                  max: 80,
                  message: intl.get('hzero.common.validation.max', {
                    max: 80,
                  }),
                },
              ],
            })(
              form.getFieldValue('fieldType') === 'CONSTANT' ? (
                <Input style={{ width: '300px' }} />
              ) : (
                <Select style={{ width: '300px' }}>
                  {Variable.map((code) => {
                    return (
                      <Option key={code.value} value={code.value}>
                        {code.meaning}
                      </Option>
                    );
                  })}
                </Select>
              )
            )}
          </FormItem>
        )}
        {handleVisible.dateMask && (
          <FormItem
            {...formLayout}
            label={intl.get('hpfm.codeRule.model.codeRule.dateMask').d('日期掩码')}
          >
            {form.getFieldDecorator('dateMask', {
              initialValue: dateMask,
              rules: [
                {
                  required: handleVisible.dateMask,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.codeRule.model.codeRule.dateMask').d('日期掩码'),
                  }),
                },
              ],
            })(
              <Select style={{ width: '300px' }}>
                {DateMask.map((code) => {
                  return (
                    <Option key={code.value} value={code.value}>
                      {code.meaning}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
        )}
        {handleVisible.resetFrequency && (
          <FormItem
            {...formLayout}
            label={intl.get('hpfm.codeRule.model.codeRule.resetFrequencyDescription').d('重置频率')}
          >
            {form.getFieldDecorator('resetFrequency', {
              initialValue: resetFrequency,
              rules: [
                {
                  required: handleVisible.resetFrequency,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hpfm.codeRule.model.codeRule.resetFrequencyDescription')
                      .d('重置频率'),
                  }),
                },
              ],
            })(
              <Select style={{ width: '300px' }}>
                {ResetFrequency.map((code) => {
                  return (
                    <Option key={code.value} value={code.value}>
                      {code.meaning}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
        )}
        {handleVisible.seqLength && (
          <FormItem
            {...formLayout}
            label={intl.get('hpfm.codeRule.model.codeRule.seqLength').d('位数')}
          >
            {form.getFieldDecorator('seqLength', {
              initialValue: seqLength,
              rules: [
                {
                  required: handleVisible.seqLength,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.codeRule.model.codeRule.seqLength').d('位数'),
                  }),
                },
              ],
            })(
              form.getFieldValue('fieldType') === 'UUID' ? (
                <Select style={{ width: '300px' }} allowClear>
                  {uuidDigit.map((item) => {
                    return (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    );
                  })}
                </Select>
              ) : (
                <InputNumber style={{ width: '300px' }} max={20} min={1} />
              )
            )}
          </FormItem>
        )}
        {(handleVisible.startValue || startValue === 0) && (
          <FormItem
            {...formLayout}
            label={intl.get('hpfm.codeRule.model.codeRule.startValue').d('开始值')}
          >
            {form.getFieldDecorator('startValue', {
              initialValue: startValue,
              rules: [
                {
                  required: handleVisible.startValue,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.codeRule.model.codeRule.startValue').d('开始值'),
                  }),
                },
              ],
            })(<InputNumber style={{ width: '300px' }} min={1} />)}
          </FormItem>
        )}
      </React.Fragment>
    </Modal>
  );
});

/**
 * 编码规则段码维护
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} codeRule - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ codeRuleOrg, loading }) => ({
  codeRuleOrg,
  organizationId: getCurrentOrganizationId(),
  saveCodeLoading: loading.effects['codeRuleOrg/saveCodeDetail'],
}))
@Form.create({ fieldNameProp: null })
export default class CodeRuleDetail extends PureComponent {
  /**
   *内部状态
   *
   */
  state = {
    selectedRows: [],
    fieldTypeList: [],
    handleVisible: {
      fieldValue: false,
      dateMask: false,
      resetFrequency: false,
      seqLength: false,
      startValue: false,
    },
    editDetailData: {},
    formVisible: false,
    showCompany: false,
  };

  /**
   *组件挂载后执行方法
   *
   */
  componentDidMount() {
    const { dispatch } = this.props;
    const lovCodes = {
      FieldType: 'HPFM.CODE_RULE.FIELD_TYPE',
      ResetFrequency: 'HPFM.CODE_RULE.RESET_FREQUENCY',
      DateMask: 'HPFM.CODE_RULE.DATE_MASK',
      UNITTYPE: 'HPFM.CODE_RULE.LEVEL_CODE',
      Variable: 'HPFM.CODE_RULE.VARIABLE',
    };
    dispatch({
      type: 'codeRuleOrg/init',
      payload: {
        lovCodes,
      },
    }).then((res) => {
      if (res) {
        this.setState({ fieldTypeList: res.FieldType });
      }
    });
    // dispatch({
    //   type: 'codeRuleOrg/fetchFieldType',
    //   payload: {
    //     lovCode: fieldTypeCode,
    //   },
    // }).then(res => {
    //   if (res) {
    //     this.setState({ fieldTypeList: res });
    //   }
    // });
    // dispatch({
    //   type: 'codeRuleOrg/fetchResetFrequency',
    //   payload: {
    //     lovCode: resetCode,
    //   },
    // });
    // dispatch({
    //   type: 'codeRuleOrg/fetchDateMask',
    //   payload: {
    //     lovCode: dateMask,
    //   },
    // });
  }

  /**
   * 生成表格头字段
   * @returns
   */
  @Bind()
  handlecolumns() {
    const {
      editRecordData: { usedFlag },
      match,
      isCurrentTenant,
    } = this.props;
    return [
      {
        title: intl.get('hpfm.codeRule.model.codeRule.orderSeq').d('序号'),
        dataIndex: 'orderSeq',
        editable: true,
        required: true,
        type: 'select',
        width: 80,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.fieldTypeDescription').d('段类型'),
        dataIndex: 'fieldTypeDescription',
        editable: true,
        required: true,
        type: 'select',
        width: 80,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.fieldValue').d('段值'),
        dataIndex: 'fieldValue',
        editable: true,
        required: true,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.dateMask').d('日期掩码'),
        dataIndex: 'dateMask',
        editable: true,
        required: true,
        width: 100,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.resetFrequencyDescription').d('重置频率'),
        dataIndex: 'resetFrequencyDescription',
        editable: true,
        required: true,
        width: 100,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.seqLength').d('位数'),
        dataIndex: 'seqLength',
        editable: true,
        required: true,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.startValue').d('开始值'),
        dataIndex: 'startValue',
        editable: true,
        required: true,
        width: 100,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.currentValue').d('当前值'),
        dataIndex: 'currentValue',
        editable: true,
        required: true,
        width: 100,
      },
      {
        title: intl.get('hpfm.codeRule.model.codeRule.resetData').d('上次重置日期'),
        dataIndex: 'resetData',
        editable: true,
        required: true,
        width: 200,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        render: (_, record) => {
          const operators = [];
          if (isCurrentTenant || usedFlag) {
            operators.push({
              key: 'edit',
              ele: (
                <div style={{ color: '#ccc' }}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </div>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          } else {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.DetailEdit`,
                      type: 'button',
                      meaning: '编码规则详情层-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.showModal(true, record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ];
  }

  /**
   * 删除数据
   */
  @Bind()
  removeData() {
    const { dispatch, organizationId, deleteLoading } = this.props;
    const { selectedRows } = this.state;
    const onOk = () => {
      dispatch({
        type: 'codeRuleOrg/removeCodeDetail',
        payload: {
          selectedRows,
          organizationId,
        },
      }).then((response) => {
        if (response) {
          this.refreshLine();
          notification.success();
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
   * 表格选择事件
   * @param {null} _ 占位符
   * @param {object} selectedRows 选中的行数据
   */
  @Bind()
  onSelectChange(_, selectedRows) {
    this.setState({ selectedRows });
  }

  /**
   * 改变选择框的显示内容
   * @param {object} value 行数据
   */
  @Bind()
  changeVisible(value = {}) {
    switch (value) {
      case 'SEQUENCE':
        this.setState({
          handleVisible: {
            fieldValue: false,
            dateMask: false,
            resetFrequency: true,
            seqLength: true,
            startValue: true,
          },
        });
        break;
      case 'DATE':
        this.setState({
          handleVisible: {
            fieldValue: false,
            dateMask: true,
            resetFrequency: false,
            seqLength: false,
            startValue: false,
          },
        });
        break;
      case 'CONSTANT':
        this.setState({
          handleVisible: {
            fieldValue: true,
            dateMask: false,
            resetFrequency: false,
            seqLength: false,
            startValue: false,
          },
        });
        break;
      case 'VARIABLE':
        this.setState({
          handleVisible: {
            fieldValue: true,
            dateMask: false,
            resetFrequency: false,
            seqLength: false,
            startValue: false,
          },
        });
        break;
      case 'UUID':
        this.setState({
          handleVisible: {
            fieldValue: false,
            dateMask: false,
            resetFrequency: false,
            seqLength: true,
            startValue: false,
          },
        });
        break;
      default:
        this.setState({
          handleVisible: {
            fieldValue: false,
            dateMask: false,
            resetFrequency: false,
            seqLength: false,
            startValue: false,
          },
        });
    }
  }

  /**
   * 刷新
   */
  @Bind()
  refreshLine() {
    const {
      dispatch,
      editRecordData,
      organizationId,
      codeRuleOrg: {
        detail: { data = {} },
      },
    } = this.props;
    const { pagination } = data;
    this.setState({
      selectedRows: [],
    });
    dispatch({
      type: 'codeRuleOrg/fetchDetail',
      payload: {
        ruleDistId: editRecordData.ruleDistId,
        organizationId,
        page: pagination,
      },
    });
  }

  /**
   * 数据保存
   * @param {object} fieldsValue 表单数据
   * @param {object} form 表单
   */
  @Bind()
  handleAddCodeDetail(fieldsValue, form, record = {}) {
    const {
      dispatch,
      organizationId,
      codeRuleOrg: { keyValue },
    } = this.props;
    const { _token, objectVersionNumber } = record;
    dispatch({
      type: 'codeRuleOrg/saveCodeDetail',
      payload: {
        _token,
        objectVersionNumber,
        ...fieldsValue,
        ...keyValue,
        ruleDetailId: this.state.editDetailData.ruleDetailId,
        organizationId,
      },
    }).then((response) => {
      if (response) {
        notification.success();
        this.showModal(false);
        form.resetFields();
        this.refreshLine();
      }
    });
  }

  /**
   *控制modal弹出层显隐
   *
   * @param {boolean} flag 显/隐标记
   * @param {object} record 行记录
   */
  @Bind()
  showModal(flag, record = {}) {
    const {
      form,
      codeRuleOrg: {
        detail: {
          data: { content = [] },
        },
      },
      dispatch,
    } = this.props;
    const { fieldTypeList } = this.state;
    const sequence = content.filter((con) => con.fieldType === 'SEQUENCE');
    if (sequence.length > 0) {
      dispatch({
        type: 'codeRuleOrg/changeFileType',
        payload: 'SEQUENCE',
      });
    } else {
      const fieldTypeCode = 'HPFM.CODE_RULE.FIELD_TYPE';
      dispatch({
        type: 'codeRuleOrg/fetchFieldType',
        payload: {
          lovCode: fieldTypeCode,
        },
      });
    }
    if (record) {
      if (record.fieldType === 'SEQUENCE') {
        dispatch({
          type: 'codeRuleOrg/resetFileType',
          payload: fieldTypeList,
        });
      }
      this.setState({
        formVisible: !!flag,
        editDetailData: record,
        handleVisible: {
          fieldValue: !!record.fieldValue,
          dateMask: !!record.dateMask,
          resetFrequency: !!record.resetFrequency,
          seqLength: !!record.seqLength,
          startValue: !!record.startValue || record.startValue === 0,
        },
      });
    } else {
      this.setState({
        formVisible: !!flag,
        editDetailData: {},
        handleVisible: {
          fieldValue: false,
          dateMask: false,
          resetFrequency: false,
          seqLength: false,
          startValue: false,
        },
      });
    }
    if (!flag) {
      form.resetFields();
    }
  }

  /**
   * 是否展示选择公司选择框
   * @param {object} value 判断数据
   */
  @Bind()
  showCompany(value = {}) {
    if (value === 'COM') {
      this.setState({
        showCompany: true,
      });
    } else {
      this.setState({
        showCompany: false,
      });
    }
  }

  /**
   * 渲染方法
   * @returns
   */
  render() {
    const {
      codeRuleOrg: { detail, code },
      visible,
      saveCodeLoading,
      handleEditDetail,
      editRecordData,
      isCurrentTenant,
      fetchLoading,
      match,
    } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { UNITTYPE } = code;
    const {
      levelCode,
      levelValue,
      levelValueDescription,
      description,
      enabledFlag,
      usedFlag,
    } = editRecordData;
    const { selectedRows, formVisible, handleVisible, editDetailData, showCompany } = this.state;
    const columns = this.handlecolumns();
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.ruleDetailId),
      onChange: this.onSelectChange,
    };
    const detailDatas = detail.data.content;

    const methods = {
      showModal: this.showModal,
      changeVisible: this.changeVisible,
      onHandleAddCodeDetail: this.handleAddCodeDetail,
    };
    const selecProps = usedFlag
      ? null
      : {
          rowSelection,
        };
    return (
      <Modal
        visible={visible}
        onCancel={() => handleEditDetail(false)}
        {...otherProps}
        width="80%"
        footer={
          isCurrentTenant
            ? null
            : [
                <Button key="cancel" onClick={() => handleEditDetail(false)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </Button>,
                <Button key="on" type="primary" onClick={() => handleEditDetail(false)}>
                  {intl.get('hzero.common.button.ok').d('确定')}
                </Button>,
              ]
        }
      >
        <React.Fragment>
          <Header title={intl.get('hpfm.codeRule.view.message.title.detail').d('编码段维护')}>
            {usedFlag === 1 && (
              <div>
                {intl
                  .get('hpfm.codeRule.view.message.info')
                  .d('提示：当前数据已经被使用，无法修改！')}
              </div>
            )}
          </Header>
          <Content>
            <Form>
              <Row type="flex" align="bottom" gutter={24}>
                <Col span={6}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.codeRule.model.codeRule.meaning').d('层级')}
                  >
                    {getFieldDecorator('levelCode', {
                      initialValue: levelCode,
                    })(
                      <Select onChange={this.showCompany} disabled>
                        {UNITTYPE.map((c) => {
                          return (
                            c.value !== 'GLOBAL' && (
                              <Option key={c.value} value={c.value}>
                                {c.meaning}
                              </Option>
                            )
                          );
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={6}>
                  {(showCompany || levelCode === 'COM') && (
                    <FormItem
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.codeRule.model.codeRule.levelValueDescription').d('值')}
                    >
                      {getFieldDecorator('levelValue', {
                        initialValue: levelValue,
                      })(
                        <Lov
                          disabled
                          textValue={levelValueDescription}
                          queryParams={{ lovCode: 'HPFM.COMPANY', enabledFlag: 1 }}
                          code="HPFM.CODE_RULE.COMPANY"
                        />
                      )}
                    </FormItem>
                  )}
                </Col>
                <Col span={6}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hzero.common.status.enable').d('启用')}
                  >
                    {getFieldDecorator('enabledFlag', {
                      initialValue: enabledFlag,
                    })(<Switch disabled />)}
                  </FormItem>
                </Col>
              </Row>
              <Row type="flex" align="bottom" gutter={24}>
                <Col span={6}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.codeRule.model.codeRule.description.dist').d('描述')}
                  >
                    {getFieldDecorator('description', {
                      initialValue: description,
                    })(<Input style={{ width: '500px' }} disabled />)}
                  </FormItem>
                </Col>
              </Row>
            </Form>
            <div className="table-operator">
              {!editRecordData.usedFlag && (
                <ButtonPermission
                  disabled={isCurrentTenant}
                  permissionList={[
                    {
                      code: `${match.path}.button.DetailCreate`,
                      type: 'button',
                      meaning: '编码规则详情层-新建',
                    },
                  ]}
                  onClick={() => {
                    this.showModal(true);
                  }}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </ButtonPermission>
              )}
              {selectedRows.length > 0 && (
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${match.path}.button.DetailDelete`,
                      type: 'button',
                      meaning: '编码规则详情层-删除',
                    },
                  ]}
                  onClick={this.removeData}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              )}
            </div>
            <Table
              loading={fetchLoading}
              rowKey="ruleDetailId"
              dataSource={detail.data.content}
              columns={columns}
              scroll={{ x: tableScrollWidth(columns) }}
              pagination={false}
              rowClassName="editable-row"
              bordered
              {...selecProps}
            />
          </Content>
          <AddForm
            {...methods}
            formVisible={formVisible}
            handleVisible={handleVisible}
            codes={code}
            editDetailData={editDetailData}
            detailDatas={detailDatas}
            loading={saveCodeLoading}
          />
        </React.Fragment>
      </Modal>
    );
  }
}
