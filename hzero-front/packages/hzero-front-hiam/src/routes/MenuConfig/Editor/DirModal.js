/**
 * 菜单管理 - 上级目录
 * TODO: 没有给宽度 会被撑开
 */
import React, { PureComponent } from 'react';
import { Modal, Table, Form, Button, Input, Row, Col } from 'hzero-ui';
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';

import { FORM_COL_3_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;
const FormContext = React.createContext();

const viewMessagePrompt = 'hiam.menuConfig.view.message';
const modelPrompt = 'hiam.menuConfig.model.menuConfig';
const commonPrompt = 'hzero.common';

const QueryForm = ({ form, ...props }) => (
  <FormContext.Provider value={form}>
    <Form {...props} />
  </FormContext.Provider>
);

const WrapperQueryForm = Form.create({ fieldNameProp: null })(QueryForm);
const formItemLayout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 14 },
};
export default class DirModal extends PureComponent {
  state = {
    selectedRow: this.props.defaultSelectedRow || {},
  };

  onSelect(selectedRow) {
    this.setState({
      selectedRow,
    });
  }

  onTableChange;

  handleRowClick(record) {
    this.setState({
      selectedRow: record,
    });
  }

  ok() {
    const { onOk = (e) => e, onCancel = (e) => e, defaultSelectedRow = {} } = this.props;
    const { selectedRow } = this.state;
    this.setState({
      selectedRow: {},
    });
    onOk(isEmpty(selectedRow) ? defaultSelectedRow : selectedRow);
    onCancel();
  }

  cancel() {
    const { onCancel = (e) => e } = this.props;
    onCancel();
  }

  defaultRowkey = 'id';

  getWrapperForm() {
    const { handleSearch = (e) => e } = this.props;
    return (
      <WrapperQueryForm layout="inline" style={{ marginBottom: 15 }}>
        <FormContext.Consumer>
          {(form) => {
            const { getFieldDecorator = (e) => e, resetFields = (e) => e, getFieldsValue } = form;
            const search = () => {
              handleSearch({ page: 0, size: 10, ...getFieldsValue() });
            };
            this.onTableChange = (pagination = {}) => {
              const { current = 1, pageSize = 10 } = pagination;
              handleSearch({ page: current - 1, size: pageSize, ...getFieldsValue() });
            };
            return (
              <>
                <Row type="flex" gutter={24} align="bottom">
                  <Col {...FORM_COL_3_LAYOUT}>
                    <FormItem
                      {...formItemLayout}
                      label={intl.get(`${modelPrompt}.dirCode`).d('目录编码')}
                    >
                      {getFieldDecorator('code')(<Input />)}
                    </FormItem>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <FormItem
                      {...formItemLayout}
                      label={intl.get(`${modelPrompt}.dirName`).d('目录名称')}
                    >
                      {getFieldDecorator('name')(<Input />)}
                    </FormItem>
                  </Col>
                  <Col span={3}>
                    <FormItem>
                      <Button onClick={() => resetFields()}>
                        {intl.get(`${commonPrompt}.button.reset`).d('重置')}
                      </Button>
                    </FormItem>
                  </Col>
                  <Col spam={3}>
                    <FormItem>
                      <Button type="primary" htmlType="submit" onClick={search}>
                        {intl.get(`${commonPrompt}.button.search`).d('查询')}
                      </Button>
                    </FormItem>
                  </Col>
                </Row>
              </>
            );
          }}
        </FormContext.Consumer>
      </WrapperQueryForm>
    );
  }

  render() {
    const {
      title,
      visible,
      onCancel,
      onOk,
      dataSource,
      pagination,
      loading,
      defaultSelectedRow,
      ...others
    } = this.props;
    const { selectedRow = {} } = this.state;
    const columns = [
      {
        title: intl.get(`${modelPrompt}.dirCode`).d('目录编码'),
        dataIndex: 'code',
      },
      {
        title: intl.get(`${modelPrompt}.dirName`).d('目录名称'),
        dataIndex: 'name',
        width: 200,
      },
      {
        title: intl.get(`${modelPrompt}.type`).d('类别'),
        dataIndex: 'typeMeaning',
        width: 120,
      },
    ];
    const tableProps = {
      columns,
      scroll: { x: tableScrollWidth(columns) },
      rowKey: this.defaultRowkey,
      rowSelection: {
        type: 'radio',
        selectedRowKeys: [!isEmpty(selectedRow.id) ? selectedRow.id : defaultSelectedRow.id],
        onSelect: this.onSelect.bind(this),
      },
      pagination,
      dataSource,
      onChange: this.onTableChange,
      loading,
      bordered: true,
      childrenColumnName: 'subMenus',
      onRow: (record, index) => ({
        onDoubleClick: () => this.ok(record, index),
        onClick: () => this.handleRowClick(record, index),
      }),
    };

    return (
      <Modal
        title={intl.get(`${viewMessagePrompt}.title.selectParentDir`).d('选择上级目录')}
        visible={visible}
        onOk={this.ok.bind(this)}
        onCancel={this.cancel.bind(this)}
        destroyOnClose
        width={700}
        {...others}
      >
        {this.getWrapperForm.bind(this)()}
        <Table {...tableProps} />
      </Modal>
    );
  }
}
