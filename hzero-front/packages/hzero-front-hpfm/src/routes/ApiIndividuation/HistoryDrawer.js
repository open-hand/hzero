import React from 'react';
import { Form, DataSet, Table, Modal, Output, CodeArea } from 'choerodon-ui/pro';
import { historyDS, historyRollBackDS } from '@/stores/apiIndividuationDS';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

const Drawer = (props) => {
  const historyDs = React.useMemo(() => new DataSet(historyDS()), []);
  const historyRollBackDs = React.useMemo(() => new DataSet(historyRollBackDS()), []);

  const { path, id } = props;
  React.useEffect(() => {
    historyDs.setQueryParameter('customizeId', id);
    historyDs.query();
  }, []);
  /**
   * 打开历史版本
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  const handleDetail = (record) => {
    Modal.open({
      closable: true,
      key: 'historyDetail',
      title: intl.get('hpfm.apiIndividuation.view.title.historyDetail').d('历史版本详情'),
      drawer: true,
      style: {
        width: 700,
      },
      children: (
        <Form record={record}>
          <Output name="contentType" disabled />
          <Output
            name="versionNumber"
            renderer={({ text }) => {
              return `v${text}.0`;
            }}
            disabled
          />
          <Output name="lastUpdateDate" disabled />
          <CodeArea
            name="customizeContent"
            options={{ lineWrapping: true }}
            style={{ height: '100%' }}
            disabled
          />
        </Form>
      ),
      footer: null,
    });
  };

  // /**
  //  * 删除历史版本
  //  * @param {object} [record={}]
  //  * @memberof apiIndividuation
  //  */
  // const handleDelete = async (record) => {
  //   await historyDs.delete(record);
  //   historyDs.query();
  // };

  /**
   * 历史版本回滚
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  const handleRollBack = async (record) => {
    historyRollBackDs.setQueryParameter('customizeContentId', record.get('customizeContentId'));
    await historyRollBackDs.query();
    historyDs.query();
  };

  const columns = React.useMemo(() => [
    { name: 'contentType' },
    {
      name: 'versionNumber',
      renderer: ({ text }) => {
        return `v${text}.0`;
      },
    },
    { name: 'lastUpdateDate' },
    {
      name: 'action',
      width: 140,
      renderer: ({ record }) => {
        const operators = [];
        operators.push({
          key: 'detail',
          ele: (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.historyDetail`,
                  type: 'button',
                  meaning: 'API个性化历史版本-详情',
                },
              ]}
              onClick={() => handleDetail(record)}
            >
              {intl.get('hzero.common.button.detail').d('详情')}
            </ButtonPermission>
          ),
          len: 2,
          title: intl.get('hzero.common.button.detail').d('详情'),
        });

        // operators.push({
        //   key: '删除',
        //   ele: (
        //     <ButtonPermission
        //       type="text"
        //       permissionList={[
        //         {
        //           code: `${path}.button.historyDelete`,
        //           type: 'button',
        //           meaning: 'API个性化历史版本-删除',
        //         },
        //       ]}
        //       onClick={() => handleDelete(record)}
        //     >
        //       {intl.get('hzero.common.button.delete').d('删除')}
        //     </ButtonPermission>
        //   ),
        //   len: 2,
        //   title: intl.get('hzero.common.button.delete').d('删除'),
        // });

        operators.push({
          key: 'rollBack',
          ele: (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.historyRollBack`,
                  type: 'button',
                  meaning: 'API个性化历史版本-回滚',
                },
              ]}
              onClick={() => handleRollBack(record)}
            >
              {intl.get('hpfm.apiIndividuation.button.rollBack').d('回滚')}
            </ButtonPermission>
          ),
          len: 4,
          title: intl.get('hpfm.apiIndividuation.button.rollBack').d('回滚'),
        });
        return operatorRender(operators);
      },
    },
  ]);

  return (
    <>
      <Table dataSet={historyDs} highLightRow={false} columns={columns} />
    </>
  );
};

export default Drawer;
