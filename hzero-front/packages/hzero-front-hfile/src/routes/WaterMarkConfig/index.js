/*
 * WaterMarkConfig 水印配置
 * @date: 2020-02-13
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Bind } from 'lodash-decorators';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, Modal, ModalContainer, DataSet } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { isTenantRoleLevel } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import Drawer from './Drawer';
import { tableDS, drawerDS } from '../../stores/WaterMarkConfigDS';

const isTenant = isTenantRoleLevel();
@formatterCollections({ code: ['hfile.waterMark'] })
export default class WaterMarkConfig extends React.Component {
  tableDs = new DataSet(tableDS());

  drawerDs = null;

  constructor(props) {
    super(props);
    this.state = {};
  }

  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      !isTenant && {
        header: intl.get('hzero.common.model.common.tenantId').d('租户'),
        name: 'tenantName',
      },
      {
        name: 'watermarkCode',
      },
      {
        name: 'description',
      },
      {
        name: 'watermarkTypeMeaning',
        width: 150,
      },
      {
        name: 'enabledFlag',
        width: 100,
        renderer: ({ value }) => enableRender(value),
        align: 'left',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        renderer: ({ record }) => {
          const { data = {} } = record;
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}/edit`,
                    type: 'button',
                    meaning: '水印配置-编辑',
                  },
                ]}
                onClick={() => {
                  this.handleEdit(false, data);
                }}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
  }

  /**
   * 保存
   */
  @Bind()
  async handleSave() {
    const validate = await this.drawerDs.submit();
    if (!validate) {
      const { current } = this.drawerDs;
      const watermarkType = current.get('watermarkType');
      if ((watermarkType === 'IMAGE' || watermarkType === 'TILE_IMAGE') && !current.get('detail')) {
        notification.warning({
          message: intl
            .get('hfile.waterMark.view.message.alert.contentRequired')
            .d('请上传水印内容'),
        });
      }
      return false;
    }
    await this.tableDs.query();
  }

  /**
   * 新建/编辑
   * @param { boolean } isCreate
   * @param { object } [record={}]
   */
  @Bind()
  handleEdit(isCreate, record = {}) {
    const { watermarkId } = record;
    this.drawerDs = new DataSet(drawerDS());
    this.drawerDs.create({});
    const drawerProps = {
      isTenant,
      isCreate,
      watermarkId,
      ds: this.drawerDs,
      onUploadSuccess: this.onUploadSuccess,
      onCancelSuccess: this.onCancelSuccess,
    };
    Modal.open({
      closable: true,
      key: 'water-mark-config',
      title: isCreate
        ? intl.get('hfile.waterMark.view.message.crete').d('新建水印配置')
        : intl.get('hfile.waterMark.view.message.edit').d('编辑水印配置'),
      drawer: true,
      style: {
        width: 500,
      },
      children: <Drawer {...drawerProps} />,
      onOk: this.handleSave,
    });
  }

  /**
   * 上传文件成功时调用
   * @param {object} file
   */
  @Bind()
  onUploadSuccess(file, isImg) {
    if (file) {
      if (isImg) {
        this.drawerDs.current.set('detail', file.response);
      } else {
        this.drawerDs.current.set('fontUrl', file.response);
      }
    }
  }

  /**
   * 删除文件后调用
   * @param {object} file
   */
  @Bind()
  onCancelSuccess(file, isImg) {
    if (file) {
      if (isImg) {
        this.drawerDs.current.set('detail', '');
      } else {
        this.drawerDs.current.set('fontUrl', '');
      }
    }
  }

  render() {
    const { match, location } = this.props;
    return (
      <>
        <Header title={intl.get('hfile.waterMark.view.message.waterMarkConfig').d('文件水印配置')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.manage-create`,
                type: 'button',
                meaning: '水印配置-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleEdit(true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDs} columns={this.columns} queryFieldsLimit={3} />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
