import React from 'react';
import { Form, Spin, ModalContainer } from 'choerodon-ui/pro';

import AuditLogTimeLine from '../../components/AuditLogTimeLine';

const Drawer = (props) => {
  const [dataSource, setDataSource] = React.useState([]);

  const { currentEditData, detailDs, history } = props;

  React.useEffect(() => {
    queryData();
  }, []);

  /**
   * 查询头行信息
   */
  const queryData = async () => {
    const { auditDocumentId, businessKey } = currentEditData;
    detailDs.setQueryParameter('businessKey', businessKey);
    detailDs.auditOpConfigId = auditDocumentId;
    await detailDs.query().then((res) => {
      setDataSource(res);
    });
  };

  const handleMore = (page) => {
    const { content = [] } = dataSource;
    detailDs.setQueryParameter('page', page);
    detailDs.query().then((res) => {
      if (res) {
        const { content: moreContent, ...r } = res;
        setDataSource({
          content: [...content, ...moreContent],
          ...r,
        });
      }
    });
    detailDs.setQueryParameter('page', 0);
  };

  const handleClose = () => {
    const { onClose } = props;
    onClose();
  };

  return (
    <>
      <Spin dataSet={detailDs}>
        <Form dataSet={detailDs}>
          <AuditLogTimeLine
            dataSource={dataSource}
            history={history}
            onMore={handleMore}
            onClose={handleClose}
            width={520}
          />
        </Form>
        <ModalContainer location={location} />
      </Spin>
    </>
  );
};

export default Drawer;
