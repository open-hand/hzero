import React from 'react';
import { Modal } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { fetchMessageDetail } from '@/services/messageTemplateService';
import styles from './index.less';

const HelpDrawer = (props) => {
  React.useEffect(() => {
    try {
      fetchMessageDetail({ textCode: 'HIOT.MSG_TEMPLATE_HELP' }).then((res) => {
        if (res && !res.failed) {
          openHelp(res);
        }
      });
    } catch {
      openHelp({});
    }
  }, []);

  const openHelp = (res) => {
    Modal.open({
      key: 'help',
      closable: true,
      style: { width: 600 },
      title: res.title || intl.get('hiot.messageTemplate.view.button.help').d('帮助'),
      children: (
        <div className={styles['help-draw-wrap']}>
          <pre dangerouslySetInnerHTML={{ __html: res.text }} />
        </div>
      ),
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
      afterClose: props.onCloseDrawer,
    });
  };

  return <div />;
};

export default HelpDrawer;
