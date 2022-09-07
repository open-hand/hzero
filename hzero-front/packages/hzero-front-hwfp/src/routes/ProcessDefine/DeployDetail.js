import React, { PureComponent } from 'react';
import { Modal, Collapse, Input, Form, Button } from 'hzero-ui';
import PropTypes from 'prop-types';
import classNames from 'classnames';

import intl from 'utils/intl';
import { valueMapMeaning } from 'utils/renderer';
import { HZERO_WFL, API_HOST } from 'utils/config';
import { getAccessToken } from 'utils/utils';

import styles from './DeployDetail.less';

const prefix = `${HZERO_WFL}/v1`;
const accessToken = getAccessToken('access_token');

/**
 * 流程部署详情-数据展示(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 12 },
};

export default class DeployDetail extends PureComponent {
  /**
   * 组件属性定义
   */
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    anchor: 'right',
    title: '',
    visible: false,
    onCancel: e => e,
  };

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      anchor,
      visible,
      title,
      process,
      deploy,
      category,
      onCancel,
      onChangePanel,
      tenantId,
      id,
    } = this.props;
    return (
      <Modal
        title={title}
        width={1000}
        className={classNames(styles['deploy-detail'])}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onCancel={onCancel}
        footer={
          <Button type="primary" onClick={onCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
        destroyOnClose
      >
        <Collapse accordion bordered={false} onChange={onChangePanel} defaultActiveKey="deploy">
          <Collapse.Panel
            header={intl.get('hwfp.processDefine.view.message.processInfo').d('流程信息')}
            key="process"
          >
            <Form.Item
              label={intl.get('hwfp.processDefine.model.deploy.name').d('名称')}
              {...formLayout}
            >
              <Input disabled value={process.name} />
            </Form.Item>
            <Form.Item
              label={intl.get('hwfp.processDefine.model.deploy.category').d('分类')}
              {...formLayout}
            >
              <Input disabled value={process.category} />
            </Form.Item>
            <Form.Item
              label={intl.get('hwfp.processDefine.model.deploy.flag').d('标识')}
              {...formLayout}
            >
              <Input disabled value={process.flag} />
            </Form.Item>
            <Form.Item
              label={intl.get('hwfp.processDefine.model.deploy.version').d('版本')}
              {...formLayout}
            >
              <Input disabled value={process.version} />
            </Form.Item>
          </Collapse.Panel>
          <Collapse.Panel
            header={intl.get('hwfp.processDefine.view.message.deployInfo').d('部署信息')}
            key="deploy"
          >
            <Form.Item
              label={intl.get('hwfp.processDefine.model.deploy.name').d('名称')}
              {...formLayout}
            >
              <Input disabled value={deploy.name} />
            </Form.Item>
            <Form.Item
              label={intl.get('hwfp.processDefine.model.deploy.category').d('分类')}
              {...formLayout}
            >
              <Input disabled value={valueMapMeaning(category, deploy.category)} />
            </Form.Item>
            <Form.Item
              label={intl.get('hwfp.processDefine.model.deploy.time').d('发布时间')}
              {...formLayout}
            >
              <Input disabled value={deploy.deploymentTime} />
            </Form.Item>
          </Collapse.Panel>
          <Collapse.Panel
            header={intl.get('hwfp.processDefine.view.message.preview').d('预览图')}
            key="image"
          >
            <img
              alt=""
              type="image/svg+xml"
              src={`${API_HOST}${prefix}/${tenantId}/process/models/definitions/image/${id}?access_token=${accessToken}`}
            />
          </Collapse.Panel>
        </Collapse>
      </Modal>
    );
  }
}
