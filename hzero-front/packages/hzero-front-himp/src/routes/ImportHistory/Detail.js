/**
 * @since 2019-10-10
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Table, Form, Output } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import classNames from 'classnames';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import { HZERO_IMP } from 'utils/config';
import { TagRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';

import queryString from 'querystring';
import { loadTemplate } from '../../services/importHistoryService';
import styles from './Detail.less';

@formatterCollections({ code: ['himp.importHistory', 'himp.comment'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    const {
      location: { search = '' },
    } = props;
    const { prefixPatch } = queryString.parse(search);
    this.state = {
      tableDS: null, // 动态ds
      prefixPatch,
      dynamicColumns: [], // 动态列
    };
  }

  componentDidMount() {
    const { match = {} } = this.props;
    const { params } = match;
    if (params.templateCode) {
      this.getTemplate();
    }
  }

  /**
   * 获取模板(动态列, 客户端前缀)
   */
  @Bind()
  getTemplate() {
    const { match = {} } = this.props;
    const { params = {} } = match;
    const { prefixPatch = '' } = this.state;
    loadTemplate({ code: params.templateCode, prefixPatch }).then((res) => {
      const parsedRes = getResponse(res);
      if (parsedRes) {
        const { prefixPatch: newPrefixPatch = '', templateType, templateTargetList = [] } = res;
        const { templateLineList = [] } = templateTargetList[0] || {};
        const dynamicFields = templateLineList.map((item) => ({
          name: item.columnCode,
          label: item.columnName,
          type: item.columnType !== 'Long' ? item.columnType : 'Number',
          required: item.nullable,
        }));
        this.setState({
          prefixPatch: newPrefixPatch || prefixPatch || (templateType === 'C' ? '' : HZERO_IMP),
          dynamicColumns: templateLineList.map((item, index) => {
            if (index !== 0) {
              return {
                name: item.columnCode,
              };
            } else {
              return {
                name: item.columnCode,
                width: item.columnName.length * 16 + 64,
              };
            }
          }),
          formDS: new DataSet({
            autoCreate: true,
            autoQuery: true,
            dataKey: 'content',
            fields: [
              {
                name: 'batch',
                type: 'string',
                label: intl.get('himp.importHistory.view.title.importBatch').d('导入批次号'),
              },
              {
                name: 'createdUserName',
                type: 'string',
                label: intl.get('himp.importHistory.view.title.createUser').d('创建人'),
              },
              {
                name: 'creationDate',
                type: 'string',
                label: intl.get('himp.importHistory.view.title.createTime').d('创建时间'),
              },
            ],
            transport: {
              read: (config) => {
                const {
                  params: { importId = 0 },
                } = this.props.match;
                const url = `${
                  this.state.prefixPatch
                }/v1/${getCurrentOrganizationId()}/import/manager/${importId}`;
                return {
                  ...config,
                  url,
                  method: 'GET',
                };
              },
            },
          }),
          tableDS: new DataSet({
            autoQuery: true,
            selection: false,
            fields: [
              ...dynamicFields,
              {
                name: '_dataStatus',
                type: 'string',
                label: intl.get('himp.comment.model.comment.dataStatus').d('数据状态'),
              },
              {
                name: '_info',
                type: 'string',
                label: intl.get('himp.comment.model.comment.errorMsg').d('错误信息'),
              },
            ],
            transport: {
              read: () => {
                const {
                  params: { templateCode = '', batch = '' },
                } = this.props.match;
                const url = `${
                  this.state.prefixPatch
                }/v1/${getCurrentOrganizationId()}/import/data?templateCode=${templateCode}&batch=${batch}`;
                return {
                  url,
                  method: 'GET',
                  transformResponse: (data) => {
                    const tempData = JSON.parse(data);
                    if (tempData.content.length === 0) return tempData;
                    tempData.content = tempData.content.map((item) => {
                      const {
                        _batch = '',
                        _dataStatus = '',
                        _dataStatusMeaning = '',
                        _id = '',
                        _templateCode = '',
                        _info = '',
                      } = item;
                      return {
                        _batch,
                        ...JSON.parse(item._data),
                        _dataStatus,
                        _dataStatusMeaning,
                        _id,
                        _templateCode,
                        _info,
                      };
                    });
                    return tempData;
                  },
                };
              },
            },
          }),
        });
      }
    });
  }

  get columns() {
    return [
      ...this.state.dynamicColumns,
      {
        name: '_dataStatus',
        renderer: (records) => {
          const { record } = records;
          const {
            data: { _dataStatusMeaning = '' },
          } = record;
          const statusList = [
            { status: 'NEW', color: 'blue' },
            { status: 'VALID_SUCCESS', color: 'green' },
            { status: 'VALID_FAILED', color: 'red' },
            { status: 'IMPORT_SUCCESS', color: 'green' },
            { status: 'IMPORT_FAILED', color: 'red' },
            { status: 'ERROR', color: 'red' },
          ];
          const tagItem = statusList.find((t) => t.status === records.value) || {};
          return TagRender(records.value, [
            {
              status: tagItem.status,
              text: _dataStatusMeaning,
              color: tagItem.color,
            },
          ]);
        },
      },
      {
        name: '_info',
        width: 200,
      },
    ];
  }

  render() {
    const { formDS, tableDS } = this.state;
    return (
      <>
        <Header
          title={intl.get('himp.importHistory.view.message.historyDetail').d('历史详情')}
          backPath="/himp/history/list"
        />
        <Content>
          <div className={classNames(styles['label-col'])}>
            {intl.get('himp.importHistory.view.message.importHistory').d('导入历史')}
          </div>
          <Form dataSet={formDS} columns={3}>
            <Output name="batch" />
            <Output name="createdUserName" />
            <Output name="creationDate" />
          </Form>
          <div className={classNames(styles['label-col'])} style={{ marginBottom: 20 }}>
            {intl.get('himp.importHistory.view.message.detailData').d('数据详情')}
          </div>
          {tableDS !== null ? (
            <Table dataSet={tableDS} columns={this.columns} queryBar="normal" />
          ) : null}
        </Content>
      </>
    );
  }
}
