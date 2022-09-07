/**
 * ActualValueEditModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-28
 * @copyright 2019-05-28 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Button, Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import EditTable from 'components/EditTable';

import intl from 'utils/intl';
import { getEditTableData, tableScrollWidth } from 'utils/utils';
import { TABLE_OPERATOR_CLASSNAME } from 'utils/constants';

function computeActualValueDataSourceFromRecord(record) {
  return (record && record.actualValue ? record.actualValue : []).map(actualValue => ({
    actualValue,
    id: uuid(),
    _status: 'update',
  }));
}

// /**
//  * 判断 actualValue 是否已编辑
//  * @param {NLPBasicData} record
//  * @param {string[]} dataSource
//  */
// function actualValueHasEdit(record, dataSource = []) {
//   const {actualValue = []} = record || {};
//   if (actualValue.length !== dataSource.length) {
//     return false;
//   } else {
//     return (actualValue.some((av, index) => {
//       return dataSource[index].actualValue !== av;
//     }))
//   }
// }

const rowKey = 'id';

@Form.create({ fieldNameProp: null })
export default class ActualValueEditModal extends Component {
  static propTypes = {
    languageMessage: PropTypes.object.isRequired,
  };

  constructor(props) {
    super(props);
    const { record } = props;
    this.state = {
      prevRecord: record, // 存储前一次的record 用来记录是否需要更新 dataSource
      dataSource: computeActualValueDataSourceFromRecord(record),
    };
  }

  /**
   * 如果 record 变化
   * @param nextProps
   * @param prevState
   * @returns {{prevRecord: *, dataSource: (*|Array)}|null}
   */
  static getDerivedStateFromProps(nextProps, prevState) {
    const { record, visible } = nextProps;
    const { prevRecord } = prevState;
    if (visible && record !== prevRecord) {
      return {
        prevRecord: record,
        dataSource: computeActualValueDataSourceFromRecord(record),
      };
    }
    return null;
  }

  // #region Modal
  @Bind()
  handleModalOk() {
    const { onOk } = this.props;
    const { dataSource = [] } = this.state;
    if (dataSource.length === 0) {
      onOk({ actualValue: [] });
    } else {
      const actualValue = getEditTableData(dataSource);
      if (actualValue.length !== dataSource.length) {
        // 数据校验失败, 虽然没有数据限制
      } else {
        onOk({ actualValue: actualValue.map(r => r.actualValue) });
      }
    }
  }

  @Bind()
  handleModalCancel() {
    const { onCancel } = this.props;
    // const {onCancel, record, languageMessage} = this.props;
    // const {dataSource = []} = this.state;
    // // TODO: dataSource 需要从表单中取值
    // if (actualValueHasEdit(record, dataSource)) {
    //   Modal.confirm({
    //     content: languageMessage.common.message.noSaveDataSubmit,
    //     onOk: () => {
    //       onCancel()
    //     }
    //   })
    // } else {
    onCancel();
    // }
  }

  // #endregion
  // #region Button
  delBtnDisabled() {
    const { selectedRows = [] } = this.state;
    return selectedRows.length === 0;
  }

  @Bind()
  handleAddBtnClick(e) {
    e.preventDefault();
    const { dataSource = [] } = this.state;
    this.setState({
      dataSource: [...dataSource, { id: uuid(), _status: 'create' }],
    });
  }

  @Bind()
  handleDeleteBtnClick(e) {
    e.preventDefault();
    const { selectedRows = [], dataSource = [] } = this.state;
    this.setState({
      selectedRowKeys: [],
      selectedRows: [],
      dataSource: dataSource.filter(record => !selectedRows.includes(record)),
    });
  }

  // #endregion

  // #region Table
  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      selectedRows,
      selectedRowKeys: selectedRows.map(record => record[rowKey]),
    });
  }

  getColumns() {
    const { languageMessage } = this.props;
    return [
      {
        dataIndex: 'actualValue',
        title: languageMessage.model.basicData.actualValue,
        width: 200,
        render: (actualValue, record) => {
          if (['create', 'update'].includes(record._status)) {
            const { $form: form } = record;
            return (
              <Form.Item>
                {form.getFieldDecorator('actualValue', {
                  initialValue: actualValue,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: languageMessage.model.basicData.actualValue,
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            );
          }
          return actualValue;
        },
      },
    ];
  }

  // #endregion

  render() {
    const { visible = false, languageMessage, updateLoading = false } = this.props;
    const { dataSource = [], selectedRowKeys = [] } = this.state;
    const columns = this.getColumns();
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
    };
    return (
      <Modal
        destroyOnClose
        width={480}
        visible={visible}
        title={languageMessage.view.title.actualValueEdit}
        onText={languageMessage.common.btn.submit}
        onOk={this.handleModalOk}
        onCancel={this.handleModalCancel}
        confirmLoading={updateLoading}
        cancelButtonProps={{ disabled: updateLoading }}
        // okButtonProps={{disabled: queryDetailLoading}}
      >
        <div className={TABLE_OPERATOR_CLASSNAME}>
          <Button type="primary" onClick={this.handleAddBtnClick}>
            {languageMessage.common.btn.add}
          </Button>
          <Button disabled={this.delBtnDisabled()} onClick={this.handleDeleteBtnClick}>
            {languageMessage.common.btn.del}
          </Button>
        </div>
        <EditTable
          bordered
          rowKey={rowKey}
          rowSelection={rowSelection}
          pagination={false}
          dataSource={dataSource}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
        />
      </Modal>
    );
  }
}
