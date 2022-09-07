import React, { Component } from 'react';
import { Button, Drawer, Form, Input, Select, Switch } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isNil } from 'lodash';

import Lov from 'components/Lov';

import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { CODE } from 'utils/regExp';

import styles from './index.less';
// 使用 FormItem 组件
const FormItem = Form.Item;
// 使用 Option 组件
const { Option } = Select;
// 使用 TextArea 组件
const { TextArea } = Input;
// hpfm 国际化前缀

/**
 * SlideDrawer - 财务代码设置 编辑侧滑弹窗组件
 * @extends {Component} - React.Component
 * @reactProps {function} [ref= (e => e)] - react ref属性
 * @reactProps {boolean} [updateLoading=false] - 编辑保存状态
 * @reactProps {boolean} visible - 弹窗是否可见
 * @reactProps {Array<object>} typeList - 状态下拉框值集
 * @reactProps {object} editRecord - 选择编辑的行数据
 * @reactProps {function} [onOk = (e => e)] - 弹窗确定时执行
 * @reactProps {function} [onClose = (e => e)] - 弹窗关闭时执行
 * @reactProps {object} form - 表单对象
 */
@Form.create({ fieldNameProp: null })
export default class SlideDrawer extends Component {
  constructor(props) {
    super(props);
    const { onRef } = props;
    if (onRef) {
      onRef(this);
    }
    this.state = {
      valueMeaning: null,
      currentTag: null,
      typeList: [],
    };
  }

  /**
   * 挂载完成时执行
   */
  componentDidMount() {
    this.initTypeList();
  }

  /**
   * 更新完成时执行
   */
  componentDidUpdate(prevProps) {
    const { visible } = this.props;
    if (visible && !prevProps.visible) {
      this.initTypeList();
    }
  }

  /**
   * initTypeList - 挂载完成时获取类型值集
   */
  @Bind()
  initTypeList() {
    const { typeList } = this.props;
    this.setState({
      typeList,
    });
  }

  /**
   * handleSelectChange - 下拉组件改变时触发
   * @param {Array} value - 类型值集的 orderSeq 字段组成的数组
   */
  @Bind()
  handleSelectChange(value = []) {
    const orderSeq = value[0];
    const {
      typeList = [],
      form: { setFieldsValue = e => e },
    } = this.props;
    const currentTag = (typeList.find(o => Number(o.orderSeq) === Number(orderSeq)) || {}).tag;
    const valueMeaning = (typeList.find(o => o.value === currentTag) || {}).meaning;
    const group = (typeList.find(o => o.orderSeq === orderSeq) || {}).parentValue;
    this.setState({
      valueMeaning,
      currentTag,
      typeList: group
        ? typeList.filter(n => n.parentValue === group && n.tag === currentTag)
        : value.length === 0
        ? typeList
        : typeList.filter(o => isNil(o.parentValue)),
    });
    setFieldsValue({ codeId: undefined });
  }

  render() {
    const { valueMeaning, currentTag, typeList = [] } = this.state;
    const {
      visible,
      onOk,
      onClose,
      form: { getFieldDecorator },
      saveLoading,
    } = this.props;
    return (
      <Drawer
        title={intl.get('hpfm.financialCode.view.message.createTitle').d('新建财务代码')}
        visible={visible}
        onClose={onClose}
        width={520}
        destroyOnClose
      >
        <div className={styles['financial-code-drawer']}>
          <Form layout="vertical">
            <FormItem
              label={intl.get('hpfm.financialCode.model.financialCode.financialCode').d('代码')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('code', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.financialCode.model.financialCode.financialCode')
                        .d('代码'),
                    }),
                  },
                  {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input trim inputChinese={false} />)}
            </FormItem>
            <FormItem
              label={intl.get('hpfm.financialCode.model.financialCode.financialName').d('名称')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('name', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.financialCode.model.financialCode.financialName')
                        .d('名称'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
            <FormItem
              label={intl.get('hpfm.financialCode.model.financialCode.financialType').d('类型')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('types', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.financialCode.model.financialCode.financialType')
                        .d('类型'),
                    }),
                  },
                ],
              })(
                <Select mode="multiple" onChange={this.handleSelectChange}>
                  {typeList.map(n => (
                    <Option key={n.orderSeq} value={n.orderSeq}>
                      {n.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            <FormItem
              label={intl
                .get('hpfm.financialCode.model.financialCode.superiorType', {
                  type: valueMeaning || '',
                })
                .d(`上级${valueMeaning || ''}`)}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('codeId', {
                rules: [].concat(
                  !isEmpty(currentTag)
                    ? {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.financialCode.model.financialCode.superiorType', {
                              type: valueMeaning || '',
                            })
                            .d(`上级${valueMeaning || ''}`),
                        }),
                      }
                    : {}
                ),
              })(
                <Lov
                  code="HPFM.FINANCE_CODE"
                  queryParams={{ tenantId: getCurrentOrganizationId(), type: currentTag }}
                />
              )}
            </FormItem>
            <FormItem
              label={intl.get('hpfm.financialCode.model.financialCode.description').d('描述')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('remark')(<TextArea rows={3} />)}
            </FormItem>
            <FormItem
              label={intl.get('hzero.common.status.enable').d('启用')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('enabledFlag', {
                valuePropName: 'checked',
                initialValue: true,
              })(<Switch />)}
            </FormItem>
            <div className="drawer-bottom">
              <Button onClick={onClose}>{intl.get(`hzero.common.button.cancel`).d('取消')}</Button>
              <Button
                style={{ marginLeft: '8px' }}
                type="primary"
                onClick={onOk}
                loading={saveLoading}
              >
                {intl.get(`hzero.common.button.save`).d('保存')}
              </Button>
            </div>
          </Form>
        </div>
      </Drawer>
    );
  }
}
