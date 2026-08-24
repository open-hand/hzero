import React, { PureComponent } from 'react';
import { Modal, Table, Form, Button, Input, Row, Col } from 'hzero-ui';
import { pullAllBy } from 'lodash';
import intl from 'utils/intl';
import { FORM_COL_4_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

const viewMessagePrompt = 'hiam.menuConfig.view.message';
const modelPrompt = 'hiam.menuConfig.model.menuConfig';
const commonPrompt = 'hzero.common';

@Form.create({ fieldNameProp: null })
export default class Organization extends PureComponent {
  constructor(props) {
    super(props);
    // 方法注册
    [
      'onCell',
      'onSelect',
      'onSelectAll',
      'onTableChange',
      'onTableChange',
      'ok',
      'cancel',
      'search',
      'reset',
    ].forEach(method => {
      this[method] = this[method].bind(this);
    });
  }

  state = {
    selectedRows: this.props.selectedRows,
  };

  onSelect(record, selected) {
    const { selectedRows = [] } = this.state;
    this.setState({
      selectedRows: selected
        ? selectedRows.concat(record)
        : selectedRows.filter(n => n.id !== record.id),
    });
  }

  onSelectAll(selected, newSelectedRows, changeRows) {
    const { selectedRows = [] } = this.state;
    this.setState({
      selectedRows: selected
        ? selectedRows.concat(changeRows)
        : pullAllBy([...selectedRows], changeRows, 'id'),
    });
  }

  ok() {
    const { onOk = e => e } = this.props;
    const { selectedRows } = this.state;
    this.setSelectedRows();
    onOk(selectedRows);
  }

  cancel() {
    const { onCancel = e => e } = this.props;
    this.setSelectedRows();
    this.reset();
    onCancel();
  }

  onTableChange(pagination = {}) {
    const {
      handleFetchData = e => e,
      form: { getFieldsValue = e => e },
    } = this.props;
    const { current = 1, pageSize = 10 } = pagination;
    handleFetchData({ page: current - 1, size: pageSize, ...getFieldsValue() });
  }

  search() {
    const {
      handleFetchData = e => e,
      pagination = {},
      form: { getFieldsValue = e => e },
    } = this.props;
    const { pageSize = 10 } = pagination;
    handleFetchData({ page: 0, size: pageSize, ...getFieldsValue() });
  }

  reset() {
    const {
      form: { resetFields = e => e },
    } = this.props;
    resetFields();
  }

  setSelectedRows(selectedRows = []) {
    this.setState({
      selectedRows,
    });
  }

  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 180,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: e => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  render() {
    const {
      title,
      onCancel,
      onOk,
      dataSource,
      loading,
      pagination,
      visible,
      form: { getFieldDecorator = e => e },
      ...others
    } = this.props;
    const { selectedRows = [] } = this.state;

    const tableProps = {
      columns: [
        {
          title: intl.get(`${modelPrompt}.permissionCode`).d('权限编码'),
          dataIndex: 'code',
          width: 150,
          onCell: this.onCell,
        },
        {
          title: intl.get(`${modelPrompt}.description`).d('描述'),
          dataIndex: 'description',
          width: 180,
          onCell: this.onCell,
        },
      ],
      rowKey: 'id',
      rowSelection: {
        selectedRowKeys: selectedRows.map(n => n.id),
        onSelect: this.onSelect,
        onSelectAll: this.onSelectAll,
      },
      pagination,
      dataSource,
      loading,
      bordered: true,
      onChange: this.onTableChange,
    };

    return (
      <Modal
        title={title || intl.get(`${viewMessagePrompt}.title.selectPermissions`).d('选择权限')}
        visible={visible}
        onOk={this.ok}
        onCancel={this.cancel}
        width={650}
        destroyOnClose
        {...others}
      >
        <Row type="flex" gutter={24} align="bottom">
          <Form>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem label={intl.get(`${modelPrompt}.permissionCode`).d('权限编码')}>
                {getFieldDecorator('code')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem label={intl.get(`${modelPrompt}.description`).d('描述')}>
                {getFieldDecorator('description')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem>
                <Button onClick={this.reset}>
                  {intl.get(`${commonPrompt}.button.reset`).d('重置')}
                </Button>
              </FormItem>
              <FormItem>
                <Button type="primary" htmlType="submit" onClick={this.search}>
                  {intl.get(`${commonPrompt}.button.search`).d('查询')}
                </Button>
              </FormItem>
            </Col>
          </Form>
        </Row>
        <Table {...tableProps} />
      </Modal>
    );
  }
}
