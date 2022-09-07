import React from 'react';
import { Form, Table, DataSet, Switch, Select, Lov, Row, Col, Modal } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { operatorRender } from 'utils/renderer';
import { DETAIL_CARD_CLASSNAME, TABLE_OPERATOR_CLASSNAME } from 'utils/constants';
import { isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';

import LineForm from './LineForm';

import { headerDS, lineDS } from '../stores/editDS';

const Edit = ({ match, history }) => {
  const {
    params: { apiConfigId },
  } = match;
  const isCreate = apiConfigId === 'create';
  const isTenant = isTenantRoleLevel();

  React.useEffect(() => {
    if (isCreate) {
      editHeaderDS.create();
    } else {
      editHeaderDS.query();
    }
  }, [isCreate]);

  const { editHeaderDS } = React.useMemo(
    () => ({
      editHeaderDS: new DataSet(headerDS(apiConfigId)),
    }),
    [apiConfigId]
  );

  const { editLineDS } = React.useMemo(
    () => ({
      editLineDS: new DataSet(lineDS(apiConfigId, editHeaderDS)),
    }),
    [editHeaderDS, apiConfigId]
  );

  const handleSave = async () => {
    const validate = await editHeaderDS.validate();
    if (validate) {
      const res = await editHeaderDS.submit();
      if (res && res.success) {
        const { content } = res;
        if (isCreate) {
          await history.push(`/hivc/api-config/edit/${content[0].apiConfigId}`);
        } else {
          editHeaderDS.query();
        }
      }
    }
  };

  const handleLineOk = async () => {
    const validate = await editLineDS.validate();
    if (validate) {
      const res = await editLineDS.submit();
      if (res && res.success) {
        await editLineDS.query();
      }
    }
  };

  const handleCreateLine = (isCreateLine, record) => {
    // 重置
    if (isCreateLine) {
      editLineDS.create();
    }
    Modal.open({
      closable: true,
      key: 'line-form',
      title: isCreateLine
        ? intl.get('hivc.apiConfig.view.title.line.create').d('新建配置行')
        : intl.get('hivc.apiConfig.view.title.line.edit').d('编辑配置行'),
      drawer: true,
      style: {
        width: 520,
      },
      children: <LineForm record={record || editLineDS.current} />,
      onOk: handleLineOk,
      onCancel: () => {
        editLineDS.reset();
      },
      onClose: () => {
        editLineDS.reset();
      },
    });
  };

  const handleDeleteLine = async (record) => {
    try {
      const res = await editLineDS.delete(record, null);
      if (res && res.failed && res.message) {
        notification.error({
          message: res.message,
        });
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
  };

  const columns = React.useMemo(() =>
    [
      {
        name: 'orderSeq',
        width: 200,
        align: 'left',
      },
      {
        name: 'serverCode',
        width: 200,
        align: 'left',
      },
      {
        name: 'interfaceCode',
        align: 'left',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const actions = [
            {
              key: '',
              title: '',
              len: 2,
              ele: (
                <ButtonPermission
                  type="text"
                  onClick={() => handleCreateLine(false, record)}
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '发票接口类型-编辑',
                    },
                  ]}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
            },
            {
              key: '',
              title: '',
              len: 2,
              ele: (
                <ButtonPermission
                  type="text"
                  onClick={() => handleDeleteLine(record)}
                  permissionList={[
                    {
                      code: `${match.path}.button.delete`,
                      type: 'button',
                      meaning: '发票接口类型-删除',
                    },
                  ]}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
            },
          ];
          return operatorRender(actions);
        },
      },
    ].filter(Boolean)
  );

  return (
    <>
      <Header
        title={
          isCreate
            ? intl.get('hivc.apiConfig.view.title.create').d('发票接口配置-新建')
            : intl.get('hivc.apiConfig.view.title.edit').d('发票接口配置-编辑')
        }
        backPath="/hivc/api-config/list"
      >
        <ButtonPermission type="c7n-pro" color="primary" icon="save" onClick={handleSave}>
          {intl.get('hzero.common.button.save').d('保存')}
        </ButtonPermission>
      </Header>
      <Content>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hivc.apiConfig.view.title.detail.header').d('配置定义')}</h3>}
        >
          <Form columns={3} dataSet={editHeaderDS}>
            {!isTenant && <Lov name="tenantLov" placeholder="" />}
            <Select name="apiType" />
            <Switch name="combineFlag" />
            <Switch name="defaultFlag" />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hivc.apiConfig.view.title.detail.line').d('配置行')}</h3>}
        >
          <Row type="flex" justify="end">
            <Col>
              <div className={TABLE_OPERATOR_CLASSNAME}>
                <ButtonPermission
                  type="c7n-pro"
                  color="primary"
                  onClick={() => handleCreateLine(true)}
                  disabled={isCreate}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </ButtonPermission>
              </div>
            </Col>
          </Row>
          <Table columns={columns} dataSet={editLineDS} />
        </Card>
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hivc.apiConfig'],
})(Edit);
