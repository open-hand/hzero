/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/20
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { Card } from 'choerodon-ui';
import { DataSet, Form, Icon, TextField, Tooltip } from 'choerodon-ui/pro';
import axios from 'axios';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import {
  sysToolsPasswordPolicyAxiosConfig,
  sysToolsPasswordPolicyEncryptDS,
} from '@/stores/sysToolsDS';

import { titleIconStyle } from '../utils';

function buildPasswordPolicyLoadingName(module, code) {
  return `password-policy-${module}-${code}-loading`;
}

const PasswordPolicy = ({ match }) => {
  // 查询出来的公钥
  const [publicKey, setPublicKey] = React.useState('');
  const encryptDs = React.useMemo(() => new DataSet(sysToolsPasswordPolicyEncryptDS()), []);
  const [loading, setLoading] = React.useState({});
  // make sure loadingProxy is always same, so loading is always newest
  const loadingProxy = React.useMemo(() => ({}), []);
  loadingProxy.loading = loading;
  const passwordPolicyAxionsConfig = React.useMemo(() => sysToolsPasswordPolicyAxiosConfig(), []);
  const getPublicKey = React.useCallback(() => {
    const module = 'hpfm';
    const code = 'public-key';
    const newLoading = {
      ...loadingProxy.loading,
      [buildPasswordPolicyLoadingName(module, code)]: true,
    };
    setLoading(newLoading);
    if (passwordPolicyAxionsConfig[module] && passwordPolicyAxionsConfig[module][code]) {
      axios(passwordPolicyAxionsConfig[module][code]())
        .then(res => {
          if (res) {
            setLoading({
              ...loadingProxy.loading,
              [buildPasswordPolicyLoadingName(module, code)]: false,
            });
            setPublicKey(res.publicKey);
            notification.success();
          } else {
            // call error, let always loading
            // maybe axios config had notification
            setLoading({
              ...loadingProxy.loading,
              [buildPasswordPolicyLoadingName(module, code)]: false,
            });
            notification.error();
          }
        })
        .catch(e => {
          setLoading({
            ...loadingProxy.loading,
            [buildPasswordPolicyLoadingName(module, code)]: false,
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
          .get('hpfm.sysTools.view.message.no-config-detail', {
            module,
            code,
          })
          .d(`没有找到对应 module: ${module},code: ${code} 的配置`),
      });
      // no config for module, code
      setLoading({
        ...loadingProxy.loading,
        [buildPasswordPolicyLoadingName(module, code)]: false,
      });
    }
  }, [setPublicKey]);
  // const encryptedPassRef = React.useRef();
  const handleEncryptSubmit = React.useCallback(async () => {
    setLoading({
      ...loadingProxy.loading,
      [buildPasswordPolicyLoadingName('hpfm', 'encrypt')]: true,
    });
    const valid = await encryptDs.validate();
    if (valid) {
      try {
        const data = await encryptDs.submit();
        encryptDs.current.set('encryptPass', data.content[0].encryptPass);
      } catch (e) {
        // do nothing, ds noticed
      }
    }
    setLoading({
      ...loadingProxy.loading,
      [buildPasswordPolicyLoadingName('hpfm', 'encrypt')]: false,
    });
  }, [encryptDs]);
  return (
    <>
      <Card
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={
          <h3>
            {intl.get('hpfm.sysTools.view.message.title.pp.hpfm.encrypt').d('获取加密后的密码')}
            <Tooltip
              title={intl
                .get('hpfm.sysTools.view.message.title.pp.hpfm.ec.tooltip')
                .d('将使用 RSA 非对称加密算法对密码加密')}
            >
              <Icon style={titleIconStyle} type="info" />
            </Tooltip>
          </h3>
        }
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${match.path}.button.pp.hpfm.encrypt`,
              type: 'button',
              meaning: '系统工具-刷新权限-加密',
            },
          ]}
          loading={loading[buildPasswordPolicyLoadingName('hpfm', 'encrypt')]}
          onClick={() => {
            handleEncryptSubmit();
          }}
        >
          {intl.get('hpfm.sysTools.view.button.encrypt').d('加密')}
        </ButtonPermission>
        <Form columns={3} dataSet={encryptDs} labelWidth={120}>
          <TextField
            colSpan={2}
            name="pass"
            label={intl.get('hpfm.sysTools.model.passwordPolicy.pass.label').d('请输入明文密码')}
          />
          <div style={{ display: 'none' }}>{/* placeholder for layout */}</div>
          <TextField
            readOnly
            colSpan={2}
            name="encryptPass"
            label={intl
              .get('hpfm.sysTools.model.passwordPolicy.encryptPass.label')
              .d('加密后的密码')}
          />
        </Form>
      </Card>
      <Card
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={
          <h3>
            {intl.get('hpfm.sysTools.view.message.title.pp.hpfm.publicKey').d('获取加密公钥')}
            <Tooltip
              title={intl
                .get('hpfm.sysTools.view.message.title.pp.hpfm.pk.tooltip')
                .d('RSA 非对称加密算法的公钥')}
            >
              <Icon style={titleIconStyle} type="info" />
            </Tooltip>
          </h3>
        }
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${match.path}.button.rp.hpfm.publicKey`,
              type: 'button',
              meaning: '系统工具-刷新权限-获取加密公钥',
            },
          ]}
          loading={loading[buildPasswordPolicyLoadingName('hpfm', 'public-key')]}
          onClick={getPublicKey}
        >
          {intl.get('hpfm.sysTools.view.getPublicKey').d('获取公钥')}
        </ButtonPermission>
        <Form columns={3} labelWidth={120}>
          <TextField
            colSpan={2}
            label={intl.get('hpfm.sysTools.model.passwordPolicy.publicKey.label').d('密码公钥')}
            value={publicKey}
          />
        </Form>
      </Card>
    </>
  );
};

export default PasswordPolicy;
