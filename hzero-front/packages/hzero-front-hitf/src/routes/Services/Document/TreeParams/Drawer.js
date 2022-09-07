/**
 * ParamsDrawer - 创建/编辑树形参数弹窗
 * @date: 2019/5/20
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Input, Modal, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import Switch from 'components/Switch';
import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;
const { Option } = Select;

/**
 * 创建/编辑树形参数弹窗
 * @extends {Component} - React.PureComponent
 * @reactProps {boolean} visible - 参数弹窗是否可见
 * @reactProps {string} actionType - HTTP操作类型(REQ/RESP)
 * @reactProps {string} mimeType - BODY的MIME类型
 * @reactProps {array} paramValueType - BODY参数值的类型值集
 * @reactProps {boolean} confirmLoading - 新建/编辑保存加载标志
 * @reactProps {obejct} currentParamData - 当前参数数据
 * @reactProps {string} paramType - 参数类型(HEADER/GET/PATH/BODY)
 * @reactProps {Function} onSave - 保存参数
 * @reactProps {Function} onCancel - 关闭弹窗
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class ParamsDrawer extends PureComponent {
  /**
   * 关闭侧滑
   */
  @Bind()
  handleClose() {
    const { onCancel } = this.props;
    onCancel();
  }

  /**
   * 保存URL参数信息侧滑
   */
  @Bind()
  handleOk() {
    const {
      form,
      actionType,
      paramType,
      onSave,
      mimeType,
      currentParamData,
      currentAction,
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        let totalValues = { requiredFlag: 0 };
        if (mimeType) {
          totalValues.mimeType = mimeType;
        }
        // // 新建
        if (currentAction !== 'edit') {
          // 校验通过，进行保存操作
          totalValues = {
            parentId: currentParamData ? currentParamData.paramId : null,
            paramType,
            actionType,
            ...totalValues,
            ...values,
          };
          onSave({ values: totalValues, flag: 'create' });
          // 编辑
        } else {
          const { objectVersionNumber, _token, paramId, parentId } = currentParamData;
          totalValues = {
            objectVersionNumber,
            paramId,
            parentId,
            paramType,
            actionType,
            _token,
            ...totalValues,
            ...values,
          };
          onSave({ values: totalValues, flag: 'edit' });
        }
      }
    });
  }

  /**
   * 渲染标题
   */
  @Bind()
  renderTitle() {
    const { currentAction } = this.props;
    let title;
    switch (currentAction) {
      case 'create':
        title = intl.get('hitf.document.view.title.create.rootParams').d('新建根节点参数');
        break;
      case 'createRecord':
        title = intl.get('hitf.document.view.title.create.childParams').d('新建子节点参数');
        break;
      case 'edit':
        title = intl.get('hitf.document.view.title.edit.params').d('编辑参数');
        break;
      default:
        break;
    }
    return title;
  }

  /**
   * 渲染类型初始值
   */
  @Bind()
  renderTypeInitialValue() {
    const { currentParamData, isHaveRoot, mimeType, currentAction } = this.props;
    const isEdit = currentAction === 'edit';
    let initialType;
    if (isEdit) {
      initialType = currentParamData.paramValueType;
    } else if (!isHaveRoot && mimeType === 'text/xml') {
      initialType = 'OBJECT';
    }
    return initialType;
  }

  /**
   * 渲染类型显示组件
   */
  @Bind()
  renderComponent() {
    const {
      paramValueTypes = [],
      isHaveRoot,
      mimeType,
      currentParamData,
      currentAction,
    } = this.props;
    const isEdit = currentAction === 'edit';
    const pathArr = (currentParamData && currentParamData.levelPath.split('/')) || [];
    let types = ['STRING', 'ARRAY', 'OBJECT', 'NUMBER', 'BOOLEAN']; // 默认参数类型
    if (mimeType === 'text/xml') {
      // 如果没有根节点或者是编辑状态下的根节点
      if (!isHaveRoot || (pathArr.length === 2 && isEdit)) {
        types = ['OBJECT'];
        // 如果有根节点，同时当前选择的是根节点，同时是编辑状态并且有子节点
      } else if (
        isHaveRoot &&
        pathArr.length !== 2 &&
        isEdit &&
        currentParamData &&
        currentParamData.children
      ) {
        types = ['OBJECT', 'ARRAY'];
      }
    } else if (
      mimeType === 'application/json' &&
      currentParamData &&
      currentParamData.children &&
      isEdit
    ) {
      types = ['OBJECT', 'ARRAY'];
    }
    const typeArr = paramValueTypes.filter((item) => types.includes(item.value));
    return (
      <Select allowClear>
        {typeArr.length &&
          typeArr.map(({ value, meaning }) => (
            <Option key={value} value={value}>
              {meaning}
            </Option>
          ))}
      </Select>
    );
  }

  render() {
    const {
      form,
      visible,
      confirmLoading,
      currentParamData,
      currentAction,
      isHaveRoot,
      mimeType,
    } = this.props;
    const { getFieldDecorator } = form;
    const isEdit = currentAction === 'edit';
    return (
      <Modal
        destroyOnClose
        title={this.renderTitle()}
        visible={visible}
        onCancel={this.handleClose}
        onOk={this.handleOk}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={confirmLoading}
      >
        <Form>
          <FormItem
            label={intl.get('hitf.services.model.services.paramName').d('参数名')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('paramName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hitf.services.model.services.paramName').d('参数名'),
                  }),
                },
                {
                  max: 128,
                  message: intl.get('hzero.common.validation.max', {
                    max: 128,
                  }),
                },
              ],
              initialValue: isEdit ? currentParamData.paramName : undefined,
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hitf.services.model.services.paramValueType').d('类型')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('paramValueType', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hitf.services.model.services.paramValueType').d('类型'),
                  }),
                },
              ],
              initialValue: this.renderTypeInitialValue(),
            })(this.renderComponent())}
          </FormItem>
          <FormItem
            label={intl.get('hitf.services.model.services.requiredFlag').d('是否必填')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('requiredFlag', {
              initialValue: isEdit ? currentParamData.requiredFlag : 1,
            })(<Switch disabled={!isHaveRoot && mimeType === 'text/xml'} />)}
          </FormItem>
          <FormItem
            label={intl.get('hitf.services.model.services.formatRegexp').d('格式限制')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('formatRegexp', {
              initialValue: isEdit ? currentParamData.formatRegexp : undefined,
              rules: [
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hitf.services.model.services.remark').d('说明')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('remark', {
              initialValue: isEdit ? currentParamData.remark : undefined,
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hitf.services.model.services.demo').d('示例')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('valueDemo', {
              initialValue: isEdit ? currentParamData.valueDemo : undefined,
            })(<Input />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
