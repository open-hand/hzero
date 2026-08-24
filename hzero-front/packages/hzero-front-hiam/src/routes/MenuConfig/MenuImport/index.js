/**
 * MenuImport - 导入客户化菜单
 * @date: 2019-8-19
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Upload } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import { API_HOST, HZERO_IAM } from 'utils/config';
import notification from 'utils/notification';
import { getAccessToken, getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

const viewMessagePrompt = 'hiam.menuConfig.view.message';

export default class MenuImport extends React.Component {
  state = {
    uploadLoading: false,
  };

  @Bind()
  getUploadProps() {
    const { onReload = e => e } = this.props;
    const organizationId = getCurrentOrganizationId();
    return {
      name: 'customMenuFile',
      accept: '.json',
      headers: {
        Authorization: `bearer ${getAccessToken()}`,
      },
      action: `${API_HOST}${HZERO_IAM}/hzero/v1/${organizationId}/menus/custom-menu-import`,
      showUploadList: false,
      onChange: ({ file }) => {
        const { status, response } = file;
        this.setState({ uploadLoading: status === 'uploading' });
        if (status === 'done' && response === 'success') {
          notification.success({
            message: intl.get(`${viewMessagePrompt}.importSuccess`).d('导入成功'),
          });
          onReload();
        } else if (status === 'error') {
          notification.error({ message: response.message });
        }
      },
      style: {
        margin: '0 12px',
      },
    };
  }

  render() {
    const { path } = this.props;
    const { uploadLoading } = this.state;
    const uploadProps = this.getUploadProps();
    return (
      <Upload {...uploadProps}>
        <ButtonPermission
          permissionList={[
            {
              code: `${path}.button.importMenu`,
              type: 'button',
              meaning: '菜单配置-导入客户化菜单',
            },
          ]}
          className="label-btn"
          icon="upload"
          loading={uploadLoading}
        >
          {intl.get(`${viewMessagePrompt}.title.importMenu`).d('导入客户化菜单')}
        </ButtonPermission>
      </Upload>
    );
  }
}
