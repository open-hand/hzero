import React from 'react';
import { Form, Row, Col, Spin } from 'hzero-ui';
import { isArray, inRange, join, map, isEmpty, isFunction, toInteger, findIndex } from 'lodash';

import intl from 'utils/intl';
import { EMAIL } from 'utils/regExp';

import {
  getColLayout,
  getGetValuePropFunc,
  getGetValueFromEventFunc,
  getInitialValue,
  getComponentType,
  renderDisabledField,
  getComponentProps,
} from './utils';
import { getDisplayValue } from '../utils';

const ComposeFormContext = React.createContext();
const { Item: FormItem } = Form;

@Form.create({ fieldNameProp: null })
export default class ComposeForm extends React.PureComponent {
  state = {
    // 存之前的 props 做比较用
    col: 3,
    fields: [],
    organizationId: undefined,
    editable: false,
    disableStyle: 'value',
    fieldLabelWidth: 150,
    context: undefined,
    // 渲染好的元素
    rows: [],
    init: false,
  };

  static defaultProps = {
    // 默认的属性
    col: 3,
    fields: [],
    dataSource: {},
    organizationId: undefined,
    editable: false,
    disableStyle: 'value',
    fieldLabelWidth: 150,
    context: undefined,
  };

  constructor(props) {
    super(props);
    this.checkUpdate = this.checkUpdate.bind(this);
    this.getDataSource = this.getDataSource.bind(this);
    this.getValidateDataSource = this.getValidateDataSource.bind(this);
    const { col, fields, organizationId, editable, disableStyle, context, fieldLabelWidth } = props;
    this.state = {
      col,
      fields,
      organizationId,
      editable,
      disableStyle,
      context,
      fieldLabelWidth,
      rows: this.renderRows(),
      init: true,
    };
  }

  componentDidMount() {
    const { onRef, onGetValidateDataSourceHook, onGetDataSourceHook } = this.props;
    // 传递 this, 获取表单数据的方法, 校验并获取表单数据的方法 传递出去
    if (isFunction(onRef)) {
      onRef(this);
    }
    if (isFunction(onGetValidateDataSourceHook)) {
      onGetValidateDataSourceHook(this.getValidateDataSource);
    }
    if (isFunction(onGetDataSourceHook)) {
      onGetDataSourceHook(this.getDataSource);
    }
  }

  /**
   * 获取表单数据
   * @returns
   * @memberof ComposeForm
   */
  getDataSource() {
    const { form, dataSource } = this.props;
    return {
      ...dataSource,
      ...form.getFieldsValue(),
    };
  }

  /**
   * 校验并获取表单数据
   * @returns
   * @memberof ComposeForm
   */
  getValidateDataSource() {
    const { form, dataSource, tabTitle, parentTabTitle } = this.props;
    return new Promise((resolve, reject) => {
      form.validateFields((err, fieldsValue) => {
        if (err) {
          reject({ err, tabTitle, parentTabTitle }); // eslint-disable-line
        } else {
          resolve({
            ...dataSource,
            ...fieldsValue,
          });
        }
      });
    });
  }

  /**
   * 检查属性变动 并调用对应的方法
   */
  checkUpdate() {
    const {
      col,
      fields,
      organizationId,
      editable,
      disableStyle,
      context,
      fieldLabelWidth,
      form,
    } = this.props;
    const {
      col: prevCol,
      fields: prevFields,
      organizationId: prevOrganizationId,
      editable: prevEditable,
      disableStyle: prevDisplayStyle,
      context: prevContext,
      fieldLabelWidth: prevFieldLabelWidth,
      init,
    } = this.state;
    if (init) {
      if (
        col !== prevCol ||
        fields !== prevFields ||
        organizationId !== prevOrganizationId ||
        editable !== prevEditable ||
        (editable === false && disableStyle !== prevDisplayStyle) ||
        context !== prevContext ||
        fieldLabelWidth !== prevFieldLabelWidth
      ) {
        // 之后是不是可以根据不同的属性的影响, 来决定影响不同的属性
        // 需要重新渲染 rows
        this.setState({
          col,
          fields,
          organizationId,
          editable,
          disableStyle,
          context,
          fieldLabelWidth,
          rows: this.renderRows(),
        });
        // 如果配置或者 dataSource 改变了需要重置表单
        form.resetFields();
      }
    }
  }

  /**
   * 渲染所有表单行
   */
  renderRows() {
    const { col, fields } = this.props;
    // 存放 生成的 Row
    const rows = [];
    // 所有的 字段的数组
    // 当前遍历的字段的下标
    let walkerIndex = 0;
    // 当前遍历的 Row 的 fields
    let rowFields = [];
    // 已经遍历的 Row 的 fields 的宽度和
    let rowCol = 0;
    // 当前遍历的 Row 的 field 的下标
    let rowIndex = 0;
    if (isArray(fields)) {
      for (; walkerIndex < fields.length; ) {
        const field = fields[walkerIndex];
        rowFields.push(field);
        if (inRange(field.colspan, 2, col + 1)) {
          rowCol += field.colspan;
        } else {
          rowCol += 1;
        }
        if (inRange(field.leftOffset, 1, col)) {
          rowCol += field.leftOffset;
        }
        if (inRange(field.rightOffset, 1, col)) {
          rowCol += field.rightOffset;
        }
        if (rowCol >= col) {
          if (rowCol > col && rowIndex > 0) {
            // 已经超过一列的宽度了,并且字段多于1个 需要 回退
            walkerIndex--;
            rowFields.pop();
          }
          // 生成 Row 并放入 rows
          rows.push(this.renderRow({ rowFields }));
          // 重置 遍历的 Row 的状态
          rowIndex = 0;
          rowCol = 0;
          rowFields = [];
        } else {
          // 继续向前遍历
          rowIndex++;
        }
        walkerIndex++;
      }
      if (rowIndex > 0) {
        rows.push(this.renderRow({ rowFields }));
      }
    }
    return rows;
  }

