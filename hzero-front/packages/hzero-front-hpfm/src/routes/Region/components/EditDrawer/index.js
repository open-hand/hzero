import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */

/**
 * 地区-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 组织实体
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  /**
   * 组件属性定义
   */
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    anchor: 'left',
    title: '',
    visible: false,
    onOk: e => e,
    onCancel: e => e,
  };

  constructor(props) {
    super(props);
    this.state = {
      detail: {}, // 查询的详情
    };
  }

  componentDidMount() {
    this.handleQueryDetail();
  }

  componentDidUpdate(prevProps) {
    if (prevProps.visible === false && this.props.visible === true) {
      this.handleQueryDetail();
    }
  }

  handleQueryDetail() {
    const { isCreate, itemData } = this.props;
    if (!isCreate && itemData.regionId) {
      const { queryDetail } = this.props;
      queryDetail({ regionId: itemData.regionId }).then(detail => {
        if (detail) {
          this.setState({ detail });
        }
      });
    }
  }

  /**
   * 在 模态框 关闭后 清除 detail
   */
  @Bind()
  handleAfterClose() {
    this.setState({ detail: {} });
  }

  /**
   * 确定操作
   */
  @Bind()
  saveBtn() {
    const { form, onOk, itemData } = this.props;
    if (onOk) {
      form.validateFields((err, values) => {
        if (!err) {
          // 校验通过，进行保存操作
          onOk({ ...itemData, ...values });
        }
      });
    }
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      anchor,
      title,
      visible,
      form,
      loading,
      onCancel,
      isCreate = false,
      queryDetailLoading = false,
    } = this.props;
    const {
      detail: { _token, regionCode, regionName, quickIndex },
    } = this.state;
    return (
      <Modal
        destroyOnClose
        afterClose={this.handleAfterClose}
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        confirmLoading={loading}
        okButtonProps={{ disabled: !visible || queryDetailLoading }}
        onOk={this.saveBtn}
        onCancel={onCancel}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.region.model.region.regionCode').d('区域代码')}
          >
            {form.getFieldDecorator('regionCode', {
              initialValue: regionCode,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.region.model.region.regionCode').d('区域代码'),
                  }),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(<Input trim inputChinese={false} typeCase="upper" disabled={!isCreate} />)}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.region.model.region.regionName').d('区域名称')}
          >
            {form.getFieldDecorator('regionName', {
              initialValue: regionName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.region.model.region.regionName').d('区域名称'),
                  }),
                },
                {
                  max: 120,
                  message: intl.get('hzero.common.validation.max', {
                    max: 120,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.region.model.region.regionName').d('区域名称')}
                field="regionName"
                token={_token}
              />
            )}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.region.model.region.quickIndex').d('快速索引')}
          >
            {form.getFieldDecorator('quickIndex', {
              initialValue: quickIndex,
              rules: [
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.region.model.region.quickIndex').d('快速索引')}
                field="quickIndex"
                token={_token}
              />
            )}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
