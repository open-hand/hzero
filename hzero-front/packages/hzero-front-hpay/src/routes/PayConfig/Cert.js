import React from 'react';
import { Form, Modal, Spin, Row, Col, Upload, Icon, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Icons from 'components/Icons';

import intl from 'utils/intl';
import { HZERO_HPAY, API_HOST } from 'utils/config';
import { getCurrentOrganizationId, getAccessToken } from 'utils/utils';
import notification from 'utils/notification';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

const uploadApi = `${API_HOST}${HZERO_HPAY}/v1/${getCurrentOrganizationId()}/configs/upload-cert`;
const headers = { Authorization: `bearer ${getAccessToken()}` };

@Form.create({ fieldNameProp: null })
export default class Cert extends React.Component {
  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  @Bind()
  handleUploadChange({ file }) {
    const {
      onCert = (e) => e,
      initData: { configId = '' },
    } = this.props;
    switch (file.status) {
      case 'error':
        notification.warning({
          message: intl.get('hpay.payConfig.view.message.uploadError').d('上传失败'),
        });
        break;
      case 'done':
        if (file.response && file.response.failed) {
          notification.error({
            message: file.response.message,
          });
        } else {
          notification.success();
          onCert({ response: file.response, configId });
        }
        break;
      default:
        break;
    }
  }

  @Bind()
  channelRender(code) {
    const {
      form,
      initData = {},
      certDetail = {},
      onClearCert = (e) => e,
      deleteLoading = false,
    } = this.props;
    const { getFieldDecorator } = form;
    let itemDom;
    const uploadProps = {
      headers,
      action: uploadApi,
      onChange: this.handleUploadChange,
    };
    switch (code) {
      case 'wxpay': {
        const { objectVersionNumber, configId, privateCert } = initData;
        const { pcName = '', objectVersionNumber: number } = certDetail;
        itemDom = (
          <>
            <Col span={24}>
              <Row>
                <Col span={18}>
                  {pcName ? (
                    <>
                      <span style={{ width: 80, display: 'inline-block' }}>
                        {intl.get('hpay.payConfig.model.payConfig.content').d('证书内容')}：
                      </span>
                      <Icons type="cert" size="20" />
                      <span style={{ marginLeft: 4 }}>{pcName}</span>
                    </>
                  ) : (
                    <FormItem
                      {...MODAL_FORM_ITEM_LAYOUT}
                      label={intl.get('hpay.payConfig.model.payConfig.content').d('证书内容')}
                    >
                      {getFieldDecorator('privateCert', {
                        initialValue: privateCert,
                      })(
                        <Upload
                          {...uploadProps}
                          data={{
                            configId,
                            objectVersionNumber: number || objectVersionNumber,
                            certType: 'PC',
                            channelCode: code,
                          }}
                        >
                          <Button>
                            <Icon type="upload" />
                            {intl.get('hzero.common.button.upload').d('上传')}
                          </Button>
                        </Upload>
                      )}
                    </FormItem>
                  )}
                </Col>
                <Col>
                  {pcName && (
                    <Button
                      style={{ marginTop: 4 }}
                      loading={deleteLoading}
                      onClick={() => onClearCert({ certType: 'PC' })}
                    >
                      {intl.get('hpay.payConfig.button.clearCert').d('清除证书')}
                    </Button>
                  )}
                </Col>
              </Row>
            </Col>
          </>
        );
        return itemDom;
      }
      case 'unionpay': {
        const { configId, privateCert, middleCert, rootCert, objectVersionNumber } = initData;
        const { mcName = '', pcName = '', rcName = '', objectVersionNumber: number } = certDetail;
        itemDom = (
          <>
            <Col span={24}>
              <Row>
                <Col span={18}>
                  {pcName ? (
                    <>
                      <span style={{ width: 87, display: 'inline-block', textAlign: 'right' }}>
                        {intl.get('hpay.payConfig.model.payConfig.privateCert').d('私钥证书')}：
                      </span>
                      <Icons type="cert" size="20" />
                      <span style={{ marginLeft: 4 }}>{pcName}</span>
                    </>
                  ) : (
                    <FormItem
                      {...MODAL_FORM_ITEM_LAYOUT}
                      style={{ flexGrow: 2 }}
                      label={intl.get('hpay.payConfig.model.payConfig.privateCert').d('私钥证书')}
                    >
                      {getFieldDecorator('privateCert', {
                        initialValue: privateCert,
                      })(
                        <Upload
                          {...uploadProps}
                          data={{
                            configId,
                            objectVersionNumber: number || objectVersionNumber,
                            certType: 'PC',
                            channelCode: code,
                          }}
                        >
                          <Button>
                            <Icon type="upload" />
                            {intl.get('hzero.common.button.upload').d('上传')}
                          </Button>
                        </Upload>
                      )}
                    </FormItem>
                  )}
                </Col>
                <Col span={6}>
                  {pcName && (
                    <Button
                      style={{ marginTop: 4 }}
                      loading={deleteLoading}
                      onClick={() => onClearCert({ certType: 'PC' })}
                    >
                      {intl.get('hpay.payConfig.button.clearCert').d('清除证书')}
                    </Button>
                  )}
                </Col>
              </Row>
            </Col>
            <Col span={24}>
              <Row>
                <Col span={18}>
                  {mcName ? (
                    <>
                      <span style={{ width: 87, display: 'inline-block', textAlign: 'right' }}>
                        {intl.get('hpay.payConfig.model.payConfig.middleCert').d('中级证书')}：
                      </span>
                      <Icons type="cert" size="20" />
                      <span style={{ marginLeft: 4 }}>{mcName}</span>
                    </>
                  ) : (
                    <FormItem
                      {...MODAL_FORM_ITEM_LAYOUT}
                      label={intl.get('hpay.payConfig.model.payConfig.middleCert').d('中级证书')}
                    >
                      {getFieldDecorator('middleCert', {
                        initialValue: middleCert,
                      })(
                        <Upload
                          {...uploadProps}
                          data={{
                            configId,
                            objectVersionNumber: number || objectVersionNumber,
                            certType: 'MC',
                            channelCode: code,
                          }}
                        >
                          <Button>
                            <Icon type="upload" />
                            {intl.get('hzero.common.button.upload').d('上传')}
                          </Button>
                        </Upload>
                      )}
                    </FormItem>
                  )}
                </Col>
                <Col span={6}>
                  {mcName && (
                    <Button
                      style={{ marginTop: 4 }}
                      loading={deleteLoading}
                      onClick={() => onClearCert({ certType: 'MC' })}
                    >
                      {intl.get('hpay.payConfig.button.clearCert').d('清除证书')}
                    </Button>
                  )}
                </Col>
              </Row>
            </Col>
            <Col span={24}>
              <Row>
                <Col span={18}>
                  {rcName ? (
                    <>
                      <span style={{ width: 87, display: 'inline-block', textAlign: 'right' }}>
                        {intl.get('hpay.payConfig.model.payConfig.rootCert').d('根证书')}：
                      </span>
                      <Icons type="cert" size="20" />
                      <span style={{ marginLeft: 4 }}>{rcName}</span>
                    </>
                  ) : (
                    <FormItem
                      {...MODAL_FORM_ITEM_LAYOUT}
                      label={intl.get('hpay.payConfig.model.payConfig.rootCert').d('根证书')}
                    >
                      {getFieldDecorator('rootCert', {
                        initialValue: rootCert,
                      })(
                        <Upload
                          {...uploadProps}
                          data={{
                            configId,
                            objectVersionNumber: number || objectVersionNumber,
                            certType: 'RC',
                            channelCode: code,
                          }}
                        >
                          <Button>
                            <Icon type="upload" />
                            {intl.get('hzero.common.button.upload').d('上传')}
                          </Button>
                        </Upload>
                      )}
                    </FormItem>
                  )}
                </Col>
                <Col span={6}>
                  {rcName && (
                    <Button
                      style={{ marginTop: 4 }}
                      loading={deleteLoading}
                      onClick={() => onClearCert({ certType: 'RC' })}
                    >
                      {intl.get('hpay.payConfig.button.clearCert').d('清除证书')}
                    </Button>
                  )}
                </Col>
              </Row>
            </Col>
          </>
        );
        return itemDom;
      }
      default:
        break;
    }
  }

  render() {
    const {
      initData = {},
      title = '',
      visible = false,
      initLoading = false,
      loading = false,
      onCancel = (e) => e,
    } = this.props;
    const { channelCode } = initData;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={visible}
        confirmLoading={loading}
        onCancel={onCancel}
        footer={
          <Button type="primary" onClick={onCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <Spin spinning={initLoading}>
          <Form>
            <Row>{this.channelRender(channelCode)}</Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
