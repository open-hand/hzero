/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/21
 * @copyright 2019 ® HAND
 */

import intl from 'utils/intl';
import React from 'react';
import { HZERO_IAM, HZERO_PLATFORM } from 'utils/config';
import { Icon, Tooltip } from 'hzero-ui';

export const sysToolsCacheAxiosConfig = () => ({
  hpfm: {
    'code-rule': () => ({
      url: `${HZERO_PLATFORM}/v1/tool/cache/code-rule`,
      method: 'POST',
      data: {},
    }),
    config: () => ({
      url: `${HZERO_PLATFORM}/v1/tool/cache/config`,
      method: 'POST',
      data: {},
    }),
    datasource: () => ({
      url: `${HZERO_PLATFORM}/v1/tool/cache/datasource`,
      method: 'POST',
      data: {},
    }),
    customize: () => ({
      url: `${HZERO_PLATFORM}/v1/ui-customize/refreshCache`,
      method: 'get',
      data: {},
    }),
    'entity-table': () => ({
      url: `${HZERO_PLATFORM}/v1/tool/cache/entity-table`,
      method: 'POST',
      data: {},
    }),
    'permission-range': () => ({
      url: `${HZERO_PLATFORM}/v1/tool/cache/permission-range`,
      method: 'POST',
      data: {},
    }),
    profile: () => ({
      url: `${HZERO_PLATFORM}/v1/tool/cache/profile`,
      method: 'POST',
      data: {},
    }),
    prompt: () => ({
      url: `${HZERO_PLATFORM}/v1/tool/cache/prompt`,
      method: 'POST',
      data: {},
    }),
    message: () => ({
      url: `${HZERO_PLATFORM}/v1/tool/cache/return-message`,
      method: 'POST',
      data: {},
    }),
  },
  hiam: {
    client: () => ({
      url: `${HZERO_IAM}/v1/tool/cache/client`,
      data: {},
      method: 'POST',
    }),
    ldap: () => ({
      url: `${HZERO_IAM}/v1/tool/cache/ldap`,
      data: {},
      method: 'POST',
    }),
    user: () => ({
      url: `${HZERO_IAM}/v1/tool/cache/user`,
      data: {},
      method: 'POST',
    }),
    domain: () => ({
      url: `${HZERO_IAM}/v1/tool/cache/domain`,
      data: {},
      method: 'POST',
    }),
    doctype: () => ({
      url: `${HZERO_IAM}/v1/tool/cache/doc-type`,
      data: {},
      method: 'POST',
    }),
    'open-app': () => ({
      url: `${HZERO_IAM}/v1/tool/cache/open-app`,
      data: {},
      method: 'POST',
    }),
    'password-policy': () => ({
      url: `${HZERO_IAM}/v1/tool/cache/password-policy`,
      data: {},
      method: 'POST',
    }),
  },
});

export const sysToolsPermissionAxiosConfig = () => ({
  hiam: {
    'assign-super-role': () => ({
      url: `${HZERO_IAM}/v1/tool/permission/assign-super-role`,
      method: 'POST',
      data: {},
    }),
    'assign-inherit-role': (data) => ({
      url: `${HZERO_IAM}/v1/tool/permission/assign-inherit-role`,
      method: 'POST',
      params: data || {},
    }),
    fresh: (data) => ({
      url: `${HZERO_IAM}/v1/tool/permission/fresh`,
      method: 'POST',
      data: data || {},
    }),
  },
});

export const sysToolsPermissionFreshDS = () => ({
  autoQueryAfterSubmit: false,
  autoCreate: true,
  fields: [
    {
      label: intl.get('hpfm.sysTools.model.refreshPermission.serviceName').d('服务名'),
      name: 'serviceName',
      type: 'object',
      lovCode: 'HADM.INSTANCE',
      required: true,
    },
    {
      // eslint-disable-next-line react/react-in-jsx-scope
      label: (
        <>
          {intl.get('hpfm.sysTools.model.refreshPermission.metaVersion').d('服务标记版本')}
          <Tooltip
            title={intl
              .get('hpfm.sysTools.view.message.title.metaVersion.tooptip')
              .d('不输入版本是刷新最新版本')}
          >
            <Icon type="question-circle" />
          </Tooltip>
        </>
      ),
      name: 'metaVersion',
      type: 'string',
    },
    {
      // boolean 值不需要 required(一定会有值)
      label: intl.get('hpfm.sysTools.model.refreshPermission.cleanPermission').d('清除过期权限'),
      name: 'cleanPermission',
      type: 'boolean',
      defaultValue: true,
    },
  ],
  transport: {
    create: ({ data }) => {
      const [{ __id, _status, serviceName, ...rest }] = data;
      const url = `${HZERO_IAM}/v1/tool/permission/fresh`;
      return {
        url,
        method: 'POST',
        params: { ...rest, serviceName: serviceName.service },
        data: {},
      };
    },
  },
});

export const sysToolsPermissionAssignInheritRoleDS = () => ({
  autoQueryAfterSubmit: false,
  fields: [
    {
      label: intl.get('hpfm.sysTools.model.refreshPermission.roleLevelPaths').d('角色路径'),
      name: 'roleLevelPaths',
      type: 'string',
    },
  ],
  transport: {
    create: ({ data }) => {
      const [{ __id, _status, ...rest }] = data;
      const url = `${HZERO_IAM}/v1/tool/permission/assign-inherit-role`;
      return {
        url,
        method: 'POST',
        params: rest,
        data: {},
      };
    },
  },
});

export const sysToolsPasswordPolicyEncryptDS = () => ({
  autoQueryAfterSubmit: false,
  autoCreate: true,
  fields: [
    {
      label: intl.get('hpfm.sysTools.model.passwordPolicy.pass').d('密码'),
      name: 'pass',
      type: 'string',
      required: true,
    },
    {
      label: intl.get('hpfm.sysTools.model.passwordPolicy.encryptPass').d('加密密码'),
      name: 'encryptPass',
      type: 'string',
      readOnly: true,
    },
  ],
  transport: {
    create: ({ data }) => {
      const [{ __id, _status, ...rest }] = data;
      const url = `${HZERO_PLATFORM}/v1/tool/pass/encrypt`;
      return {
        url,
        method: 'POST',
        params: rest,
        data: {},
      };
    },
  },
});

export const sysToolsPasswordPolicyAxiosConfig = () => ({
  hpfm: {
    'public-key': () => ({
      url: `${HZERO_PLATFORM}/v1/tool/pass/public-key`,
      method: 'GET',
    }),
  },
});
