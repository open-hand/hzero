/*
 * DataSourceApplication 数据源应用配置
 * @date: 2020-04-22
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import {
  Table,
  DataSet,
  Form,
  TextField,
  Switch,
  Lov,
  Modal,
  ModalContainer,
  Button,
  Spin,
  Select,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import axios from 'axios';

import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender, yesOrNoRender } from 'utils/renderer';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';

import {
  detailDS as detailDs,
  detailTableDS as detailTableDs,
  detailTableDetailDS as detailTableDetailDs,
} from '../../stores/DsRoutesDS';
import Drawer from './Drawer';

const Detail = (props) => {
  let modal;

  const {
    match: { path },
  } = props;

  const detailTableDS = React.useMemo(() => new DataSet(detailTableDs()), []);

  const detailDS = React.useMemo(() => new DataSet(detailDs()), []);

  const detailTableDetailDS = React.useMemo(() => new DataSet(detailTableDetailDs()), []);

  const [loading, setLoading] = React.useState(false);

  React.useEffect(() => {
    const {
      match: {
        params: { dsRouteId },
      },
    } = props;
    detailDS.setQueryParameter('dsRouteId', dsRouteId);
    detailTableDS.setQueryParameter('dsRouteId', dsRouteId);
    if (dsRouteId !== 'create') {
      setLoading(true);
      detailDS.query();
      detailTableDS.query();
      setLoading(false);
    }
  }, []);

  const columns = React.useMemo(
    () => [
      { name: 'datasourceCode' },
      { name: 'masterFlag', width: 150, renderer: ({ value }) => yesOrNoRender(value) },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        renderer: ({ record }) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '数据源应用配置-编辑',
                    },
                  ]}
                  onClick={() => {
                    handleEdit(true, record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.target`,
                      type: 'button',
                      meaning: '数据源应用配置-删除',
                    },
                  ]}
                  onClick={() => {
                    handleDelete(record);
                  }}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators, record, { limit: 3 });
        },
        lock: 'right',
      },
    ],
    []
  );

  const handleEdit = (isEdit, record) => {
    detailTableDetailDS.create({});
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hzero.common.view.title.create').d('新建')
      : intl.get('hzero.common.view.title.edit').d('编辑');
    modal = Modal.open({
      drawer: true,
      key: 'datasource',
      destroyOnClose: true,
      title,
      children: (
        <Drawer
          currentEditData={currentEditData}
          isEdit={isEdit}
          detailTableDetailDS={detailTableDetailDS}
        />
      ),
      okText: intl.get('hzero.common.button.save').d('保存'),
      footer: (
        <>
          <Button color="primary" onClick={() => handleOk(isEdit)}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          {!isEdit && (
            <Button onClick={handleCancel}>
              {intl.get('hzero.common.button.cancel').d('取消')}
            </Button>
          )}
        </>
      ),
    });
  };

  /**
   *
   * @param {*} record
   * 新建的临时删除
   */
  const handleDelete = async (record) => {
    const recordData = record.toData();
    if (recordData && recordData.dsRouteDtlId !== undefined) {
      // ds的destroy方法
      await detailTableDS.delete(record);
    } else {
      detailTableDS.remove(record);
      const detailData = detailTableDS.toData();
      detailTableDS.loadData(detailData);
      notification.success();
    }
  };

  const handleSave = async () => {
    const { history } = props;
    const lineData = detailTableDS.toData();
    const headerData = detailDS.toData()[0];
    const dataSource = {
      ...headerData,
      dsList: lineData.map((item) => {
        // eslint-disable-next-line no-param-reassign
        delete item.datasourceLov;
        return item;
      }),
    };
    delete dataSource.datasourceLov;
    delete dataSource.tenantLov;
    delete dataSource.serviceLov;
    const validate = await detailDS.validate();
    if (validate && (headerData.serviceCode || headerData.tenantNum)) {
      setLoading(true);
      axios({
        url: isTenantRoleLevel()
          ? `${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/ds-routes/save`
          : `${HZERO_PLATFORM}/v1/ds-routes/save`,
        method: 'POST',
        data: dataSource,
      })
        .then((res) => {
          if (res) {
            notification.success();
            history.push(`/hpfm/ds-routes/detail/${res.dsRouteId}`);
            detailDS.setQueryParameter('dsRouteId', res.dsRouteId);
            detailTableDS.setQueryParameter('dsRouteId', res.dsRouteId);
            detailDS.query().then((r) => {
              if (r) {
                detailTableDS.query();
                setLoading(false);
              }
            });
          }
        })
        .catch((err) => {
          notification.error({
            message: err.message,
          });
        });
      setLoading(false);
    } else {
      notification.warning({
        message: intl.get('hpfm.dsRoutes.view.message.title.need').d('请输入服务或租户'),
      });
    }
  };

  /**
   *
   * @param {*} isEdit
   * 如果是新建 直接往里面塞数据
   * 如果是编辑 判断dsId相同，然后替换原来的编辑数据
   */
  const handleOk = async (isEdit) => {
    const validate = await detailTableDetailDS.validate();
    if (validate) {
      if (!isEdit) {
        const addData = detailTableDetailDS.current.toData();
        const addTableData = detailTableDS.toData();
        detailTableDS.loadData([...[addData], ...addTableData]);
      } else {
        const editTableData = detailTableDS.toData();
        const editData = detailTableDetailDS.current.toData();
        detailTableDS.current.set(editData);
        editTableData.forEach((item, index) => {
          if (item.dsRouteDtlId === editData.dsRouteDtlId) {
            // eslint-disable-next-line no-param-reassign
            editTableData.splice(index, 1, editData);
          }
        });
        detailTableDS.loadData(editTableData);
      }
      modal.close();
    }
  };

  const handleCancel = () => {
    modal.close();
    const record = detailTableDetailDS.current;
    detailTableDetailDS.remove(record);
  };

  return (
    <Spin spinning={loading}>
      <Header
        title={intl.get('hpfm.dsRoutes.view.message.title.dsRoutes').d('数据源路由规则')}
        backPath="/hpfm/ds-routes"
      >
        <Spin dataSet={detailDS}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}/save`,
                type: 'button',
                meaning: '数据源应用配置-保存',
              },
            ]}
            icon="save"
            color="primary"
            onClick={handleSave}
            loading={loading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Spin>
      </Header>
      <Content>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hpfm.dsRoutes.view.message.title.info').d('基本信息')}</h3>}
        >
          <Form dataSet={detailDS} columns={3}>
            <Lov name="serviceLov" />
            <Lov name="tenantLov" />
            <Switch name="enabledFlag" />
            <TextField name="url" colSpan={2} />
            <Select name="methodList" multiple />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hpfm.dsRoutes.view.message.title.ds').d('数据源')}</h3>}
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '数据源应用配置-数据源-新建',
              },
            ]}
            onClick={() => handleEdit(false)}
            color="primary"
            icon="add"
            style={{ marginBottom: 12 }}
          >
            {intl.get('hpfm.dsRoutes.button.createDs').d('新建数据源')}
          </ButtonPermission>
          <Table dataSet={detailTableDS} columns={columns} queryBar="none" />
        </Card>
        <ModalContainer location={location} />
      </Content>
    </Spin>
  );
};

export default formatterCollections({ code: ['hpfm.dsRoutes'] })(Detail);
