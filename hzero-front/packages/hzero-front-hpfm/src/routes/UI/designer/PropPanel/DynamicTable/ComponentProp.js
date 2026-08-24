/**
 * ComponentProp.js
 * @author WY
 * @date 2018-10-03
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Input, InputNumber, Divider, Button, Checkbox, Select } from 'hzero-ui';
import { forEach, isFunction } from 'lodash';

import intl from 'utils/intl';
import ValueList from 'components/ValueList';

import HiddenColumnsEditModal from './HiddenColumnsEditModal';

import { attributeNameProp, attributeValueProp } from '../../config';
import { getQueryFormOptions, templatesIsNoEqual } from '../../utils';

const inputComponentStyle = {
  width: '100%',
};

@Form.create({
  fieldNameProp: null,
  onValuesChange(props, changedValues, allValues) {
    if (isFunction(props.onValuesChange)) {
      props.onValuesChange(props, changedValues, allValues);
    }
  },
})
class ComponentProp extends React.Component {
  state = {
    hiddenColumnModalProps: {},
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const { templates } = nextProps;
    const { templates: prevTemplates } = prevState;
    if (templatesIsNoEqual(prevTemplates, templates)) {
      return {
        queryFormOptions: getQueryFormOptions(templates),
        templates,
      };
    }
    return null;
  }

  constructor(props) {
    super(props);
    this.handleUpdateHiddenColumn = this.handleUpdateHiddenColumn.bind(this);
    this.handleCloseHiddenColumnModal = this.handleCloseHiddenColumnModal.bind(this);
    this.handleOpenHiddenColumnModal = this.handleOpenHiddenColumnModal.bind(this);
  }

  render() {
    const { component } = this.props;
    const { hiddenColumnModalProps = {} } = this.state;
    const propValues = {};
    forEach(component.config, prop => {
      propValues[prop[attributeNameProp]] = prop[attributeValueProp];
    });
    return (
      <Form>
        {this.renderComponentCommonProps(propValues)}
        <Divider />
        {this.renderComponentDynamicTableProps(propValues)}
        <Form.Item>
          <Button
            style={{ width: '100%' }}
            type="primary"
            onClick={this.handleOpenHiddenColumnModal}
          >
            定义隐藏域
          </Button>
        </Form.Item>
        {hiddenColumnModalProps.visible && (
          <HiddenColumnsEditModal
            {...hiddenColumnModalProps}
            key={component.templateCode}
            onOk={this.handleUpdateHiddenColumn}
            onCancel={this.handleCloseHiddenColumnModal}
          />
        )}
      </Form>
    );
  }

  renderComponentCommonProps() {
    const { form, component } = this.props;
    return (
      <React.Fragment>
        <Form.Item label="编码">
          {form.getFieldDecorator('templateCode', {
            initialValue: component.templateCode,
          })(<Input disabled />)}
        </Form.Item>
        <Form.Item label="名称">
          {form.getFieldDecorator('description', {
            initialValue: component.description,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: '名称',
                }),
              },
            ],
          })(<Input />)}
        </Form.Item>
      </React.Fragment>
    );
  }

  renderComponentDynamicTableProps(propValues) {
    const { form } = this.props;
    const { queryFormOptions } = this.state;
    return (
      <React.Fragment>
        <Form.Item key="pagination" className="ant-form-item-checkbox-cascade-parent">
          {form.getFieldDecorator('pagination', {
            initialValue: propValues.pagination,
          })(
            <Checkbox checkedValue unCheckedValue={false}>
              分页
            </Checkbox>
          )}
        </Form.Item>
        <Form.Item
          key="defaultPageSize"
          label="分页大小"
          className="ant-form-item-checkbox-cascade-child"
        >
          {form.getFieldDecorator('defaultPageSize', {
            initialValue: propValues.defaultPageSize,
          })(
            <ValueList
              options={[
                { value: 10, meaning: '10' },
                { value: 20, meaning: '20' },
                { value: 50, meaning: '50' },
                { value: 100, meaning: '100' },
              ]}
            />
          )}
        </Form.Item>
        <Form.Item key="rowKey" label="数据主键">
          {form.getFieldDecorator('rowKey', {
            initialValue: propValues.rowKey,
            rules: [
              {
                required: true,
                message: intl
                  .get('hzero.common.validation.notNull', {
                    name: '数据主键',
                  })
                  .d(),
              },
            ],
          })(React.createElement(Input))}
        </Form.Item>
        <Form.Item key="queryForm" label="查询表单">
          {form.getFieldDecorator('queryForm', {
            initialValue: propValues.queryForm,
          })(<Select>{queryFormOptions}</Select>)}
        </Form.Item>
        <Form.Item key="queryUrl" label="加载数据URL">
          {form.getFieldDecorator('queryUrl', {
            initialValue: propValues.queryUrl,
          })(<Input />)}
        </Form.Item>
        <Form.Item key="removeUrl" label="删除数据URL">
          {form.getFieldDecorator('removeUrl', {
            initialValue: propValues.removeUrl,
          })(React.createElement(Input))}
        </Form.Item>
        <Form.Item key="batchRemoveUrl" label="批量删除数据URL">
          {form.getFieldDecorator('batchRemoveUrl', {
            initialValue: propValues.batchRemoveUrl,
          })(<Input />)}
        </Form.Item>
        <Form.Item key="styleWidth" label="宽度">
          {form.getFieldDecorator('style.width', {
            initialValue: propValues['style.width'],
          })(<InputNumber style={inputComponentStyle} />)}
        </Form.Item>
      </React.Fragment>
    );
  }

  /**
   * open hiddenColumn editModal
   */
  handleOpenHiddenColumnModal() {
    const { component } = this.props;
    this.setState({
      hiddenColumnModalProps: {
        visible: true,
        hiddenColumns: component.hiddenColumns,
      },
    });
  }

  handleCloseHiddenColumnModal() {
    this.setState({
      hiddenColumnModalProps: {
        visible: false,
      },
    });
  }

  handleUpdateHiddenColumn(hiddenColumns) {
    const { component } = this.props;
    component.hiddenColumns = hiddenColumns;
    this.setState({
      hiddenColumnModalProps: {
        visible: false,
      },
    });
  }
}

if (process.env.NODE_ENV === 'production') {
  ComponentProp.displayName = 'DynamicTable(ComponentProp)';
}

export default ComponentProp;
