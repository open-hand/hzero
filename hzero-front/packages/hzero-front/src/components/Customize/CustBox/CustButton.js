import React, { Component, Fragment } from 'react';
import { Tooltip, Button, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

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
          <Button icon="setting" className={styles.settings} onClick={this.openCustModal} />
        </Tooltip>
        <Modal
          visible={modalVisible}
          title={intl.get('hcuz.custButton.view.button.individuation').d('个性化')}
          width={1000}
          // maskStyle={{ zIndex: 100 }}
          // wrapClassName={styles['cust-modal-wrap']}
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
