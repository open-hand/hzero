import React from 'react';
import { Form, Row, Col, Spin } from 'hzero-ui';
import { isArray, inRange, join, map, isEmpty, isFunction, forEach, omit, template } from 'lodash';

import intl from 'utils/intl';
import request from 'utils/request';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';

import {
  getColLayout,
  getGetValuePropFunc,
  getGetValueFromEventFunc,
  getInitialValue,
  getComponentType,
  renderDisabledField,
  preProcessComponentProps,
  dealObjectProps,
  // postProcessComponentProps,
} from '../utils';
import { fieldLabelProp, fieldNameProp, composeFormOmitProps } from '../config';
import { postDynamicFormProcessComponentProps } from './utils';

async function fetchGet({ url, query, body }) {
  return request(url, {
    method: 'GET',
    query,
    body,
  });
}

async function fetchPost({ url, query, body }) {
  return request(url, {
    method: 'POST',
    query,
    body,
  });
}
async function fetchPut({ url, query, body }) {
  return request(url, {
    method: 'PUT',
    query,
    body,
  });
}

// const ComposeFormContext = React.createContext();
const { Item: FormItem } = Form;

const queryUrlInterpolate = /{([\s\S]+?)}/g;

@Form.create({ fieldNameProp: null })
export default class DynamicForm extends React.PureComponent {
  state = {
    ds: false, // 是否是从 queryUrl 加载数据
    dataSource: {}, // 保存初始属性, 提交时 数据为 初始值和表单值的merge
    queryLoading: false, // fetch data loading
    saveLoading: false, // save data loading
  };

  static defaultProps = {
    // 默认的属性
    col: 3,
    fields: [],
    editable: false,
    fieldLabelWidth: 150,
    context: undefined,
  };

  constructor(props) {
    super(props);
    this.query = this.query.bind(this);
    this.reset = this.reset.bind(this);
    this.submit = this.submit.bind(this);
    this.getFormData = this.getFormData.bind(this);

    this.getOrganizationParams = this.getOrganizationParams.bind(this); // 获取租户id url 路径参数
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const nextState = {};
    if (prevState.queryUrl !== nextProps.queryUrl) {
      if (nextProps.queryUrl) {
        nextState.ds = true;
        nextState.queryUrlTemplate = template(nextProps.queryUrl, {
          interpolate: queryUrlInterpolate,
        });
      } else {
        nextState.ds = false;
        nextState.queryUrlTemplate = undefined;
      }
      nextState.queryUrl = nextProps.queryUrl;
    }
    if (nextProps.dataSource) {
      if (nextProps.dataSource !== prevState.dataSource) {
        nextState.dataSource = nextProps.dataSource;
      }
    }
    return nextState;
  }

  componentDidMount() {
    const { onRef, form, dataSource = {} } = this.props;
    const { ds } = this.state;
    // 传递 this, 获取表单数据的方法, 校验并获取表单数据的方法 传递出去
    if (ds) {
      this.query();
    } else {
      form.setFieldsValue(dataSource);
    }
    if (isFunction(onRef)) {
      onRef(this);
    }
  }

