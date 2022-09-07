/**
 * DataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-30
 * @copyright 2019-05-30 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Table, Tooltip } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';

import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

import Span from '@/components/ProxyComponent/Span';

import styles from './styles.less';

const actionStyle = {
  cursor: 'pointer',
};

const rowKey = 'id';

export default class DataTable extends Component {
  static propTypes = {
    onChange: PropTypes.func.isRequired,
    onRecordView: PropTypes.func.isRequired,
    languageMessage: PropTypes.object.isRequired,
    dataSource: PropTypes.array,
    pagination: PropTypes.any,
  };

  static defaultProps = {
    dataSource: [],
    pagination: false,
  };

  @Bind()
  handleRecordViewBtnClick(record) {
    const { onRecordView } = this.props;
    onRecordView(record);
  }

  getColumns() {
    const { languageMessage, isSiteFlag } = this.props;
    return [
      isSiteFlag && {
        dataIndex: 'tenantName',
        width: 240,
        title: languageMessage.model.textExtractionLog.tenant,
      },
      {
        dataIndex: 'templateCode',
        title: languageMessage.model.textExtractionLog.templateCode,
        width: 200,
      },
      {
        dataIndex: 'text',
        title: languageMessage.model.textExtractionLog.text,
      },
      {
        dataIndex: 'convertText',
        title: languageMessage.model.textExtractionLog.convert,
      },
      {
        dataIndex: 'contexts',
        title: languageMessage.model.textExtractionLog.contexts,
        width: 240,
        render: (contexts = []) => {
          if (!isNil(contexts)) {
            if (contexts.length >= 2) {
              const title = (
                <ul className={styles['hnlp-text-extractionlog-list']}>
                  {contexts.map(({ contextKey, contextType }) => (
                    <li key={contextKey}>
                      <div>
                        <p>contextKey: {contextKey}</p>
                        <p>contextType: {contextType}</p>
                      </div>
                    </li>
                  ))}
                </ul>
              );
              return (
                <Tooltip title={title}>
                  <ul className={styles['hnlp-text-extractionlog-list']}>
                    <li>
                      <div>
                        <p>contextKey: {contexts[0].contextKey}</p>
                        <p>contextType: {contexts[0].contextType}</p>
                      </div>
                    </li>
                    <li>...</li>
                  </ul>
                </Tooltip>
              );
            } else {
              return (
                <ul className={styles['hnlp-text-extractionlog-list']}>
                  {contexts.map(({ contextKey, contextType }) => (
                    <li key={contextKey}>
                      <div>
                        <p>contextKey: {contextKey}</p>
                        <p>contextType: {contextType}</p>
                      </div>
                    </li>
                  ))}
                </ul>
              );
            }
          }
        },
      },
      {
        key: 'action',
        title: languageMessage.common.btn.action,
        width: 80,
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'view',
              ele: (
                <Span style={actionStyle} record={record} onClick={this.handleRecordViewBtnClick}>
                  {languageMessage.common.btn.view}
                </Span>
              ),
              len: 2,
              title: languageMessage.common.btn.view,
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
  }

  // #endregion

  render() {
    const { dataSource = [], pagination = false, queryLoading = false, onChange } = this.props;
    const columns = this.getColumns();
    return (
      <Table
        bordered
        rowKey={rowKey}
        onChange={onChange}
        dataSource={dataSource}
        pagination={pagination}
        loading={queryLoading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
      />
    );
  }
}
