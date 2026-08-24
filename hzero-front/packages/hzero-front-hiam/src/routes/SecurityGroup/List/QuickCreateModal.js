/**
 * 安全组 - 快速创建弹窗
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-11-26
 * @LastEditTime: 2019-11-28 11:13
 * @Copyright: Copyright (c) 2018, Hand
 */
import React, { useMemo, useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Button, DataSet, Table, TextField, Switch, Form, IntlField } from 'choerodon-ui/pro';
import { Steps, Card, Tabs } from 'choerodon-ui';
import { isEmpty } from 'lodash';

import { enableRender } from 'utils/renderer';
import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import notification from 'utils/notification';

import { secGrpDS as SecGrpDS, secGrpDetailDS as SecGrpDetailDS } from '@/stores/SecurityGroupDS';
import VisitPermissionTab from '../Detail/VisitPermissionTab';
import DimensionTab from '../Detail/DimensionTab';
import FieldDimensionTab from '../Detail/FieldPermissionTab/ListPage';
import CardTab from '../Detail/CardTab';
import DataPermissionTab from '../Detail/DataPermissionTab';
import styles from './index.less';

const { Step } = Steps;
const { TabPane } = Tabs;

const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

export default observer((props) => {
  const [current, setCurrent] = useState(0);
  const [activeKey, setActiveKey] = useState('visit');
  const [secGrpId, setSecGrpId] = useState(null);
  const [roleId, setRoleId] = useState(null);

  const secGrpDS = useMemo(
    () =>
      new DataSet({
        ...SecGrpDS(),
        selection: 'multiple',
        queryParameter: {},
        transport: {
          read: ({ data, params }) => ({
            url: `${HZERO_IAM}/v1${levelUrl}/sec-grps/quick-create`,
            params: { ...data, ...params, roleId: props.roleId },
            method: 'get',
          }),
          submit: ({ data, dataSet }) => {
            const secData = {
              secGrp: data[0],
              sourceSecGrpIds: dataSet.selected.map((record) => record.get('secGrpId')),
              roleId: props.roleId,
            };
            return {
              url: `${HZERO_IAM}/v1${levelUrl}/sec-grps/quick-create`,
              data: secData,
            };
          },
          destroy: ({ data }) => ({
            url: `${HZERO_IAM}/v1/${levelUrl}/sec-grps`,
            method: 'delete',
            data: data[0],
          }),
        },
        feedback: {
          submitSuccess: () => {},
        },
      }),
    []
  );

  const secGrpDetailDS = useMemo(
    () =>
      new DataSet({
        ...SecGrpDetailDS(secGrpId),
        autoQuery: !!secGrpId,
        transport: {
          read: ({ data, params }) => ({
            url: `${HZERO_IAM}/v1${levelUrl}/sec-grps/${secGrpId}`,
            params: { ...data, ...params },
            method: 'get',
          }),
          submit: ({ data, dataSet }) => {
            const {
              queryParameter: { roleId: dsRoleId },
            } = dataSet;
            return {
              url: `${HZERO_IAM}/v1${levelUrl}/sec-grps`,
              data: {
                ...data[0],
                roleId: dsRoleId,
              },
              method: 'PUT',
            };
          },
        },
      }),
    [secGrpId]
  );

  const columns = useMemo(
    () => [
      { name: 'secGrpCode' },
      { name: 'secGrpName' },
      { name: 'levelMeaning' },
      { name: 'remark' },
      { name: 'createRoleName' },
      { name: 'enabledFlag', width: 90, renderer: ({ value }) => enableRender(value) },
    ],
    []
  );

  /**
   * step2 form dom
   * @returns {*}
   */
  function secGrpForm() {
    return (
      <Form dataSet={secGrpDS} columns={2}>
        <TextField name="secGrpCode" />
        <IntlField name="secGrpName" />
        <IntlField name="remark" />
        <Switch name="enabledFlag" />
      </Form>
    );
  }

  /**
   * step1 table dom
   * @returns {*}
   */
  function secGrpTable() {
    return <Table dataSet={secGrpDS} queryFieldsLimit={2} columns={columns} />;
  }

  /**
   * step3 detail dom
   * @returns {*}
   */
  function secGrpDetail() {
    const { secGrpSource } = props;
    const commonProp = {
      secGrpId,
      isSelf: true,
      roleId,
      secGrpSource,
    };
    return (
      <>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={
            <h3>{intl.get('hiam.securityGroup.view.title.basicInformation').d('基本信息')}</h3>
          }
          loading={false}
        >
          <Form dataSet={secGrpDetailDS} columns={3}>
            <TextField name="secGrpCode" disabled />
            <IntlField name="secGrpName" />
            <IntlField name="remark" />
            <TextField name="secGrpLevelMeaning" disabled />
            <Switch name="enabledFlag" />
          </Form>
        </Card>
        <Tabs defaultActiveKey="visit" animated={false} onChange={handleChangePermissionType}>
          <TabPane
            tab={intl.get('hiam.securityGroup.view.title.tab.visit.permission').d('访问权限')}
            key="visit"
          >
            {activeKey === 'visit' && <VisitPermissionTab {...commonProp} />}
          </TabPane>
          <TabPane
            tab={intl.get('hiam.securityGroup.view.title.tab.field.permission').d('字段权限')}
            key="field"
          >
            {activeKey === 'field' && <FieldDimensionTab {...commonProp} />}
          </TabPane>
          <TabPane
            tab={intl.get('hiam.securityGroup.view.title.tab.workplace').d('工作台配置')}
            key="workplace"
          >
            {activeKey === 'workplace' && <CardTab {...commonProp} />}
          </TabPane>
          <TabPane
            tab={intl.get('hiam.securityGroup.view.title.tab.dimension').d('数据权限维度')}
            key="dimension"
          >
            {activeKey === 'dimension' && <DimensionTab {...commonProp} />}
          </TabPane>
          <TabPane
            tab={intl.get('hiam.securityGroup.view.title.tab.data.permission').d('数据权限')}
            key="data"
          >
            {activeKey === 'data' && <DataPermissionTab {...commonProp} />}
          </TabPane>
        </Tabs>
      </>
    );
  }

  const steps = useMemo(
    () => [
      {
        title: intl.get('hiam.securityGroup.view.title.step.select').d('选择安全组'),
        content: secGrpTable(),
      },
      {
        title: intl.get('hiam.securityGroup.view.title.step.info').d('安全组信息'),
        content: secGrpForm(),
      },
      {
        title: intl.get('hiam.securityGroup.view.title.step.edit').d('编辑安全组'),
        content: secGrpId && secGrpDetail(),
      },
    ],
    [secGrpId, activeKey]
  );

  /**
   * 下一步触发函数
   * @returns {Promise<void>}
   */
  async function next() {
    if (current === 0) {
      if (secGrpDS.selected.length > 0) {
        secGrpDS.create({ tenantId: organizationId });
        setCurrent(current + 1);
      } else {
        notification.error({
          message: intl.get('hiam.securityGroup.view.title.step.select.tip').d('请选择安全组'),
        });
      }
    } else if (current === 1) {
      const res = await secGrpDS.submit();
      if (!isEmpty(res) && res.success) {
        setSecGrpId(res.content[0].secGrpId);
        setRoleId(res.content[0].roleId);
        setCurrent(current + 1);
      }
    }
  }

  /**
   * 上一步触发函数
   * @returns {Promise<void>}
   */
  async function prev() {
    if (current === 1) {
      secGrpDS.reset();
    } else if (current === 2) {
      const record = secGrpDS.current.toJSONData();
      secGrpDS.remove(secGrpDS.current);
      const res = await secGrpDS.submit();
      if (!isEmpty(res) && res.success) {
        secGrpDS.create(record);
      }
    }
    setCurrent(current - 1);
  }

  /**
   * 修改详情页tab activeKey
   * @param key
   */
  function handleChangePermissionType(key) {
    setActiveKey(key);
  }

  /**
   * 取消、关闭弹窗
   * @returns {Promise<void>}
   */
  async function cancel() {
    if (current === 2) {
      secGrpDS.remove(secGrpDS.current);
      const res = await secGrpDS.submit();
      if (!isEmpty(res) && res.success) {
        props.onCancel(current);
      }
    } else {
      props.onCancel(current);
    }
  }

  /**
   * 完成快速创建、保存编辑内容
   * @returns {Promise<boolean>}
   */
  async function done() {
    // eslint-disable-next-line no-unused-expressions
    secGrpDetailDS.current && secGrpDetailDS.current.set('draftFlag', 0);
    secGrpDetailDS.setQueryParameter('roleId', props.roleId);
    const res = await secGrpDetailDS.submit();
    if (!isEmpty(res) && res.failed && res.message) {
      return false;
    }
    if (!isEmpty(res) && res.success) {
      props.onCancel(current);
    } else if (res === false) {
      notification.error({
        message: intl.get('hiam.securityGroup.view.message.required').d('存在必输字段未填写'),
      });
    } else {
      props.onCancel(current);
    }
  }

  return (
    <div>
      <Steps current={current} className={styles['steps-header']}>
        {steps.map((item) => (
          <Step key={item.title} title={item.title} />
        ))}
      </Steps>
      <div className={styles['steps-content']}>{steps[current].content}</div>
      <div className={styles['steps-action']}>
        {current < steps.length - 1 && (
          <Button color="primary" onClick={() => next()}>
            {intl.get('hzero.common.button.next').d('下一步')}
          </Button>
        )}
        {current === steps.length - 1 && (
          <Button color="primary" onClick={() => done()}>
            {intl.get('hiam.securityGroup.model.securityGroup.done').d('完成')}
          </Button>
        )}
        {current > 0 && (
          <Button style={{ marginLeft: 8 }} onClick={() => prev()}>
            {intl.get('hzero.common.button.previous').d('上一步')}
          </Button>
        )}
        <Button onClick={() => cancel()}>{intl.get('hzero.common.button.cancel').d('取消')}</Button>
      </div>
    </div>
  );
});