  /**
   * 渲染表单行
   * @param {Object[]} rowFields - 一行对应的字段
   */
  renderRow({ rowFields }) {
    const { disableStyle } = this.props;
    return (
      <Row
        type="flex"
        key={join(map(rowFields, field => field.fieldCode), '-')}
        className={disableStyle === 'value' ? 'row-disabled' : ''}
      >
        {map(rowFields, field =>
          this.renderComposeFormField({
            field,
            disableStyle,
          })
        )}
      </Row>
    );
  }

  /**
   * 渲染最终的字段
   * @param {Object} field - 字段
   */
  renderComposeFormField({ field }) {
    const { disableStyle, fieldLabelWidth, col, editable, organizationId, context } = this.props;
    const formItemProps = {
      labelCol: {
        style: { width: fieldLabelWidth, minWidth: fieldLabelWidth, maxWidth: fieldLabelWidth },
      },
      wrapperCol: { style: { flex: 'auto' } },
    };
    const colProps = getColLayout(col);
    const fieldColProps = getColLayout(col, field.colspan);
    const leftEmptyCols = [];
    const rightEmptyCols = [];
    if (inRange(field.leftOffset, 1, col)) {
      for (let i = 0; i < field.leftOffset; i++) {
        leftEmptyCols.push(<Col {...colProps} key={`${field.fieldCode}#left-offset-${i}`} />);
      }
    }
    if (inRange(field.rightOffset, 1, col)) {
      for (let i = 0; i < field.rightOffset; i++) {
        rightEmptyCols.push(<Col {...colProps} key={`${field.fieldCode}#right-offset-${i}`} />);
      }
    }
    const ComponentType = getComponentType(field);
    const componentProps = getComponentProps({
      field,
      componentType: field.componentType,
      context,
    });

    const otherFormItemOptions = {};
    let isViewOnly = false; // 附件是否为只读
    const getValueFromEvent = getGetValueFromEventFunc(field.componentType);
    const getValueProps = getGetValuePropFunc(field);
    if (field.componentType === 'Upload') {
      otherFormItemOptions.valuePropName = 'attachmentUUID';
      if (field.props) {
        const propsIndex = findIndex(field.props, ['attributeName', 'viewOnly']);
        if (propsIndex >= 0) {
          isViewOnly = field.props[propsIndex].attributeValue;
        }
      }
    }
    if (getValueFromEvent) {
      otherFormItemOptions.getValueFromEvent = getValueFromEvent;
    }
    if (getValueProps) {
      // 不影响存的值, 只影响传递给组件的值
      otherFormItemOptions.getValueProps = getValueProps;
    }
    const composeFormItem = (
      <Col {...fieldColProps} key={field.fieldCode}>
        <ComposeFormContext.Consumer>
          {({ form, dataSource }) => {
            const otherComponentProps = {}; // 为 lov 和 valueList 准备的属性
            switch (field.componentType) {
              case 'Lov':
              case 'ValueList':
                otherComponentProps.textValue = getDisplayValue(field, dataSource);
                break;
              default:
                break;
            }
            return editable ? (
              <FormItem
                label={field.fieldDescription}
                {...formItemProps}
                required={
                  field.componentType !== 'Checkbox' &&
                  toInteger(field.requiredFlag) !== 0 &&
                  !isViewOnly
                } // 当附件只读时，不必输
              >
                {(field.fieldCode === 'mail'
                  ? form.getFieldDecorator(`mail`, {
                      ...otherFormItemOptions,
                      initialValue: getInitialValue({ field, dataSource }),
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hzero.common.email').d('邮箱'),
                          }),
                        },
                        {
                          pattern: EMAIL,
                          message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                        },
                        {
                          max: 60,
                          message: intl.get('hzero.common.validation.max', {
                            max: 60,
                          }),
                        },
                      ],
                    })
                  : form.getFieldDecorator(field.fieldCode, {
                      ...otherFormItemOptions,
                      initialValue: getInitialValue({ field, dataSource }),
                      rules: [
                        {
                          required: toInteger(field.requiredFlag) !== 0 && !isViewOnly,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: field.fieldDescription,
                          }),
                        },
                      ],
                    }))(
                  React.createElement(
                    ComponentType,
                    { ...componentProps, ...otherComponentProps } // otherComponentProps 比 componentProps 优先级高
                  )
                )}
              </FormItem>
            ) : (
              <FormItem label={field.fieldDescription} {...formItemProps}>
                {renderDisabledField({
                  field,
                  dataSource,
                  formItemProps,
                  organizationId,
                  disableStyle,
                  componentProps: { ...componentProps, ...otherComponentProps },
                })}
              </FormItem>
            );
          }}
        </ComposeFormContext.Consumer>
      </Col>
    );
    if (isEmpty(leftEmptyCols) && isEmpty(rightEmptyCols)) {
      return composeFormItem;
    }
    return (
      <React.Fragment key={field.fieldCode}>
        {leftEmptyCols}
        {composeFormItem}
        {rightEmptyCols}
      </React.Fragment>
    );
  }

  render() {
    const { editable = false, form, dataSource, loading = false } = this.props;
    const { rows = [] } = this.state;
    this.checkUpdate();
    return (
      <Spin spinning={loading}>
        <Form layout="inline" className={editable ? 'compose-form' : 'compose-form-disabled'}>
          <ComposeFormContext.Provider value={{ form, dataSource }}>
            {rows}
          </ComposeFormContext.Provider>
        </Form>
      </Spin>
    );
  }
}
