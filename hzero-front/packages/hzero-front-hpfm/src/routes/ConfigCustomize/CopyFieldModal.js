import React, { Component } from 'react';
import { connect } from 'dva';
import { Modal, Button, Form, Table, Tooltip, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { totalRender } from 'utils/renderer';
import { getCurrentOrganizationId, createPagination } from 'utils/utils';

import styles from './index.less';

const rowKey = 'id';

@connect(({ configCustomize, loading }) => ({
  configCustomize,
  fetchLoading: loading.effects['configCustomize/fetchSameModelUnit'],
  saveLoading: loading.effects['configCustomize/copyFiled'],
}))
@Form.create({ fieldNameProp: null })
export default class CopyFieldModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sourceData: [], // 左侧表格数据
      sourcePagination: {}, // 左侧表格分页
      targetData: [], // 右侧表格数据
      sourceSelectedKeys: [], // 左侧表格选中数据
      targetSelectedKeys: [], // 右侧表格选中数据
    };
  }

  componentDidMount() {
    this.fetchSourceData();
  }

  @Bind()
  fetchSourceData(params = {}) {
    const { currentUnit = {}, dispatch } = this.props;
    const { modelId, unitType } = currentUnit;
    dispatch({
      type: 'configCustomize/fetchSameModelUnit',
      params: {
        lovCode: 'HPFM.CUST.UNIT_FOR_FIELD_COPY',
        tenantId: getCurrentOrganizationId(),
        modelId,
        unitType,
        ...params,
      },
    }).then((res) => {
      if (res) {
        if (res.fail) {
          notification.error({ message: res.message });
          return;
        }
        const dataSource = (res || {}).content || [];
        const pagination = createPagination(res || {});
        this.setState({
          sourceData: dataSource,
          sourcePagination: pagination,
        });
      }
    });
  }

  @Bind()
  getTableColumns() {
    return [
      {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.unitName').d('单元名称'),
        dataIndex: 'unitName',
        width: 400,
        render: (_, record) => {
          const { unitName, unitCode } = record;
          return (
            <div>
              <div style={{ fontWeight: 600, color: '#666' }}>{unitName}</div>
              <div style={{ color: '#a5a5a5' }}>
                <Tooltip placement="bottom" title={unitCode}>
                  {unitCode}
                </Tooltip>
              </div>
            </div>
          );
        },
      },
    ];
  }

  @Bind()
  handleSelected(type, selectedKeys) {
    this.setState({
      [type]: selectedKeys,
    });
  }

  @Bind()
  handleTransfer(from) {
    const { sourceData, targetData, sourceSelectedKeys, targetSelectedKeys } = this.state;
    if (from === 'source') {
      const transferData = sourceData.filter((item) => sourceSelectedKeys.includes(item[rowKey]));
      this.setState({
        sourceSelectedKeys: [],
        targetData: targetData.concat(transferData),
      });
    } else {
      const newTargetData = targetData.filter((item) => !targetSelectedKeys.includes(item[rowKey]));
      this.setState({
        targetSelectedKeys: [],
        targetData: newTargetData,
      });
    }
  }

  @Bind()
  handleChangePagination({ current, pageSize }) {
    this.fetchSourceData({ page: { current, pageSize } });
  }

  @Bind()
  handleOk() {
    const { copyFields = [], dispatch, handleClose } = this.props;
    const { targetData = [] } = this.state;
    const configFieldIds = copyFields.map((item) => item.configFieldId);
    const unitIds = targetData.map((item) => item[rowKey]);
    dispatch({
      type: 'configCustomize/copyFiled',
      payload: {
        configFieldIds,
        unitIds,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        handleClose();
      }
    });
  }

  render() {
    const {
      sourceData = [],
      sourcePagination = {},
      targetData = [],
      sourceSelectedKeys = [],
      targetSelectedKeys = [],
    } = this.state;
    const { handleClose, fetchLoading, saveLoading } = this.props;
    return (
      <Modal
        title={intl
          .get('hpfm.individual.model.title.selectUnitCopyField')
          .d('选择个性化单元进行字段拷贝')}
        width={1000}
        destroyOnClose
        wrapClassName={styles['copy-field-modal']}
        visible
        onOk={this.handleOk}
        onCancel={handleClose}
        okButtonProps={{
          disabled: targetData.length < 1,
          loading: saveLoading,
        }}
        cancelButtonProps={{
          disabled: saveLoading,
        }}
      >
        <Spin spinning={fetchLoading || saveLoading || false}>
          <div className={styles['modal-content']}>
            <div className={styles['modal-content-left']}>
              <Table
                rowKey={rowKey}
                resizable={false}
                columns={this.getTableColumns()}
                dataSource={sourceData}
                pagination={sourcePagination}
                onChange={this.handleChangePagination}
                rowSelection={{
                  getCheckboxProps: (record) => ({
                    disabled: targetData.find((item) => item[rowKey] === record[rowKey]),
                  }),
                  selectedRowKeys: sourceSelectedKeys,
                  onChange: (selectedKeys) =>
                    this.handleSelected('sourceSelectedKeys', selectedKeys),
                }}
              />
            </div>
            <div className={styles['modal-content-center']}>
              <div>
                <Button
                  icon="left"
                  type="primary"
                  disabled={targetSelectedKeys.length < 1}
                  onClick={() => this.handleTransfer('target')}
                />
                <Button
                  icon="right"
                  type="primary"
                  disabled={sourceSelectedKeys.length < 1}
                  onClick={() => this.handleTransfer('source')}
                />
              </div>
            </div>
            <div className={styles['modal-content-right']}>
              <Table
                rowKey={rowKey}
                resizable={false}
                columns={this.getTableColumns()}
                pagination={{
                  showSizeChanger: true,
                  showTotal: totalRender,
                  pageSizeOptions: ['10', '20', '50', '100'],
                }}
                dataSource={targetData}
                rowSelection={{
                  selectedRowKeys: targetSelectedKeys,
                  onChange: (selectedKeys) =>
                    this.handleSelected('targetSelectedKeys', selectedKeys),
                }}
              />
            </div>
          </div>
        </Spin>
      </Modal>
    );
  }
}
