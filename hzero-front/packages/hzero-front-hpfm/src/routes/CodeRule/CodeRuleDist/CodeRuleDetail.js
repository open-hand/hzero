/**
 * CodeRuleDetail - 编码规则详情层
 * @date: 2018-6-29
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Form, Input, InputNumber, Modal, Row, Select, Table, Badge, Col } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import Checkbox from 'components/Checkbox';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
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
    encryptedFlag,
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
        onHandleAddCodeDetail(fieldsValue, form);
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

  return (
    <Modal
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
              {FieldType.map((code) => (
                <Option key={code.value} value={code.value}>
                  {code.meaning}
                </Option>
              ))}
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
                  {Variable.map((code) => (
                    <Option key={code.value} value={code.value}>
                      {code.meaning}
                    </Option>
                  ))}
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
                {DateMask.map((code) => (
                  <Option key={code.value} value={code.value}>
                    {code.meaning}
                  </Option>
                ))}
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
                {ResetFrequency.map((code) => (
                  <Option key={code.value} value={code.value}>
                    {code.meaning}
                  </Option>
                ))}
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
                  {uuidDigit.map((item) => (
                    <Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
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
        {(handleVisible.encryptedFlag || form.getFieldValue('fieldType') === 'SEQUENCE') && (
          <FormItem
            {...formLayout}
            label={intl.get('hpfm.codeRule.model.codeRule.encryptedFlag').d('是否加密')}
          >
            {form.getFieldDecorator('encryptedFlag', {
              initialValue: encryptedFlag,
            })(<Checkbox checkedValue />)}
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
@connect(({ codeRule, loading }) => ({
  codeRule,
  saveCodeLoading: loading.effects['codeRule/saveCodeDetail'],
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
      encryptedFlag: false,
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
      type: 'codeRule/init',
      payload: {
        lovCodes,
      },
    }).then((res) => {
      if (res) {
        this.setState({ fieldTypeList: res.FieldType });
      }
    });
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
        title: intl.get('hpfm.codeRule.model.codeRule.encryptedFlag').d('是否加密'),
        dataIndex: 'encryptedFlag',
        editable: true,
        width: 100,
        render: (_, record) => {
          if (record.fieldType === 'SEQUENCE') {
            return (
              <Badge
                status={record.encryptedFlag === 1 ? 'success' : 'error'}
                text={
                  record.encryptedFlag === 1
                    ? intl.get('hpfm.codeRule.model.codeRule.encrypted').d('加密')
                    : intl.get('hpfm.codeRule.model.codeRule.noEncrypted').d('不加密')
                }
              />
            );
          }
        },
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
        fixed: 'right',
        render: (_, record) => {
          const operators = [];
          if (usedFlag) {
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
    const {
      dispatch,
      codeRule: { organizationId },
      deleteLoading,
    } = this.props;
    const { selectedRows } = this.state;
    const onOk = () => {
      dispatch({
        type: 'codeRule/removeCodeDetail',
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
   * @param {null} _占位符
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
            encryptedFlag: true,
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
            encryptedFlag: false,
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
            encryptedFlag: false,
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
            encryptedFlag: false,
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
            encryptedFlag: false,
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
            encryptedFlag: false,
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
      codeRule: {
        organizationId,
        detail: { data = {} },
      },
    } = this.props;
    const { pagination } = data;
    this.setState({
      selectedRows: [],
    });
    dispatch({
      type: 'codeRule/fetchDetail',
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
  handleAddCodeDetail(fieldsValue, form) {
    const {
      dispatch,
      codeRule: { organizationId, keyValue },
    } = this.props;
    const { editDetailData } = this.state;
    const { _token, objectVersionNumber, ruleDetailId } = editDetailData;
    dispatch({
      type: 'codeRule/saveCodeDetail',
      payload: {
        objectVersionNumber,
        _token,
        ...fieldsValue,
        ...keyValue,
        ruleDetailId,
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
      codeRule: {
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
        type: 'codeRule/changeFileType',
        payload: 'SEQUENCE',
      });
    } else {
      const fieldTypeCode = 'HPFM.CODE_RULE.FIELD_TYPE';
      dispatch({
        type: 'codeRule/fetchFieldType',
        payload: {
          lovCode: fieldTypeCode,
        },
      });
    }
    if (record) {
      if (record.fieldType === 'SEQUENCE') {
        dispatch({
          type: 'codeRule/resetFileType',
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
      codeRule: { detail, code },
      match,
      visible,
      saveCodeLoading,
      handleEditDetail,
      editRecordData,
      fetchLoading,
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
        footer={[
          <Button key="cancel" onClick={() => handleEditDetail(false)}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button key="on" type="primary" onClick={() => handleEditDetail(false)}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
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
                          queryParams={{ enabledFlag: 1, lovCode: 'HPFM.COMPANY' }}
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
                    })(<Input style={{ width: 500 }} disabled />)}
                  </FormItem>
                </Col>
              </Row>
            </Form>
            <div className="table-operator">
              {!editRecordData.usedFlag && (
                <ButtonPermission
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
              <ButtonPermission
                disabled={selectedRows.length <= 0}
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
            </div>
            <Table
              bordered
              pagination={false}
              rowKey="ruleDetailId"
              rowClassName="editable-row"
              loading={fetchLoading}
              dataSource={detail.data.content}
              columns={columns}
              scroll={{ x: tableScrollWidth(columns) }}
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
