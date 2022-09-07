/*
 * @Description: 事件定义 - 编辑表单
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-06-09 13:49:47
 * @Copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Divider } from 'choerodon-ui';
import { Button, Table, DataSet, Lov as ProLov } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import Lov from 'components/Lov';
import Switch from 'components/Switch';
import { getCurrentUser } from 'utils/utils';

import { EventParamsDS } from '@/stores/EventDS';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
const otherProps = {
  wrapClassName: 'ant-modal-sidebar-right',
  transitionName: 'move-right',
};
@Form.create({ fieldNameProp: null })
export default class EditModal extends React.PureComponent {
  constructor(props) {
    super(props);
    this.eventParamsDS = new DataSet({
      ...EventParamsDS(),
    });
  }

  getSnapshotBeforeUpdate(prevProps) {
    const { modalVisible } = this.props;
    return modalVisible !== (prevProps || {}).modalVisible && modalVisible === true;
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      if (this.props.initData.eventId) {
        this.eventParamsDS.setQueryParameter('sourceId', this.props.initData.eventId);
        this.eventParamsDS.query();
      } else {
        this.eventParamsDS.loadData([]);
      }
    }
  }

  /**
   * 事件定义保存
   */
  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk({ ...fieldsValue, eventConfigs: this.eventParamsDS.toJSONData() });
      }
    });
  }

  get columns() {
    return [
      {
        name: 'configCodeLov',
        editor: () => {
          return <ProLov />;
        },
      },
      {
        name: 'configValue',
        editor: true,
      },
    ];
  }

  render() {
    const { form, initData, title, loading, onCancel, modalVisible } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOk}
        footer={[
          <Button key="ok" onClick={this.handleOk} color="primary">
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
          <Button key="cancel" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
        ]}
        {...otherProps}
      >
        <Form>
          <FormItem
            {...formLayout}
            label={intl.get('hevt.common.model.categoryCode').d('事件编码')}
          >
            {getFieldDecorator('eventCode', {
              initialValue: initData.eventCode,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hevt.common.model.categoryCode').d('事件编码'),
                  }),
                },
              ],
            })(<Input trim inputChinese={false} disabled={!!initData.eventCode} />)}
          </FormItem>
          <FormItem {...formLayout} label={intl.get('hevt.common.model.eventName').d('事件名称')}>
            {getFieldDecorator('eventName', {
              initialValue: initData.eventName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hevt.common.model.eventName').d('事件名称'),
                  }),
                },
                {
                  max: 360,
                  message: intl.get('hzero.common.validation.max', {
                    max: 360,
                  }),
                },
              ],
            })(<Input trim />)}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl.get('hevt.event.model.event.categoryName').d('事件类型')}
          >
            {getFieldDecorator('categoryId', {
              initialValue: initData.categoryId,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hevt.event.model.event.categoryName').d('事件类型'),
                  }),
                },
              ],
            })(
              <Lov
                code="HEVT.EVENT_CATEGORY"
                queryParams={{
                  enabledFlag: 1,
                }}
                textValue={initData.categoryName}
              />
            )}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl.get('hevt.event.model.event.eventSourceCode').d('事件源')}
          >
            {getFieldDecorator('eventSourceId', {
              initialValue: initData.eventSourceId,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hevt.event.model.event.eventSourceCode').d('事件源'),
                  }),
                },
              ],
            })(
              <Lov
                code="HEVT.EVENT_SOURCE"
                textValue={initData.eventSourceName}
                queryParams={{
                  enabledFlag: 1,
                  tenantId: getCurrentUser().tenantId,
                }}
              />
            )}
          </FormItem>
          <FormItem {...formLayout} label={intl.get('hevt.event.model.event.topic').d('Topic')}>
            {getFieldDecorator('topic', {
              initialValue: initData.topic,
              validateFirst: true,
              rules: [
                {
                  pattern: /^[A-Za-z0-9-_.]*$/,
                  message: intl
                    .get('hevt.event.validation.topic')
                    .d('只能由字母、数字、“-”、“_”、“.”组成'),
                },
                {
                  max: 480,
                  message: intl.get('hzero.common.validation.max', {
                    max: 480,
                  }),
                },
              ],
            })(<Input trim inputChinese={false} />)}
          </FormItem>
          <FormItem {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
            {getFieldDecorator('enabledFlag', {
              initialValue: initData.enabledFlag === undefined ? 1 : initData.enabledFlag,
            })(<Switch />)}
          </FormItem>
        </Form>
        <Divider orientation="left">
          <h3>{intl.get('hevt.event.view.title.handleParams').d('事件参数配置')}</h3>
        </Divider>
        <Table
          buttons={['add', 'delete']}
          dataSet={this.eventParamsDS}
          queryBar="none"
          columns={this.columns}
        />
      </Modal>
    );
  }
}
