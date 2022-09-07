import React, { PureComponent } from 'react';
import { Button, Form, Input, Modal, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { createPagination, tableScrollWidth } from 'utils/utils';

const FormItem = Form.Item;
const FormContext = React.createContext();

const QueryForm = ({ form, ...props }) => (
  <FormContext.Provider value={form}>
    <Form {...props} />
  </FormContext.Provider>
);

const WrapperQueryForm = Form.create({ fieldNameProp: null })(QueryForm);

export default class Organization extends PureComponent {
  state = {
    selectedRow: {},
    getFieldsValue: (e) => e,
  };

  onSelect(selectedRow) {
    this.setState({
      selectedRow,
    });
  }

  @Bind()
  handleRowClick(record) {
    this.onSelect(record);
  }

  @Bind()
  handleRowDoubleClick(record) {
    this.onSelect(record);
    this.ok();
  }

  ok() {
    const { onOk = (e) => e, onCancel = (e) => e } = this.props;
    const { selectedRow } = this.state;
    onOk(selectedRow);
    onCancel();
    this.setState({
      selectedRow: {},
    });
  }

  cancel() {
    const { onCancel = (e) => e } = this.props;
    onCancel();
  }

  getWrapperForm() {
    const { onSearch = (e) => e, filterItems = [] } = this.props;
    return (
      filterItems.length > 0 && (
        <WrapperQueryForm layout="inline">
          <FormContext.Consumer>
            {(form) => {
              const { getFieldDecorator = (e) => e, resetFields = (e) => e, getFieldsValue } = form;
              this.setState({ getFieldsValue });
              const search = () => {
                onSearch(getFieldsValue());
              };
              return (
                <div className="table-list-search">
                  {filterItems.map((item) => (
                    <FormItem label={item.label} key={item.code}>
                      {getFieldDecorator(item.code)(<Input />)}
                    </FormItem>
                  ))}
                  <FormItem>
                    <Button onClick={() => resetFields()}>
                      {intl.get(`hzero.common.button.reset`).d('重置')}
                    </Button>
                  </FormItem>
                  <FormItem>
                    <Button type="primary" htmlType="submit" onClick={search}>
                      {intl.get(`hzero.common.button.search`).d('查询')}
                    </Button>
                  </FormItem>
                </div>
              );
            }}
          </FormContext.Consumer>
        </WrapperQueryForm>
      )
    );
  }

  /**
   * handlePagination - 分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handlePagination(pagination = {}) {
    const { onSearch = (e) => e } = this.props;
    const { getFieldsValue } = this.state;
    onSearch({ page: pagination, ...getFieldsValue() });
  }

  render() {
    const {
      title,
      visible,
      onCancel,
      onOk,
      dataSource,
      loading,
      columns,
      rowKey,
      ...others
    } = this.props;
    const { selectedRow = {} } = this.state;
    const tableProps = {
      columns,
      scroll: { x: tableScrollWidth(columns) },
      rowKey,
      loading,
      rowSelection: {
        type: 'radio',
        selectedRowKeys: [selectedRow[rowKey]],
        onSelect: this.onSelect.bind(this),
      },
      dataSource: dataSource.content,
      pagination: createPagination(dataSource),
      onChange: this.handlePagination,
      bordered: true,
      onRow: (record, index) => ({
        onDoubleClick: () => this.handleRowDoubleClick(record, index),
        onClick: () => this.handleRowClick(record, index),
      }),
    };

    return (
      <Modal
        title={intl.get('hadm.sourceLov.view.message.selectOne').d('选择一列数据')}
        visible={visible}
        onOk={this.ok.bind(this)}
        onCancel={this.cancel.bind(this)}
        destroyOnClose
        width={680}
        {...others}
      >
        {this.getWrapperForm.bind(this)()}
        <Table {...tableProps} />
      </Modal>
    );
  }
}
