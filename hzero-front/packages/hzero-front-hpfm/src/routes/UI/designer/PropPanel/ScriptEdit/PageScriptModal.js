/**
 * PageScriptModal.js
 * @date 2018/11/6
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import { Modal, Input, Button } from 'hzero-ui';
import { map, omit, filter } from 'lodash';
import { Bind } from 'lodash-decorators';

import { getEditTableData } from 'utils/utils';

import EditTable from 'components/EditTable';

import ScriptEdit from './index';
import styles from '../../index.less';

const omitProps = ['dataSource'];

const modalBodyStyle = {
  height: 400,
  overflow: 'auto',
};

const tableScroll = {
  y: 340,
};

export default class PageScriptModal extends React.Component {
  static getDerivedStateFromProps(nextProps, prevState) {
    const {
      diffState: { dataSource: diffDataSource },
    } = prevState;
    const { dataSource } = nextProps;
    if (diffDataSource !== dataSource) {
      return {
        dataSource: map(dataSource, r => ({ ...r, _status: 'update' })), // 已经把所有数据都clone了一份(没有引用数据)
        diffState: {
          dataSource,
        },
      };
    }
    return null;
  }

  state = {
    diffState: {},
  };

  render() {
    const otherProps = omit(this.props, omitProps);
    const { dataSource, scriptEditProps, editRecord } = this.state;
    return (
      <Modal
        {...otherProps}
        title="页面方法"
        onOk={this.handleSave}
        afterClose={this.handleCleanState}
        width={1000}
        bodyStyle={modalBodyStyle}
      >
        <div className={styles['page-func-modal']}>
          <div className={styles['page-func-modal-table']}>
            <div className={styles['page-func-modal-table-operator']}>
              <Button icon="plus" onClick={this.handleScriptAdd} />
            </div>
            <EditTable
              bordered
              columns={this.columns}
              dataSource={dataSource}
              rowKey={this.orderRowKey}
              pagination={false}
              rowClassName={this.getRowClassName}
              scroll={tableScroll}
            />
          </div>
          <div className={styles['page-func-modal-script-modal']}>
            {editRecord && <ScriptEdit {...scriptEditProps} onChange={this.handleScriptChange} />}
          </div>
        </div>
      </Modal>
    );
  }

  columns = [
    {
      title: '编码',
      dataIndex: 'name',
      width: 120,
      render(item, record) {
        const { _status, $form } = record;
        switch (_status) {
          case 'create':
            return $form.getFieldDecorator('name', {
              initialValue: item,
              rules: [{ required: true, message: '编码不能为空' }],
            })(<Input />);
          case 'update':
          default:
            return item;
        }
      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      width: 200,
      render(item, record) {
        const { _status, $form } = record;
        switch (_status) {
          case 'create':
          case 'update':
            return $form.getFieldDecorator('description', {
              initialValue: item,
              rules: [{ required: true, message: '描述不能为空' }],
            })(<Input />);
          default:
            return item;
        }
      },
    },
    {
      title: '操作',
      key: 'content',
      width: 80,
      render: (item, record) => {
        const { _status } = record;
        switch (_status) {
          case 'create':
          case 'update':
            return (
              <span className={styles['table-row-operator']}>
                <a onClick={this.handleScriptEdit.bind(this, record)}>编辑</a>
                <a onClick={this.handleScriptRemove.bind(this, record)}>删除</a>
              </span>
            );
          default:
            return (
              <span className={styles['table-row-operator']}>
                <a className={styles['tag-a-disabled']}>编辑</a>
                <a>删除</a>
              </span>
            );
        }
      },
    },
  ];

  @Bind()
  handleSave() {
    const { onOk } = this.props;
    const { dataSource = [] } = this.state;
    const dLen = dataSource.length; // 所有 script 的数量
    const saveDataSource = getEditTableData(dataSource, []);
    if (saveDataSource.length !== dLen) {
      // 校验出现问题
      return;
    }
    onOk(saveDataSource);
  }

  /**
   * 在 Modal 关闭后 清理 state 数据
   */
  @Bind()
  handleCleanState() {
    this.setState({
      editRecord: null,
      scriptEditProps: {},
    });
  }

  @Bind()
  orderRowKey(record, index) {
    return index + 1;
  }

  @Bind()
  handleScriptAdd() {
    const newRecord = {
      _status: 'create',
      name: 'create',
      description: '',
      content: 'function create() {\n}',
    };
    const { dataSource = [] } = this.state;
    this.setState({
      dataSource: [...dataSource, newRecord],
      editRecord: newRecord,
      scriptEditProps: {
        value: newRecord.content,
      },
    });
  }

  handleScriptEdit(record, e) {
    if (e && e.preventDefault) {
      e.preventDefault();
    }
    const { editRecord } = this.state;
    if (editRecord === record) {
      return;
    }
    const { content = '' } = record;
    this.setState({
      editRecord: record,
      scriptEditProps: {
        value: content,
      },
    });
  }

  handleScriptRemove(record, e) {
    if (e && e.preventDefault) {
      e.preventDefault();
    }
    const { editRecord, dataSource } = this.state;

    const nextState = {};
    if (editRecord === record) {
      nextState.editRecord = null;
      nextState.scriptEditProps = {};
    }
    nextState.dataSource = filter(dataSource, r => r !== record);
    this.setState(nextState);
  }

  @Bind()
  handleScriptChange(value) {
    const { scriptEditProps, editRecord } = this.state;
    editRecord.content = value;
    this.setState({
      scriptEditProps: {
        ...scriptEditProps,
        value,
      },
    });
  }

  @Bind()
  getRowClassName(record) {
    const { editRecord } = this.state;
    if (record === editRecord) {
      return styles['active-table-row'];
    }
    return '';
  }
}
