import React, { Component, Fragment } from 'react';
import { Button } from 'choerodon-ui/pro';
import { Tooltip, Modal } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import CustModal from './CustModal';
import styles from './style.less';

@formatterCollections({ code: ['hcuz.custButton'] })
export default class CustButton extends Component {
  state = {
    modalVisible: false,
  };

  @Bind()
  closeModal() {
    this.setState({
      modalVisible: false,
    });
  }

  @Bind()
  openCustModal() {
    this.setState({
      modalVisible: true,
    });
  }

  render() {
    const { modalVisible } = this.state;
    const { unit = [] } = this.props;
    return (
      <Fragment>
        <Tooltip
          placement="bottom"
          title={intl.get('hcuz.custButton.view.message.individuation').d('个性化')}
        >
          <Button icon="settings" className={styles.settings} onClick={this.openCustModal}>
            {false}
          </Button>
        </Tooltip>
        <Modal
          visible={modalVisible}
          title={intl.get('hcuz.custButton.view.button.individuation').d('个性化')}
          width={1000}
          maskStyle={{ zIndex: 100 }}
          wrapClassName={styles['cust-modal-wrap']}
          destroyOnClose
          maskClosable={false}
          className={styles['cust-modal']}
          footer={null}
          onCancel={this.closeModal}
        >
          <CustModal unit={unit} handleClose={this.closeModal} />
        </Modal>
      </Fragment>
    );
  }
}
