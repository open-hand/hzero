/**
 * 银行账户
 * @since 2020-02-12
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, ModalContainer, Table, Modal } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import Drawer from './Drawer';
import {
  tableDs as TableDs,
  detailDs as DetailDs,
  balanceDs as BalanceDs,
} from '../../stores/BankAccountDS';
import BalanceDrawer from './BalanceDrawer';

const BankAccount = ({ match: { path } }) => {
  const tableDs = React.useMemo(() => new DataSet(TableDs()), []);

  const detailDs = React.useMemo(() => new DataSet(DetailDs()), []);

  const balanceDs = React.useMemo(() => new DataSet(BalanceDs()), []);

  const columns = React.useMemo(
    () => [
      { name: 'bankMarkMeaning', width: 220 },
      { name: 'accountNumber', width: 220 },
      { name: 'bankCode' },
      { name: 'bankNumber', width: 220 },
      { name: 'accountName', width: 220 },
      { name: 'groupNumber', width: 220 },
      { name: 'bankName', width: 220 },
      {
        name: 'childrenFlag',
        renderer: ({ value }) => enableRender(value),
        align: 'left',
        width: 130,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
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
                      code: `${path}/edit`,
                      type: 'button',
                      meaning: '银行账户-编辑',
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
              key: 'balance',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/balance`,
                      type: 'button',
                      meaning: '银行账户-余额',
                    },
                  ]}
                  onClick={() => {
                    handleBalance(record);
                  }}
                >
                  {intl.get('hebk.bankAccount.button.balance').d('余额')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hebk.bankAccount.button.balance').d('余额'),
            }
          );
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  const handleEdit = (isEdit, record) => {
    detailDs.create({});
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hzero.common.view.title.create').d('新建')
      : intl.get('hzero.common.view.title.edit').d('编辑');
    Modal.open({
      drawer: true,
      key: 'bankAccount',
      destroyOnClose: true,
      closable: true,
      title,
      children: <Drawer currentEditData={currentEditData} isEdit={isEdit} detailDs={detailDs} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: handleOk,
      onCancel: () => {
        detailDs.removeAll();
      },
      onClose: () => {
        detailDs.removeAll();
      },
    });
  };

  const handleBalance = (record) => {
    balanceDs.create({});
    const currentBalanceData = record && record.toData();
    const title = intl.get('hebk.bankAccount.view.title.balance').d('余额');
    Modal.open({
      drawer: true,
      key: 'balance',
      destroyOnClose: true,
      closable: true,
      title,
      children: <BalanceDrawer currentBalanceData={currentBalanceData} balanceDs={balanceDs} />,
      onOk: () => {
        balanceDs.removeAll();
      },
      onCancel: () => {
        balanceDs.removeAll();
      },
      onClose: () => {
        balanceDs.removeAll();
      },
    });
  };

  const handleOk = async () => {
    try {
      const validate = await detailDs.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    tableDs.query();
  };

  return (
    <>
      <Header title={intl.get('hebk.banAccount.view.message.title.backAccount').d('银行账户')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/create`,
              type: 'button',
              meaning: '银行账户-新建',
            },
          ]}
          icon="add"
          color="primary"
          onClick={() => handleEdit(false)}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
        <ModalContainer location={location} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hebk.bankAccount'] })(BankAccount);
