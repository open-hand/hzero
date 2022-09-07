/**
 * FIXME: 在使用 IconFont 后 需要换种方式使用 icon @WY yang.wang06@hand-china.com
 */

import React, { PureComponent } from 'react';
import { Col, Drawer, Pagination, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Icons from 'components/Icons';

import intl from 'utils/intl';

import styles from './index.less';

const viewMessagePrompt = 'hiam.menuConfig.view.message';

export default class IconList extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      page: 0, // 当前分页
    };
  }

  @Bind()
  cancel() {
    const { onCancel = e => e } = this.props;
    onCancel();
  }

  @Bind()
  onPaginationChange(current) {
    this.dynamicImport(current - 1);
  }

  render() {
    const { visible, onCancel = e => e } = this.props;
    const { page = 0, total = 0 } = this.state;
    const paginationProps = {
      size: 'small',
      current: page + 1,
      total,
      pageSize: 12,
      showSizeChanger: false,
      onChange: this.onPaginationChange,
    };
    return (
      <Drawer
        destroyOnClose
        width={600}
        title={intl.get(`${viewMessagePrompt}.title.selectIcons`).d('选择图标')}
        visible={visible}
        onClose={onCancel}
        wrapClassName={styles['menu-icon-container']}
      >
        <Row type="flex" justify="start" gutter={12} className={styles.row}>
          <Col span={4} className={styles.col}>
            <Icons size={24} className={styles['menu-icon']} />
          </Col>
        </Row>
        <br />
        <span style={{ textAlign: 'right' }}>
          <Pagination {...paginationProps} />
        </span>
      </Drawer>
    );
  }
}