  /**
   * 渲染所有表单行
   */
  renderRows() {
    const { col, fields, context, dataSource, form } = this.props;
    const contextFields = [];
    forEach(fields, field => {
      if (field.visiableFlag === 1) {
        const contextField = dealObjectProps(field, context);
        contextField.props = preProcessComponentProps({ field: contextField, context });
        contextFields.push(contextField);
      }
    });
    forEach(contextFields, field => {
      postDynamicFormProcessComponentProps({
        fields: contextFields,
        field,
        dealProps: field.props,
        dataSource,
        form,
      });
    });
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
    if (isArray(contextFields)) {
      for (; walkerIndex < contextFields.length; ) {
        const field = contextFields[walkerIndex];
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
    const { wrapperFieldComponent } = this.props;
    return (
      <Row type="flex" key={join(map(rowFields, field => field[fieldNameProp]), '-')}>
        {map(rowFields, field => {
          if (isFunction(wrapperFieldComponent)) {
            return wrapperFieldComponent(
              this.renderComposeFormField({
                field,
              }),
              field
            );
          }
          return this.renderComposeFormField({
            field,
          });
        })}
      </Row>
    );
  }

  /**
   * 渲染最终的字段
   * @param {Object} field - 字段
   */
  renderComposeFormField({ field }) {
    const { fieldLabelWidth, col, editable, form } = this.props;
    const { dataSource } = this.state;
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
        leftEmptyCols.push(<Col {...colProps} key={`${field[fieldNameProp]}#left-offset-${i}`} />);
      }
    }
    if (inRange(field.rightOffset, 1, col)) {
      for (let i = 0; i < field.rightOffset; i++) {
        rightEmptyCols.push(
          <Col {...colProps} key={`${field[fieldNameProp]}#right-offset-${i}`} />
        );
      }
    }
    const ComponentType = getComponentType(field);
    const otherFormItemOptions = {};
    // 表单字段 omit 属性
    const componentProps = omit(field.props, ['labelDisplayFlag']);
    const getValueFromEvent = getGetValueFromEventFunc(field.componentType);
    const getValueProps = getGetValuePropFunc(field);
    if (getValueFromEvent) {
      otherFormItemOptions.getValueFromEvent = getValueFromEvent;
    }
    if (getValueProps) {
      // 不影响存的值, 只影响传递给组件的值
      otherFormItemOptions.getValueProps = getValueProps;
    }
    const visible = field.visiableFlag !== 0;
    if (field.props.description) {
      formItemProps.extra = field.props.description;
    }
    const composeFormItem = editable ? (
      visible ? (
        <Col {...fieldColProps} key={field[fieldNameProp]}>
          <FormItem
            label={field.props.labelDisplayFlag ? field[fieldLabelProp] : ''}
            {...formItemProps}
            required={field.requiredFlag !== 0}
          >
            {form.getFieldDecorator(field[fieldNameProp], {
              ...otherFormItemOptions,
              initialValue: getInitialValue({ field, dataSource }),
              rules: [
                {
                  required: field.requiredFlag !== 0,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: field[fieldLabelProp],
                  }),
                },
              ],
            })(
              <ComponentType
                {...componentProps}
                disabled={field.props.disabled || field.enabledFlag !== 1}
              />
            )}
          </FormItem>
        </Col>
      ) : null
    ) : (
      <Col {...fieldColProps} key={field[fieldNameProp]}>
        <FormItem label={field[fieldLabelProp]} {...formItemProps}>
          {renderDisabledField({
            field,
            dataSource,
            formItemProps,
            componentProps: field.props,
          })}
        </FormItem>
      </Col>
    );
    if (isEmpty(leftEmptyCols) && isEmpty(rightEmptyCols)) {
      return composeFormItem;
    }
    return (
      <React.Fragment key={field[fieldNameProp]}>
        {leftEmptyCols}
        {composeFormItem}
        {rightEmptyCols}
      </React.Fragment>
    );
  }

  render() {
    const { editable = false, loading = false } = this.props;
    const { queryLoading, saveLoading } = this.state;
    const otherProps = omit(this.props, composeFormOmitProps);
    return (
      <Spin spinning={loading || queryLoading || saveLoading}>
        <Form
          layout="inline"
          className={editable ? 'compose-form' : 'compose-form-disabled'}
          {...otherProps}
        >
          {this.renderRows()}
        </Form>
      </Spin>
    );
  }

  getOrganizationParams() {
    const organizationId = getCurrentOrganizationId();
    return {
      organizationId,
    };
  }

  /**
   * 加载数据
   */
  query() {
    const { params } = this.props;
    const { ds, queryUrlTemplate } = this.state;
    if (ds) {
      this.setState({
        queryLoading: true,
      });
      fetchGet({
        url: queryUrlTemplate({ ...this.getOrganizationParams(), ...params }),
        query: params,
      })
        .then(res => {
          const dataSource = getResponse(res) || {};
          const { form } = this.props;
          this.setState({ dataSource });
          form.setFieldsValue(dataSource);
        })
        .then(() => {
          this.setState({
            queryLoading: false,
          });
        });
    }
  }

  /**
   * 重置表单
   */
  reset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 提交表单
   */
  submit() {
    const { form, rowKey } = this.props;
    return new Promise((resolve, reject) => {
      form.validateFields((err, data) => {
        if (!err) {
          const { submit, submitUrl } = this.props;
          const { dataSource = {} } = this.state;
          const submitData = { ...dataSource, ...data };
          if (submitUrl) {
            // 通过
            const dynamicService = submitData[rowKey] ? fetchPut : fetchPost;
            this.setState({
              saveLoading: true,
            });
            dynamicService({ url: submitUrl, body: submitData })
              .then(res => {
                const hasError = getResponse(res);
                if (hasError) {
                  resolve();
                } else {
                  reject(res);
                }
              })
              .then(() => {
                this.setState({
                  saveLoading: false,
                });
              });
          } else if (isFunction(submit)) {
            return submit(submitData);
          }
        }
      });
    });
  }

  getFormData() {
    return new Promise((resolve, reject) => {
      const { form } = this.props;
      form.validateFields((err, fieldsValue) => {
        const { dataSource = {} } = this.state;
        if (err) {
          reject(err);
        }
        resolve({ ...dataSource, ...fieldsValue });
      });
    });
  }
}

// 暴露出去的方法
DynamicForm.exportFuncs = ['submit', 'reset'];
DynamicForm.exportFuncsInfo = {
  submit: {
    descriptionIntlCode: 'hpfm.ui.tpl.form.submit',
    descriptionIntlDefault: intl.get('hzero.common.button.submit').d('提交'),
  },
  reset: {
    descriptionIntlCode: 'hpfm.ui.tpl.form.reset',
    descriptionIntlDefault: intl.get('hzero.common.button.reset').d('重置'),
  },
};
