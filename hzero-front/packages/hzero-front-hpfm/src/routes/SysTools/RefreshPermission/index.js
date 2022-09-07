/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/20
 * @copyright 2019 ® HAND
 */

import React from 'react';

import { Card, Popconfirm } from 'choerodon-ui';
import { DataSet, Form, Icon, TextArea, TextField, Tooltip, Switch, Lov } from 'choerodon-ui/pro';
import axios from 'axios';
import { isUndefined } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import {
  sysToolsPermissionAssignInheritRoleDS,
  sysToolsPermissionAxiosConfig,
  sysToolsPermissionFreshDS,
} from '@/stores/sysToolsDS';

import { titleIconStyle } from '../utils';

function buildRefreshPermissionLoadingName(module, code) {
  return `refresh-permission-${module}-${code}-loading`;
}

const RefreshPermission = ({ match }) => {
  const freshDs = React.useMemo(() => new DataSet(sysToolsPermissionFreshDS()), []);
  const assignInheritRoleDs = React.useMemo(
    () => new DataSet(sysToolsPermissionAssignInheritRoleDS()),
    []
  );
  const refreshPermissionAxionsConfig = React.useMemo(() => sysToolsPermissionAxiosConfig(), []);
  const [loading, setLoading] = React.useState({});
  // make sure loadingProxy is always same, so loading is always newest
  const loadingProxy = React.useMemo(() => ({}), []);
  loadingProxy.loading = loading;
  const refreshPermissionProxyCall = React.useCallback((module, code, data) => {
    const newLoading = {
      ...loadingProxy.loading,
      [buildRefreshPermissionLoadingName(module, code)]: true,
    };
    setLoading(newLoading);
    if (refreshPermissionAxionsConfig[module] && refreshPermissionAxionsConfig[module][code]) {
      axios(refreshPermissionAxionsConfig[module][code](data))
        .then((res) => {
          if (isUndefined(res)) {
            setLoading({
              ...loadingProxy.loading,
              [buildRefreshPermissionLoadingName(module, code)]: false,
            });
            notification.success();
          } else {
            // call error, let always loading
            // maybe axios config had notification
            notification.error();
            setLoading({
              ...loadingProxy.loading,
              [buildRefreshPermissionLoadingName(module, code)]: false,
            });
          }
        })
        .catch((e) => {
          setLoading({
            ...loadingProxy.loading,
            [buildRefreshPermissionLoadingName(module, code)]: false,
          });
          if (e && e.message) {
            notification.error({ message: e.message });
          }
        });
    } else {
      // TODO: maybe no arrive020
      notification.warning({
        message: intl.get('hpfm.sysTools.view.message.no-config').d('没有配置'),
        description: intl
          .get('hpfm.sysTools.view.message.no-config-detail', { module, code })
          .d(`没有找到对应 module: ${module},code: ${code} 的配置`),
      });
      // no config for module, code
      setLoading({
        ...loadingProxy.loading,
        [buildRefreshPermissionLoadingName(module, code)]: false,
      });
    }
  }, []);
  const handleFreshSubmit = React.useCallback(async () => {
    setLoading({
      ...loadingProxy.loading,
      [buildRefreshPermissionLoadingName('hiam', 'fresh')]: true,
    });
    const valid = await freshDs.validate();
    if (valid) {
      try {
        await freshDs.submit();
      } catch (e) {
        // do nothing, ds noticed
      }
    }
    setLoading({
      ...loadingProxy.loading,
      [buildRefreshPermissionLoadingName('hiam', 'fresh')]: false,
    });
  }, [freshDs]);
  // const handleAssignInheritRoleSubmit = React.useCallback(async () => {
  //   setLoading({
  //     ...loadingProxy.loading,
  //     [buildRefreshPermissionLoadingName('hiam', 'assign-inherit-role')]: true,
  //   });
  //   const valid = await assignInheritRoleDs.validate();

  //   // console.log(assignInheritRoleDs.records[0].get('roleLevelPaths'));
  //   if (valid) {
  //     axios(refreshPermissionAxionsConfig.hiam['assign-inherit-role']({roleLevelPaths: assignInheritRoleDs.records[0]&&assignInheritRoleDs.records[0].get('roleLevelPaths')})).then(res => {
  //       console.log(res);
  //       if (res.status === 204) {
  //         setLoading({
  //           ...loadingProxy.loading,
  //           [buildRefreshPermissionLoadingName('hiam', 'assign-inherit-role')]: false,
  //         });
  //         notification.success();
  //       } else {
  //         // call error, let always loading
  //         // maybe axios config had notification
  //         notification.error();
  //       }
  //     });
  //   }
  // }, [assignInheritRoleDs]);

  const handelChange = (record) => {
    freshDs.current.set('metaVersion', (record && record.version) || '');
  };

  return (
    <>
      <Card
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={
          <h3>
            {intl.get('hpfm.sysTools.view.message.title.rp.hiam.fresh').d('手动刷新表IAM权限')}
          </h3>
        }
      >
        <Popconfirm
          title={intl
            .get('hpfm.sysTools.view.message.refreshPermission.fresh')
            .d('确认刷新表IAM权限')}
          onConfirm={() => {
            handleFreshSubmit();
          }}
        >
          <ButtonPermission
            icon="sync"
            permissionList={[
              {
                code: `${match.path}.button.rp.hiam.fresh`,
                type: 'button',
                meaning: '系统工具-刷新权限-手动刷新表IAM权限',
              },
            ]}
            loading={loading[buildRefreshPermissionLoadingName('hiam', 'fresh')]}
          >
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </ButtonPermission>
        </Popconfirm>
        <Form columns={3} dataSet={freshDs} labelWidth={140}>
          <Lov name="serviceName" colSpan={2} onChange={handelChange} />
          <TextField name="metaVersion" colSpan={2} newLine />
          <Switch
            newLine
            colSpan={2}
            name="cleanPermission"
            help={intl
              .get('hpfm.sysTools.view.message.title.rp.hiam.cp.tooltip')
              .d('将删除权限表中该服务不存在的权限')}
            showHelp="tooltip"
            label={intl
              .get('hpfm.sysTools.model.refreshPermission.cleanPermission')
              .d('清除过期权限')}
          />
        </Form>
      </Card>
      <Card
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={
          <h3>
            {intl.get('hpfm.sysTools.view.message.title.rp.hiam.air').d('角色继承树分配权限')}
            <Tooltip
              title={intl
                .get('hpfm.sysTools.view.message.title.rp.hiam.air.tooltip')
                .d('不传角色编码则默认处理内置角色')}
            >
              <Icon style={titleIconStyle} type="info" />
            </Tooltip>
          </h3>
        }
      >
        <Popconfirm
          title={intl
            .get('hpfm.sysTools.view.message.refreshPermission.air')
            .d('确认刷新角色继承树分配权限')}
          onConfirm={() => {
            // handleAssignInheritRoleSubmit();
            refreshPermissionProxyCall('hiam', 'assign-inherit-role', {
              roleLevelPaths:
                assignInheritRoleDs.records[0] &&
                assignInheritRoleDs.records[0].get('roleLevelPaths'),
            });
          }}
        >
          <ButtonPermission
            icon="sync"
            permissionList={[
              {
                code: `${match.path}.button.rp.hiam.air`,
                type: 'button',
                meaning: '系统工具-刷新权限-角色继承树分配权限',
              },
            ]}
            loading={loading[buildRefreshPermissionLoadingName('hiam', 'assign-inherit-role')]}
          >
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </ButtonPermission>
        </Popconfirm>
        <Form columns={3} dataSet={assignInheritRoleDs} labelWidth={140}>
          <TextArea
            colSpan={2}
            name="roleLevelPaths"
            help={intl
              .get('hpfm.sysTools.view.message.roleLevelPaths.tooltip')
              .d('多个路径用逗号隔开，不传角色路径则默认处理内置角色')}
            showHelp="tooltip"
            label={intl.get('hpfm.sysTools.model.refreshPermission.roleLevelPaths').d('角色路径')}
          />
        </Form>
      </Card>
      {/* <Card
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={
          <h3>
            {intl
              .get('hpfm.sysTools.view.message.title.rp.hiam.assignSuperRole')
              .d('将权限集挂到超级管理员下')}
          </h3>
        }
      >
        <Popconfirm
          title={intl
            .get('hpfm.sysTools.view.message.refreshPermission.assignSuperRole')
            .d('确认将权限集挂到超级管理员下')}
          onConfirm={() => {
            refreshPermissionProxyCall('hiam', 'assign-super-role');
          }}
        >
          <ButtonPermission
            icon="sync"
            permissionList={[
              {
                code: `${match.path}.button.rp.hiam.assignSuperRole`,
                type: 'button',
                meaning: '系统工具-刷新权限-将权限集挂到超级管理员下',
              },
            ]}
            loading={loading[buildRefreshPermissionLoadingName('hiam', 'assign-super-role')]}
          >
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </ButtonPermission>
        </Popconfirm>
      </Card> */}
    </>
  );
};

export default RefreshPermission;
