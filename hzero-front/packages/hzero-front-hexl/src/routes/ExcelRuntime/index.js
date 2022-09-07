/*
 * @description: Excel试算
 * @Author: suyu.zeng@hand-china.com
 * @Date: 2019-09-17 12:04:38
 * @LastEditors  : shenghao.liu@hand-china.com
 * @LastEditTime : 2020-01-03 16:00:44
 */
import React, { Component } from 'react';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import { Button, DataSet, Lov, Modal, Table } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';
import notification from 'utils/notification';
import { API_HOST, HZERO_HEXL } from 'utils/config';
import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import { getAccessToken, getCurrentOrganizationId } from 'utils/utils';
import excelRuntimeDs from './stores/excelRuntimeDataSet';
import styles from './index.less';
@formatterCollections({ code: ['hzero.hexl'] })
export default class ExcelRuntime extends Component {
  constructor(props) {
    super(props);

    // 实例化dataset
    this.excelRuntimeDs = new DataSet({
      ...excelRuntimeDs(),
      events: {
        // indexChnage: this.updateConfigHdMultiDsCurRecord,
        // select: this.updateConfigHdMultiDsCurRecord,
      },
    });
  }

  @Bind()
  runtimeCreate() {
    this.excelRuntimeDs.create({}, 0);
  }

  @Bind()
  runtimeRefresh() {
    this.excelRuntimeDs.query();
  }

  @Bind()
  async runtimeSave() {
    try {
      const validFlag = await this.excelRuntimeDs.validate();
      if (validFlag) {
        const res = await this.excelRuntimeDs.submit();
        if (!isEmpty(res) && res.failed && res.message) {
          notification.error({ message: res.message });
        } else {
          this.excelRuntimeDs.query();
        }
      } else {
        notification.error({
          message: intl.get('hzero.common.notification.invalid').d('校验不通过'),
        });
      }
    } catch (err) {
      // notification.error({
      //   message: `${intl.get('hzero.common.notification.error').d('操作失败')}:${err.message}`,
      // });
      // this.excelRuntimeDs.query();
    }
  }

  @Bind()
  async runtimeDelete() {
    if (this.excelRuntimeDs.selected.length > 0) {
      try {
        const res = await this.excelRuntimeDs.delete(this.excelRuntimeDs.selected);
        if (!isEmpty(res) && res.failed && res.message) {
          notification.error({ message: res.message });
        } else {
          this.excelRuntimeDs.query();
        }
      } catch (err) {
        notification.error({
          message: `${intl.get('hzero.common.notification.error').d('操作失败')}:${err.message}`,
        });
        // this.excelRuntimeDs.query();
      }
    } else {
      notification.warning({
        message: intl.get('hzero.common.validation.atLeast').d('请至少选择一条数据'),
      });
    }
  }

  get buttons() {
    return [
      <Button funcType="raised" icon="refresh" onClick={this.runtimeRefresh} key="refresh">
        {intl.get('hzero.common.button.refresh').d('刷新')}
      </Button>,
      <Button funcType="raised" icon="save" onClick={this.runtimeSave} key="save">
        {intl.get('hzero.common.button.save').d('保存')}
      </Button>,
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${this.props.match.path}.button.delete`,
            type: 'button',
            meaning: 'WebExcel试算-删除',
          },
        ]}
        funcType="raised"
        icon="delete"
        onClick={this.runtimeDelete}
        key="delete"
      >
        {intl.get('hzero.common.button.delete').d('删除')}
      </ButtonPermission>,
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${this.props.match.path}.button.create`,
            type: 'button',
            meaning: 'WebExcel试算-新建',
          },
        ]}
        funcType="raised"
        color="primary"
        icon="add"
        onClick={this.runtimeCreate}
        key="add"
      >
        {intl.get('hzero.common.button.add').d('新增')}
      </ButtonPermission>,
    ];
  }

  @Bind()
  calcColumnRenderer({ record }) {
    if (record.pristineData.runtimeId) {
      return (
        <Button funcType="flat" icon="mode_edit" onClick={(e) => this.openEditModal(record, e)}>
          {intl.get('hzero.hexl.field.runtime').d('试算')}
        </Button>
      );
    }
  }

  // 试算窗口
  @Bind()
  openEditModal(record) {
    Modal.open({
      drawer: true,
      style: { width: '100%' },
      bodyStyle: { height: '100%' },
      title: intl.get('hzero.hexl.field.runtime').d('试算'),
      closable: true,
      className: styles.editModalWrap,
      children: (
        <div className={styles.ModalIframeWrap}>
          <iframe
            title={intl.get('hzero.hexl.field.runtime').d('试算')}
            src={`${API_HOST}${HZERO_HEXL}/index.html?templateCode=${
              record.pristineData.templateCode
            }&access_token=${getAccessToken()}&readOnly=N&organizationId=${getCurrentOrganizationId()}&runtimeId=${
              record.pristineData.runtimeId
            }&templateVersion=${record.pristineData.templateVersion}`}
            width="95%"
          />
        </div>
      ),
      footer: null,
      onClose: () => {
        this.excelRuntimeDs.query();
      },
    });
  }

  get columns() {
    return [
      { name: 'description', editor: true, sortable: true },
      {
        name: 'templateRecord',
        editor: (record) =>
          record.status === 'add' ? (
            <Lov
              placeholder={intl.get('hzero.hexl.prompt.selectTemplate').d('请选择Excel模板')}
              noCache
            />
          ) : null,
        renderer: ({ record }) => record.get('templateCode'),
        sortable: true,
        align: 'center',
      },
      {
        name: 'templateVersion',
        editor: false,
        sortable: true,
      },
      {
        name: 'action',
        editor: true,
        width: 150,
        renderer: this.calcColumnRenderer,
        header: <span>{intl.get('hzero.common.button.action').d('操作')}</span>,
        align: 'center',
      },
    ];
  }

  render() {
    return (
      <>
        <Header title={intl.get('hzero.hexl.field.excelRuntime').d('WebExcel试算')}>
          {this.buttons}
        </Header>
        <Content>
          <Table dataSet={this.excelRuntimeDs} border queryFieldsLimit={2} columns={this.columns} />
        </Content>
      </>
    );
  }
}
