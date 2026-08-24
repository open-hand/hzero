/**
 * UploadCa - 上传证书
 * @date: 2019-9-11
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Upload } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { API_HOST, HZERO_PLATFORM, VERSION_IS_OP } from 'utils/config';
import notification from 'utils/notification';
import { getAccessToken, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';

const limitSize = 1024;

/**
 * 上传CA证书
 * @extends {Component} - React.Component
 * @reactProps {Boolean} isCreate - 是否为新建
 * @reactProps {number} id - 证书id
 * @reactProps {Function} onReload - 重新加载页面
 * @return React.element
 */
export default class UploadCa extends Component {
  state = {
    createLoading: false,
  };

  @Bind()
  getUploadProps() {
    const { onReload = e => e, id = null, isCreate } = this.props;
    const organizationId = getCurrentOrganizationId();
    const isTenant = isTenantRoleLevel();
    const actionSuffix =
      isTenant || VERSION_IS_OP ? `${organizationId}/certificates` : 'certificates';
    const handledAction = isCreate ? actionSuffix : `${actionSuffix}/update`;
    const data = isCreate ? {} : { certificateId: id };
    return {
      name: 'customMenuFile',
      accept: '.cer',
      data,
      headers: {
        Authorization: `bearer ${getAccessToken()}`,
      },
      action: `${API_HOST}${HZERO_PLATFORM}/v1/${handledAction}`,
      showUploadList: false,
      beforeUpload: file => {
        const { size } = file;
        if (size > limitSize * 1024) {
          notification.warning({
            message: intl
              .get('hpfm.caManagement.view.message.upload.limit', { size: limitSize / 1024 })
              .d(`证书大小不能超过${limitSize / 1024}M`),
          });
          return false;
        }
      },
      onChange: ({ file }) => {
        const { status, response } = file;
        if (isCreate) {
          this.setState({ createLoading: status === 'uploading' });
        }
        if (status === 'done' && !response.failed) {
          notification.success({
            message: intl.get('hpfm.caManagement.view.message.upload.success').d('上传成功'),
          });
          onReload();
        } else if (status === 'done' && response.failed) {
          notification.error({ message: response.message });
        } else if (status === 'error') {
          notification.error();
        }
      },
    };
  }

  render() {
    const { isCreate } = this.props;
    const { createLoading } = this.state;
    const uploadProps = this.getUploadProps();
    return (
      <Upload {...uploadProps}>
        {isCreate ? (
          <Button icon="upload" type="primary" loading={createLoading}>
            {intl.get('hpfm.caManagement.view.button.upload').d('上传证书')}
          </Button>
        ) : (
          <a>{intl.get('hpfm.caManagement.view.button.upload.again').d('重新上传')}</a>
        )}
      </Upload>
    );
  }
}
