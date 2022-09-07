import React from 'react';
import { Form, DataSet, Table, Modal, TextArea } from 'choerodon-ui/pro';
import { Icon, Tooltip } from 'choerodon-ui';

import { pointDrawerDS, pointDS } from '@/stores/apiIndividuationDS';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';

const Drawer = (props) => {
  const pointDs = React.useMemo(() => new DataSet(pointDS()), []);
  const pointDrawerDs = React.useMemo(() => new DataSet(pointDrawerDS()), []);
  const { path, serviceName, onDbClick } = props;

  React.useEffect(() => {
    pointDs.setQueryParameter('serviceName', serviceName);
    pointDs.query();
  }, []);

  const handleFresh = () => {
    Modal.open({
      closable: true,
      key: 'refreshPoint',
      title: intl.get('hpfm.apiIndividuation.button.refreshPoint').d('刷新服务'),
      drawer: true,
      children: (
        <Form dataSet={pointDrawerDs}>
          <TextArea
            name="packageNames"
            label={
              <span>
                {intl.get('hpfm.apiIndividuation.model.apiIndividuation.packageNames').d('包名')}
                <Tooltip
                  title={intl
                    .get('hpfm.apiIndividuation.view.message.packageNames')
                    .d('输入扫描的包名，多个包用逗号隔开')}
                >
                  <Icon type="help_outline" />
                </Tooltip>
              </span>
            }
          />
        </Form>
      ),
      onOk: handleOk,
    });
  };

  const handleOk = async () => {
    pointDrawerDs.current.set('serviceName', serviceName);
    await pointDrawerDs.submit();
    pointDs.query();
  };

  const columns = React.useMemo(() => [
    { name: 'packageName' },
    { name: 'className' },
    { name: 'methodName' },
    { name: 'methodArgs' },
    { name: 'methodReturn' },
  ]);

  return (
    <>
      <Table
        buttons={[
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${path}.button.historyDelete`,
                type: 'button',
                meaning: 'API个性化选择切入点-刷新切入点',
              },
            ]}
            onClick={() => handleFresh()}
          >
            {intl.get('hpfm.apiIndividuation.button.refreshPoint').d('刷新切入点')}
          </ButtonPermission>,
        ]}
        dataSet={pointDs}
        highLightRow={false}
        columns={columns}
        onRow={({ record }) => {
          return {
            onDoubleClick: () => {
              onDbClick(record);
            },
          };
        }}
      />
    </>
  );
};

export default Drawer;
