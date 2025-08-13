import React from 'react';
import { Form, DataSet, Table, Modal, Output, CodeArea } from 'choerodon-ui/pro';
import { Badge } from 'choerodon-ui';
import { logDrawerDS } from '@/stores/apiIndividuationDS';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

const Drawer = (props) => {
  const logDrawerDs = React.useMemo(() => new DataSet(logDrawerDS()), []);

  React.useEffect(() => {
    logDrawerDs.setQueryParameter('customizeId', id);
    logDrawerDs.query();
  }, []);

  const { path, id } = props;

  /**
   * 打开应用日志
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  const handleDetail = (record) => {
    Modal.open({
      closable: true,
      key: 'historyDetail',
      title: intl.get('hpfm.apiIndividuation.view.title.logDetail').d('日志详情'),
      drawer: true,
      style: {
        width: 800,
      },
      children: (
        <Form record={record}>
          <Output
            name="applyStatus"
            renderer={({ value, text }) => {
              return <Badge status={value === 'APPLY_SUCCESS' ? 'success' : 'error'} text={text} />;
            }}
          />
          <CodeArea
            disabled
            name="logContent"
            options={{ lineWrapping: true }}
            style={{ height: '100%' }}
          />
        </Form>
      ),
      footer: null,
    });
  };

  // /**
  //  * 删除日志
  //  * @param {object} [record={}]
  //  * @memberof apiIndividuation
  //  */
  // const handleDelete = async (record) => {
  //   await logDrawerDs.delete(record);
  //   logDrawerDs.query();
  // };

  const columns = React.useMemo(() => [
    { name: 'creationDate' },
    { name: 'applyStatus' },
    {
      name: 'action',
      width: 120,
      renderer: ({ record }) => {
        const operators = [];

        operators.push({
          key: 'detail',
          ele: (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.logDetail`,
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
        //           code: `${path}.button.logDelete`,
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

        return operatorRender(operators);
      },
    },
  ]);

  return (
    <>
      <Table dataSet={logDrawerDs} highLightRow={false} columns={columns} />
    </>
  );
};

export default Drawer;
