/**
 * 带查询的表单 - 就是 动态表单 限制 col = 3,
 * @date 2018/10/24
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Form, Button, Row, Col, Icon } from 'hzero-ui';
import { map, forEach, isFunction, omit } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import {
  dealObjectProps,
  preProcessComponentProps,
  getComponentType,
  getGetValueFromEventFunc,
  getGetValuePropFunc,
} from '../utils';
import { postDynamicFormProcessComponentProps } from '../DynamicForm/utils';
import { searchFormOmitProps } from '../config';

const btnStyle = { marginRight: 8 };

@Form.create({ fieldNameProp: null })
export default class DynamicSearchForm extends React.Component {
  constructor(props) {
    super(props);

    // state
    this.state = {
      advanceFormActive: false,
    };
  }

  /**
   * 渲染查询表单
   * 根据 fields.length 的不同 返回不同的表单
   * <= 3 普通表单
   * <= 5 不同表单 字段固定比例宽
   * > 5 高级表单
   */
  render() {
    const { fields = [], searchBtnText, resetBtnText, className } = this.props;
    const otherFormProps = omit(this.props, searchFormOmitProps);
    const buttons = (
      <>
        <Button key="search" onClick={this.handleSearchBtnClick} type="primary" style={btnStyle}>
          {searchBtnText || intl.get('hzero.common.button.search').d('查询')}
        </Button>
        <Button key="reset" onClick={this.handleResetBtnClick} style={btnStyle}>
          {resetBtnText || intl.get('hzero.common.button.reset').d('重置')}
        </Button>
      </>
    );
    const formClassName = `${className || ''} table-list-form`;
    if (fields.length <= 3) {
      return (
        <Form layout="inline" className={formClassName} {...otherFormProps}>
          {this.renderFormItems()}
          <Form.Item key="btn">{buttons}</Form.Item>
        </Form>
      );
    }
    return (
      <>
        <Form className={formClassName} {...otherFormProps}>
          <Row>
            <Col span={18}>
              <Row>{this.renderFormItems()}</Row>
            </Col>
            <Col span={6} className="search-btn-more">
              <Form.Item>
                {buttons}
                {this.renderAdvanceBtn()}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </>
    );
  }

  componentDidMount() {
    const { onRef } = this.props;
    if (isFunction(onRef)) {
      onRef(this);
    }
  }

  /**
   * 渲染表单字段
   */
  renderFormItems() {
    const { fields, context, form } = this.props;
    const contextFields = map(fields, field => {
      const contextField = dealObjectProps(field, context);
      contextField.props = preProcessComponentProps({ field: contextField, context });
      return contextField;
    });
    forEach(contextFields, field => {
      postDynamicFormProcessComponentProps({
        fields: contextFields,
        field,
        dealProps: field.props,
        form,
      });
    });
    return map(contextFields, this.renderField);
  }

  /**
   * 根据条件渲染字段
   * 当字段大于3 且 当前表单是高级表单的激活状态时 返回 Col 前三个字段, 否则返回全部字段
   * 当字段小于3时 返回 FormItem
   * @param {object} field - 当前字段
   * @param {number} index - 当前字段是所有字段的第index个字段
   */
  renderField(field, index) {
    const { fields, form } = this.props;
    const { advanceFormActive } = this.state;
    const ComponentType = getComponentType(field);

    const otherFormItemOptions = {};
    const getValueFromEvent = getGetValueFromEventFunc(field.componentType);
    const getValueProps = getGetValuePropFunc(field);
    if (getValueFromEvent) {
      otherFormItemOptions.getValueFromEvent = getValueFromEvent;
    }
    if (getValueProps) {
      // 不影响存的值, 只影响传递给组件的值
      otherFormItemOptions.getValueProps = getValueProps;
    }
    if (fields.length <= 3) {
      return (
        <Form.Item label={field.fieldLabel} required={field.requiredFlag !== 0}>
          {form.getFieldDecorator(field.fieldName, {
            ...otherFormItemOptions,
            rules: [
              {
                required: field.required,
                message: intl
                  .get('hzero.common.validation.notNull', {
                    name: field.fieldLabel,
                  })
                  .d(`${field.fieldLabel}不能为空`),
              },
            ],
          })(React.createElement(ComponentType, field.props))}
        </Form.Item>
      );
    }
    if (fields.length > 5 && !advanceFormActive && index >= 3) {
      return null;
    }
    return (
      <Col span={8}>
        <Form.Item
          labelCol={{ span: 10 }}
          wrapperCol={{ span: 14 }}
          label={field.fieldLabel}
          required={field.requiredFlag !== 0}
        >
          {form.getFieldDecorator(field.fieldName, {
            ...otherFormItemOptions,
            rules: [
              {
                required: field.required,
                message: intl
                  .get('hzero.common.validation.notNull', {
                    name: field.fieldLabel,
                  })
                  .d(`${field.fieldLabel}不能为空`),
              },
            ],
          })(React.createElement(ComponentType, field.props))}
        </Form.Item>
      </Col>
    );
  }

  /**
   * 按条件来渲染高级表单按钮
   * this.props.fields.length
   * <=5 不渲染 return null
   * >5 且 this.state.advanceFormActive 为真 渲染收起按钮 返回 收起按钮
   * >5 且 this.state.advanceFormActive 为假 渲染展开按钮 返回 展开按钮
   */
  renderAdvanceBtn() {
    const { fields } = this.props;
    const { advanceFormActive } = this.state;
    if (fields.length <= 5) {
      return null;
    }
    return advanceFormActive ? (
      <a onClick={this.handleCloseAdvanceBtn} className="unselect">
        {intl.get('hzero.common.button.up').d('收起')}
        <Icon type="up" />
      </a>
    ) : (
      <a onClick={this.handleExpandAdvanceBtn} className="unselect">
        {intl.get('hzero.common.button.expand').d('展开')}
        <Icon type="down" />
      </a>
    );
  }

  /**
   * 关闭高级表单
   */
  @Bind()
  handleCloseAdvanceBtn() {
    this.setState({
      advanceFormActive: false,
    });
  }

  /**
   * 展开高级表单
   */
  @Bind()
  handleExpandAdvanceBtn() {
    this.setState({
      advanceFormActive: true,
    });
  }

  /**
   * 查询按钮点击
   */
  @Bind()
  handleSearchBtnClick() {
    const { form, submitTrigger } = this.props;
    return new Promise((resolve, reject) => {
      if (isFunction(submitTrigger)) {
        form.validateFields((err, fieldsValue) => {
          if (err) {
            reject(err);
            return;
          }
          submitTrigger(fieldsValue).then(resolve, reject);
        });
      }
    });
  }

  /**
   * 重置按钮点击
   */
  @Bind()
  handleResetBtnClick() {
    const { form, onReset } = this.props;
    form.resetFields();
    if (isFunction(onReset)) {
      onReset();
    }
  }
}

const exportFuncs = ['submitTrigger'];
const exportFuncsInfo = {
  submitTrigger: {
    descriptionIntlCode: 'hpfm.ui.tpl.form.submit',
    descriptionIntlDefault: intl.get('hzero.common.button.submit').d('提交'),
  },
};
// const internalFuncs = ['lineRemove'];
// const internalFuncsInfo = {
//   lineRemove: {
//     descriptionIntlCode: 'hpfm.ui.tpl.table.internal.lineRemove',
//     descriptionIntlDefault: '删除行',
//     code: 'lineRemove',
//   },
// };

// 暴露出去的方法
DynamicSearchForm.exportFuncs = exportFuncs;
DynamicSearchForm.exportFuncsInfo = exportFuncsInfo;

// // 内部方法
// DynamicSearchForm.internalFuncs = internalFuncs;
// DynamicSearchForm.internalFuncsInfo = internalFuncsInfo;
