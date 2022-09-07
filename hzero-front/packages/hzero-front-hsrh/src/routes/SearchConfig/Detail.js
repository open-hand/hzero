/**
 * @since 2019-10-16
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Table, Form, Button, TextField, ModalContainer, Switch, DataSet } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { detailFormDS, detailTableDS } from '@/stores/searchConfigGroupDS';
import {
  fetchSync,
  fetchEditSyncFields,
  fetchEditBasicInformation,
} from '@/services/searchConfigService';

export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.detailFormDS = new DataSet(detailFormDS());
    this.detailTableDS = new DataSet(detailTableDS(this.detailFormDS));
  }

  componentDidMount() {
    this.refresh();
  }

  // 刷新页面
  @Bind()
  refresh() {
    const { match } = this.props;
    const {
      params: { type, indexId = '' },
    } = match;
    if (type === 'create') {
      this.detailFormDS.create({});
      this.detailTableDS.remove();
    } else {
      this.detailFormDS.setQueryParameter('indexId', indexId);
      this.detailTableDS.setQueryParameter('indexId', indexId);
      this.detailFormDS.query();
      this.detailTableDS.query();
    }
  }

  get columns() {
    const {
      location: { search = '' },
    } = this.props;
    const { enabledFlag } = queryString.parse(search.substring(1));
    const { match } = this.props;
    const {
      params: { type },
    } = match;
    const flag = type === 'edit' ? enabledFlag === '0' : true;

    return [
      {
        name: 'fieldName',
        align: 'left',
        editor: flag,
      },
      // {
      //   name: 'fieldMapping',
      //   align: 'left',
      //   editor: flag,
      // },
      {
        name: 'fieldType',
        align: 'left',
        editor: flag,
      },
      {
        name: 'analyzerFlag',
        align: 'left',
        editor: flag,
      },
      {
        name: 'filedAnalyzerCode',
        align: 'left',
        editor: flag,
      },
      {
        name: 'pkFlag',
        align: 'left',
        editor: flag,
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 160,
        renderer: ({ record }) => {
          const actions = [];
          actions.push({
            ele: (
              <a
                onClick={() => {
                  this.handleDelete(record);
                }}
                disabled={!flag}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </a>
            ),
            key: 'delete',
            len: 2,
            title: intl.get('hzero.common.button.delete').d('删除'),
          });
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'center',
      },
    ];
  }

  /**
   * 删除行
   */
  @Bind()
  async handleDelete(record) {
    const { match } = this.props;
    const {
      params: { type },
    } = match;
    await this.detailTableDS.delete(record);
    if (type !== 'create') {
      this.detailTableDS.query();
    }
  }

  @Bind()
  async handleSave() {
    const { history, match } = this.props;
    const {
      params: { type },
    } = match;
    const formChange = this.detailFormDS.isModified();
    const tableChange = this.detailTableDS.isModified();
    if (formChange || tableChange) {
      let res = '';
      const validate = await this.detailFormDS.validate();
      switch (type) {
        case 'edit':
          if (validate) {
            try {
              const basicInformationParams = this.detailFormDS.toData();
              const { indexId } = basicInformationParams[0];
              const syncParams = this.detailTableDS.data.map((item) => {
                const tempItemData = item.toData();
                if (tempItemData.fieldId === undefined) {
                  tempItemData.indexId = indexId;
                  return tempItemData;
                }
                return tempItemData;
              });
              res = await Promise.all([
                fetchEditBasicInformation(basicInformationParams[0]),
                fetchEditSyncFields(syncParams),
              ]);
              if (res[0] && res[0].objectVersionNumber) {
                notification.success({
                  message: intl
                    .get('hsrh.searchConfig.view.message.editInformationSuccess')
                    .d('编辑信息成功！'),
                });
                history.push('/hsrh/search-config/list');
              } else {
                notification.error({
                  message: intl
                    .get('hsrh.searchConfig.view.message.editInformationError')
                    .d('编辑信息失败！'),
                });
              }
            } catch (err) {
              // 猪齿鱼自动提示
            }
          }
          break;
        default:
          if (validate) {
            try {
              res = await this.detailTableDS.submit();
              if (res && res.success) {
                history.push('/hsrh/search-config/list');
              }
              if (!res && type === 'create') {
                notification.info({
                  message: intl
                    .get('hsrh.searchConfig.view.message.rowNotification')
                    .d('请添加同步字段行信息'),
                });
              } else if (!res && type === 'copy') {
                notification.info({
                  message: intl
                    .get('hsrh.searchConfig.view.message.HeadOrRowNotification')
                    .d('基本信息头或者同步字段行未修改'),
                });
              }
            } catch (err) {
              // 猪齿鱼自动提示
            }
          }
      }
    } else {
      notification.info({
        message: intl.get('hsrh.searchConfig.view.message.dataNotChange').d('基本信息未改变'),
      });
    }
  }

  // 同步
  @Bind()
  async handleSync() {
    const { indexCode } = this.detailFormDS.current.toData();
    const res = await fetchSync({ indexCode });
    if (res && res.failed === true) {
      notification.info({
        message: res.message,
      });
    } else {
      notification.success({
        message: intl.get('hsrh.searchConfig.view.message.asyncSuccess').d('同步信息成功!'),
      });
    }
  }

  // 状态更新
  @Bind()
  async enabledFlagChange(enabledFlag) {
    const { match, history } = this.props;
    const {
      params: { type, indexId = '' },
    } = match;
    if (type === 'edit') {
      const basicInformationParams = this.detailFormDS.toData();
      await fetchEditBasicInformation(basicInformationParams[0]).then((res) => {
        if (res) {
          this.refresh();
          notification.success({
            message: intl
              .get('hsrh.searchConfig.view.message.editInformationSuccess')
              .d('编辑信息成功！'),
          });
        }
      });
      history.push(`/hsrh/search-config/${type}/${indexId}?enabledFlag=${enabledFlag}`);
    }
  }

  render() {
    const { location } = this.props;
    const { search = '' } = location;
    const { enabledFlag } = queryString.parse(search.substring(1));
    const { match } = this.props;
    const {
      params: { type },
    } = match;
    const judge = type === 'edit';
    // const indexCodeFlag = type !== 'edit';
    const editFlag = judge ? enabledFlag === '1' : false;
    // eslint-disable-next-line no-nested-ternary
    const buttonConfig = judge ? (enabledFlag === '1' ? [] : ['add']) : ['add'];
    return (
      <React.Fragment>
        <Header
          title={intl.get('hsrh.searchConfig.view.title.searchConfigDetail').d('索引配置详情')}
          backPath="/hsrh/search-config/list"
        >
          {!editFlag ? (
            <Button color="primary" icon="save" onClick={this.handleSave}>
              {intl.get(`hzero.common.button.save`).d('保存')}
            </Button>
          ) : null}
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hsrh.searchConfig.view.title.basicInformation').d('基本信息')}</h3>
            }
          >
            <Form labelLayout="horizontal" dataSet={this.detailFormDS} columns={3}>
              <TextField name="indexCode" disabled={judge} />
              <TextField name="remark" disabled={editFlag} />
              <Switch name="enabledFlag" onChange={this.enabledFlagChange} />
              <TextField name="shards" disabled={judge} />
              <TextField name="replicas" disabled={judge} />
              <TextField name="createUser" disabled />
              <TextField name="updateUser" disabled />
              <TextField name="lastUpdateDate" disabled />
              <TextField name="creationDate" disabled />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hsrh.searchConfig.view.title.syncField').d('同步字段')}</h3>}
          >
            <Table dataSet={this.detailTableDS} columns={this.columns} buttons={buttonConfig} />
          </Card>
          <ModalContainer location={location} />
        </Content>
      </React.Fragment>
    );
  }
}
