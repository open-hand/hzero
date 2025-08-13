/**
 * AuthDetail - 除公司以外的tab页（包括动态渲染组件）
 * @date: 2019-11-1
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import { HZERO_IAM } from 'utils/config';
import notification from 'utils/notification';
import { getCurrentOrganizationId, isTenantRoleLevel, getResponse } from 'utils/utils';
import { queryLov } from 'services/api';

import AddDataModal from '@/components/AuthorityManagement/AddDataModal';
import { addData } from '@/services/securityGroupService';
import openAddModal from '../../Components/AddModal';

const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

export default class AuthDetail extends Component {
  constructor(props) {
    super(props);
    this.headInfo = {};
    this.state = {
      lovInfo: {},
      isShowAddModal: false,
    };
  }

  authDetailDS = new DataSet(
    (() => {
      const {
        labels = {},
        secGrpId,
        authorityTypeCode,
        valueSourceType = 'LOCAL',
        isSelf,
        roleId,
        secGrpSource,
      } = this.props;
      const { nameLabel, codeLabel = '' } = labels;
      return {
        autoQuery: true,
        dataKey: 'secGrpDclLineList.content',
        pageSize: 10,
        selection: isSelf ? 'multiple' : false,
        fields: [
          {
            name: 'dataName',
            type: 'string',
            label: nameLabel,
          },
          valueSourceType === 'LOCAL' && {
            name: 'dataCode',
            type: 'string',
            label: codeLabel,
          },
        ].filter(Boolean),
        queryFields: [
          {
            name: 'dataName',
            label: nameLabel,
            type: 'string',
          },
          valueSourceType === 'LOCAL' && {
            name: 'dataCode',
            label: codeLabel,
            type: 'string',
          },
        ].filter(Boolean),
        transport: {
          read: ({ data, params }) => ({
            url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-dcls/${secGrpId}/authority${
              isSelf ? '' : '/assigned'
            }`,
            params: {
              ...data,
              ...params,
              roleId,
              authorityTypeCode,
              secGrpSource,
            },
            method: 'get',
          }),
          destroy: ({ data }) => ({
            url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-dcls/${secGrpId}/authority?authorityTypeCode=${authorityTypeCode}`,
            data,
            method: 'delete',
          }),
        },
        feedback: {
          loadSuccess: res => {
            if (res) {
              if ('secGrpDcl' in res) {
                const {
                  secGrpDcl: { secGrpDclId, _token, objectVersionNumber },
                } = res;
                this.headInfo = {
                  secGrpDclId,
                  _token,
                  objectVersionNumber,
                };
              }
              if (valueSourceType === 'LOV') {
                this.handleCheckViewCode();
              }
            }
          },
        },
      };
    })()
  );

  /**
   * 动态组件值集查询
   */
  @Bind()
  handleCheckViewCode() {
    const { viewCode } = this.props;
    queryLov({ viewCode }).then(res => {
      const lovInfo = { ...res };
      if (!isEmpty(lovInfo)) {
        const { viewCode: hasCode } = lovInfo;
        if (hasCode) {
          this.setState({
            lovInfo,
          });
        } else {
          notification.error({
            message: intl
              .get('hzero.common.components.lov.notification.undefined')
              .d('值集视图未定义!'),
          });
        }
      }
    });
  }

  /**
   * 删除权限
   */
  @Bind()
  async handleDeletePermission() {
    const res = await this.authDetailDS.delete();
    if (!isEmpty(res) && res.failed && res.message) {
      notification.error({ message: res.message });
    } else if (!isEmpty(res) && res.success) {
      this.authDetailDS.query();
    }
  }

  get columns() {
    const { valueSourceType } = this.props;
    return [{ name: 'dataName' }, valueSourceType === 'LOCAL' && { name: 'dataCode' }].filter(
      Boolean
    );
  }

  get buttons() {
    const { labels = {}, valueSourceType, isSelf } = this.props;
    const { addLabel, deleteLabel } = labels;
    const addButtons = [
      [
        'add',
        {
          children: addLabel,
          onClick: valueSourceType === 'LOCAL' ? this.handleOpenDrawer : this.handleShowAddModal,
        },
      ],
      [
        'delete',
        {
          children: deleteLabel,
        },
      ],
    ];
    return isSelf ? addButtons : [];
  }

  /**
   * 显示新建弹窗
   */
  @Bind()
  handleOpenDrawer() {
    const { roleId, secGrpId, authorityTypeCode, labels, isSelf, secGrpSource } = this.props;
    const { addTitleLabel, nameTitleLabel, codeTitleLabel } = labels;
    const { secGrpDclId } = this.headInfo;
    openAddModal({
      title: addTitleLabel,
      nameTitle: nameTitleLabel,
      codeTitle: codeTitleLabel,
      roleId,
      isSelf,
      secGrpSource,
      authorityTypeCode,
      secGrpId,
      secGrpDclId,
      pageDataSet: this.authDetailDS,
    });
  }

  /**
   * 显示动态组件
   */
  @Bind()
  handleShowAddModal() {
    const { lovInfo } = this.state;
    if (!isEmpty(lovInfo)) {
      this.setState({ isShowAddModal: true });
    } else {
      notification.error({
        message: intl
          .get('hzero.common.components.lov.notification.undefined')
          .d('值集视图未定义!'),
      });
    }
  }

  /**
   * 隐藏动态组件
   */
  @Bind()
  handleHideAddModal() {
    this.setState({ isShowAddModal: false });
    this.authDetailRef.state.addRows = [];
  }

  /**
   * 动态组件添加数据
   * @param {array} addRows - 选中的行
   */
  @Bind()
  async handleAddData(addRows) {
    const { secGrpId, authorityTypeCode } = this.props;
    const res = await addData({
      secGrpId,
      data: addRows,
      authorityTypeCode,
    });
    if (getResponse(res)) {
      notification.success();
      this.handleHideAddModal();
      this.authDetailDS.query();
    }
  }

  render() {
    const { authorityTypeCode, valueSourceType, addTitleLabel } = this.props;
    const { isShowAddModal, lovInfo } = this.state;
    const addModalOptions = {
      rowKey: 'dataId',
      title: addTitleLabel,
      lov: lovInfo,
      confirmLoading: false,
      addData: this.handleAddData,
      modalVisible: isShowAddModal,
      onShowAddModal: this.handleShowAddModal,
      onHideAddModal: this.handleHideAddModal,
      ref: node => {
        this.authDetailRef = node;
      },
    };

    return (
      <>
        <Table
          key={authorityTypeCode}
          dataSet={this.authDetailDS}
          queryFieldsLimit={valueSourceType === 'LOCAL' ? 2 : 1}
          columns={this.columns}
          buttons={this.buttons}
        />
        <AddDataModal {...addModalOptions} />
      </>
    );
  }
}
