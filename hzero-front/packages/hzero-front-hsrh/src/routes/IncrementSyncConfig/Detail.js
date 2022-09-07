/**
 * @since 2019-10-16
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Card } from 'choerodon-ui';
import {
  DataSet,
  Table,
  Form,
  Button,
  TextField,
  Modal,
  Lov,
  ModalContainer,
  Select,
  TextArea,
  Switch,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { detailFormDS, detailTableDS } from '@/stores/incrementSyncConfigGroupDS';
import { fetchSync } from '@/services/incrementSyncConfigService';

export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isSuccess: false,
      syncConfId: null,
      isDB: true,
    };
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
      params: { type, syncConfId = '' },
    } = match;
    if (type === 'create') {
      this.detailFormDS.create({});
    } else {
      this.setState({ syncConfId, isSuccess: true });
      this.detailFormDS.setQueryParameter('syncConfId', syncConfId);
      this.detailTableDS.setQueryParameter('syncConfId', syncConfId);
      this.detailFormDS.query().then((res) => {
        if (res) {
          this.sourceFromTypeChange(res.sourceFromType);
        }
      });
      this.detailTableDS.query();
    }
  }

  get columns() {
    const { match } = this.props;
    const {
      params: { type },
    } = match;
    const judge = type === 'detail';
    return [
      {
        name: 'fieldName',
        align: 'left',
      },
      {
        name: 'fieldMapping',
        align: 'left',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 200,
        renderer: ({ record }) => {
          const actions = [];
          actions.push(
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleEdit(record);
                  }}
                  disabled={judge}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleDelete(record);
                  }}
                  disabled={judge}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </a>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'center',
      },
    ];
  }

  /**
   * 编辑行
   */
  @Bind()
  handleEdit(record) {
    this.openModal(record);
  }

  /**
   * 删除行
   */
  @Bind()
  async handleDelete(record) {
    await this.detailTableDS.delete(record);
  }

  /**
   * 保存头
   */
  @Bind()
  async handleSave(isCreate) {
    if (this.detailFormDS.isModified()) {
      const validate = await this.detailFormDS.validate();
      if (isCreate && validate) {
        try {
          const res = await this.detailFormDS.submit();
          const { success } = res;
          // const { syncConfId } = content[0];
          if (success) {
            const { history } = this.props;
            history.push('/hsrh/increment-sync/list');
            // this.detailFormDS.setQueryParameter('syncConfId', syncConfId);
            // this.detailFormDS.query();
            // this.setState({
            //   isSuccess: true,
            //   syncConfId,
            // });
          }
        } catch (err) {
          // 猪齿鱼自动弹出提示
        }
      }
    } else {
      notification.info({
        message: intl.get('hsrh.incrementSync.view.message.dataNotChange').d('数据未改变'),
      });
    }
  }

  /**
   * 同步信息
   */
  @Bind()
  async handleSync() {
    const { syncConfCode } = this.detailFormDS.current.toData();
    const res = await fetchSync({ syncConfCode });
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

  /**
   * 取消
   */
  @Bind()
  handleCancel() {
    const { history } = this.props;
    history.push('/hsrh/increment-sync/list');
  }

  /**
   * 打开模态框
   */
  @Bind()
  openModal(record, isNew) {
    let isCancel = false;
    const { match } = this.props;
    const {
      params: { type },
    } = match;
    const judge = type === 'create';
    Modal.open({
      title: judge
        ? intl.get('hsrh.incrementSync.view.title.crete').d('新建同步字段')
        : intl.get('hsrh.incrementSync.view.title.edit').d('编辑同步字段'),
      drawer: true,
      width: 520,
      children: (
        <Form dataSet={this.detailTableDS}>
          <Lov name="fieldIdSet" />
          <TextField name="fieldMapping" />
        </Form>
      ),
      onOk: async () => {
        const validate = this.detailTableDS.validate();
        console.log(validate);
        if (validate) {
          const res = await this.detailTableDS.submit();
          if (res) {
            const { syncConfId } = this.state;
            this.detailTableDS.setQueryParameter('syncConfId', syncConfId);
            this.detailTableDS.query();
          }
          return res;
        } else {
          return false;
        }
      },
      onCancel: () => {
        isCancel = true;
        return isCancel;
      },
      afterClose: () => isCancel && isNew && this.detailTableDS.remove(record),
    });
  }

  /**
   * 新建行
   */
  @Bind()
  createItem() {
    const { syncConfId } = this.state;
    this.openModal(this.detailTableDS.create({ syncConfId }), true);
  }

  @Bind()
  sourceFromTypeChange(res) {
    this.setState({
      isDB: res !== 'DB',
    });
  }

  /**
   * 列表button
   */
  createButton = (
    <Button funcType="flat" color="primary" icon="playlist_add" onClick={this.createItem} key="add">
      {intl.get('hzero.common.button.add').d('新增')}
    </Button>
  );

  render() {
    const { isSuccess, isDB } = this.state;
    const { match, location } = this.props;
    const {
      params: { type },
    } = match;
    const judge = !(type === 'create' || type === 'edit');
    const buttonConfig = !judge && isSuccess ? [this.createButton] : [];
    // const tableNameEdit = type !== 'create';
    return (
      <React.Fragment>
        <Header
          title={
            // eslint-disable-next-line no-nested-ternary
            judge
              ? type === 'create'
                ? intl.get('hsrh.incrementSync.view.title.createSyncConfig').d('新建索引同步配置')
                : intl.get('hsrh.incrementSync.view.title.editSyncConfig').d('编辑索引同步配置')
              : intl.get('hsrh.incrementSync.view.title.SyncConfigDetail').d('索引同步配置详情')
          }
          backPath="/hsrh/increment-sync/list"
        >
          {type !== 'detail' ? (
            <React.Fragment>
              <Button color="primary" icon="save" onClick={() => this.handleSave(true)}>
                {intl.get(`hzero.common.button.save`).d('保存')}
              </Button>
              {type !== 'create' ? (
                <Button icon="save" onClick={this.handleSync}>
                  {intl.get(`hzero.common.button.sync`).d('同步')}
                </Button>
              ) : null}
            </React.Fragment>
          ) : null}
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hsrh.syncConfig.view.title.basicInformation').d('基本信息')}</h3>}
            loading={false}
          >
            <Form labelLayout="horizontal" dataSet={this.detailFormDS} columns={3}>
              <TextField name="syncConfCode" disabled={type === 'edit'} format="uppercase" />
              <Lov name="indexSet" disabled={type !== 'create'} />
              <TextField name="remark" disabled={judge} />
              <Select name="sourceFromType" disabled={judge} onChange={this.sourceFromTypeChange} />
              <Lov name="dataSourceApiLov" disabled={judge} />
              <TextField name="sourceFromDetailCode" disabled />
              <Switch name="isRecord" disabled={judge} />
              {/* <TextField name="batchNumber" disabled={judge} /> */}
              <TextArea
                newLine
                colSpan={12}
                rows={3}
                name="commandContent"
                disabled={isDB}
                resize="both"
              />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hsrh.incrementSync.view.title.syncField').d('同步字段')}</h3>}
            loading={false}
          >
            <Table dataSet={this.detailTableDS} columns={this.columns} buttons={buttonConfig} />
          </Card>
          <ModalContainer location={location} />
        </Content>
      </React.Fragment>
    );
  }
}
