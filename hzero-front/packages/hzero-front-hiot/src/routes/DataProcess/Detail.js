/**
 * 数据处理 - 详情页
 * @date: 2020-07-20
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo, useState } from 'react';
import {
  DataSet,
  Table,
  Button,
  Form,
  Spin,
  TextArea,
  Lov,
  IntlField,
  TextField,
} from 'choerodon-ui/pro';
import { Row, Col, Card } from 'choerodon-ui';
import queryString from 'querystring';
import { isNil } from 'lodash';
import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import { getCurrentOrganizationId } from 'utils/utils';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import styles from './index.less';
import { detailDS, deviceTableDS, createDeviceDS } from '../../stores/DataProcessDS';

const currentOrganizationId = getCurrentOrganizationId();
const Detail = (props) => {
  const {
    match: { params = {} },
    location: { search },
  } = props;
  const { action } = params;
  const { id } = queryString.parse(search.substring(1));

  const detailDs = useMemo(() => new DataSet(detailDS()), []);
  const tableDs = useMemo(() => new DataSet(deviceTableDS()), []);
  const createDeviceDs = useMemo(() => new DataSet(createDeviceDS()), []);
  const [isEdit, setIsEdit] = useState(action === 'edit');
  const [processId, setProcessId] = useState(id);

  const lovDs = useMemo(
    () =>
      new DataSet({
        autoCreate: true,
        fields: [
          {
            name: 'device',
            type: 'object',
            lovCode: 'HIOT.PROCESS_DATA_TOPIC',
            lovPara: { tenantId: currentOrganizationId, dataProcessId: processId },
            multiple: true,
          },
        ],
      }),
    []
  );

  const {
    match: { path },
  } = props;

  React.useEffect(() => {
    tableDs.setQueryParameter('tenantId', currentOrganizationId);
    if (isEdit) {
      tableDs.dataProcessId = id;
      tableDs.query();
      queryData();
    } else {
      detailDs.create({ tenantId: currentOrganizationId }, 0);
    }
  }, []);

  const queryData = async () => {
    try {
      detailDs.setQueryParameter('dataProcessId', id);
      await detailDs.query();
    } catch (e) {
      //
    }
  };

  const columns = useMemo(
    () => [
      { name: 'deviceCode' },
      { name: 'deviceName' },
      { name: 'guid' },
      {
        name: 'receivedTopic',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'delete',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.delete`,
                    type: 'button',
                    meaning: '数据处理详情页-删除',
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
          });

          return operatorRender(operators);
        },
      },
    ],
    []
  );

  // 保存
  const handleSave = async () => {
    try {
      const validate = await detailDs.validate();
      let dataId = '';
      if (!validate) {
        return false;
      } else {
        await detailDs.submit().then((res) => {
          if (!isEdit) {
            const { dataProcessId } = res.content[0] || {};
            dataId = dataProcessId;
            setProcessId(dataProcessId);
          }
        });
      }
      if (isEdit) {
        detailDs.query();
      } else {
        setIsEdit(true);
        detailDs.setQueryParameter('dataProcessId', dataId);
        detailDs.query();
        tableDs.dataProcessId = dataId;
        await tableDs.query();
      }
    } catch {
      return false;
    }
  };

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  // 添加应用设备/网关
  const handleAddDevice = async (list) => {
    if (!isNil(list)) {
      const dataList = list.map((item) => {
        return {
          ...item,
          dataProcessId: processId,
        };
      });
      const data = { processTopicList: dataList };
      createDeviceDs.dataProcessId = processId;
      createDeviceDs.create(data, 0);
      try {
        const validate = await createDeviceDs.submit();
        if (!validate) {
          return false;
        }
        tableDs.query();
      } catch {
        return false;
      }
    }
  };

  return (
    <>
      <Header
        title={
          isEdit
            ? intl.get('hiot.dataProcess.view.message.editDataProcess').d('编辑数据处理规则')
            : intl.get('hiot.dataProcess.view.message.createDataProcess').d('新建数据处理规则')
        }
        backPath="/hiot/data-process/list"
      >
        <Button color="primary" icon="save" onClick={handleSave}>
          {intl.get('hzero.common.button.save').d('保存')}
        </Button>
      </Header>
      <Content>
        <Card
          bordered={false}
          title={intl.get('hiot.dataProcess.view.message.dataAbout').d('数据处理信息')}
        >
          <Row style={{ margin: '0 15px' }}>
            <Col span={16}>
              <Spin dataSet={detailDs}>
                <Form dataSet={detailDs} columns={2}>
                  <TextField name="dataProcessCode" disabled={isEdit} />
                  <IntlField name="dataProcessName" />
                  {/* <Lov name="preProcessLov" /> */}
                  <Lov name="dataSinkLov" />
                  <TextArea name="remarks" colSpan={2} />
                </Form>
              </Spin>
            </Col>
          </Row>
        </Card>

        {isEdit && (
          <Card
            bordered={false}
            className={styles['data-process-card']}
            title={intl.get('hiot.dataProcess.view.message.deviceTitle').d('应用设备/网关')}
            extra={
              <div>
                <Lov
                  dataSet={lovDs}
                  name="device"
                  mode="button"
                  clearButton={false}
                  icon="add"
                  noCache
                  className={styles['detail-table-button']}
                  onChange={handleAddDevice}
                >
                  {intl.get('hiot.dataProcess.view.button.add').d('添加')}
                </Lov>
              </div>
            }
          >
            <div className={styles['detail-table-wrap']}>
              <Table dataSet={tableDs} columns={columns} queryFieldsLimit={2} />
            </div>
          </Card>
        )}
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hiot.dataProcess', 'hiot.common'] })(Detail);
