import React, { PureComponent } from 'react';
import { isEmpty } from 'lodash';
import { Button, Form, Icon, Input, Modal, Select, Tabs, Upload } from 'hzero-ui';
import isAbsoluteUrl from 'is-absolute-url';
import intl from 'utils/intl';
import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import notification from 'utils/notification';
import { getAttachmentUrl } from 'utils/utils';
// import intl from 'utils/intl';

const { TabPane } = Tabs;
const FormItem = Form.Item;
const { Option } = Select;

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 4 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 20 },
  },
};

@Form.create({ fieldNameProp: null })
export default class RichTextInsertImages extends PureComponent {
  state = {
    fileList: [],
    fileUrl: null,
  };

  config = getEnvConfig();

  ok() {
    const {
      form: { getFieldValue = (e) => e },
      onOk,
    } = this.props;
    const { fileList, fileUrl } = this.state;
    let content = '';
    if (!isEmpty(fileList) && fileUrl) {
      content += `<a href="${fileUrl}" target="_blank">${
        getFieldValue('fileName') || fileList[0].name || fileUrl
      }</a>`;
    }
    const url = getFieldValue('url');
    const urlPrefix = getFieldValue('urlPrefix');

    if (!isEmpty(url)) {
      content += `<a href="${urlPrefix + url}" target="_blank">${
        getFieldValue('fileName') || url + urlPrefix
      }</a>`;
    }

    onOk(content);
    this.cancel();
  }

  cancel() {
    const { onCancel = (e) => e } = this.props;
    this.setState({
      fileList: [],
    });
    onCancel();
  }

  onUploadRemove(file) {
    const { fileList } = this.state;
    this.setState({
      fileList: fileList.filter((o) => o.uid !== file.uid),
    });
  }

  setFileList(file) {
    this.setState({
      fileList: [file],
    });
  }

  beforeUpload(file) {
    const { BKT_PUBLIC, HZERO_FILE } = getEnvConfig();
    const { bucketName = BKT_PUBLIC, bucketDirectory = 'editor' } = this.props;
    const formData = new FormData();
    formData.append('file', file, file.name);
    formData.append('bucketName', bucketName);
    formData.append('directory', bucketDirectory);
    formData.append('fileName', file.name);
    this.setState({
      fileList: [file],
    });
    request(`${HZERO_FILE}/v1/files/multipart`, {
      method: 'POST',
      body: formData,
      type: 'FORM',
      processData: false,
      responseType: 'text',
    }).then((res) => {
      if (isAbsoluteUrl(res)) {
        notification.success();
        this.setState({
          fileUrl: getAttachmentUrl(res, BKT_PUBLIC, undefined, bucketDirectory),
        });
      } else {
        notification.error({ description: JSON.parse(res).message });
      }
    });
    return false;
  }

  render() {
    const { visible, form, prompt = {} } = this.props;
    const { getFieldDecorator } = form;
    const { fileList } = this.state;
    const uploadProps = {
      beforeUpload: this.beforeUpload.bind(this),
      onRemove: this.onUploadRemove.bind(this),
      fileList,
    };
    const prefixSelector = getFieldDecorator(`urlPrefix`, {
      initialValue: 'https://',
    })(
      <Select>
        <Option value="https://">https://</Option>
        <Option value="http://">http://</Option>
      </Select>
    );

    return (
      <Modal
        title={
          prompt.insertFile || intl.get('hzero.common.richTextEditor.insertFile').d('插入文件')
        }
        visible={visible}
        onOk={this.ok.bind(this)}
        onCancel={this.cancel.bind(this)}
        destroyOnClose
        width={500}
      >
        <Tabs defaultActiveKey="upload">
          <TabPane
            tab={
              <span>
                <Icon type="upload" />
                {prompt.uploadLocalFile ||
                  intl.get('hzero.common.richTextEditor.localUpload').d('本地上传')}
              </span>
            }
            key="upload"
            style={{ height: '100%' }}
          >
            <Upload {...uploadProps}>
              <Button>
                <Icon type="upload" />
                {prompt.selectFile ||
                  intl.get('hzero.common.richTextEditor.selectUpload').d('选择文件上传')}
              </Button>
            </Upload>
            <br />
          </TabPane>
          <TabPane
            tab={
              <span>
                <Icon type="link" />
                {prompt.fileLink || intl.get('hzero.common.richTextEditor.fileLink').d('文件链接')}
              </span>
            }
            key="link"
          >
            <Form>
              <FormItem {...formItemLayout} label="url">
                {getFieldDecorator(`url`)(
                  <Input addonBefore={prefixSelector} style={{ width: '80%' }} />
                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
                label={
                  prompt.fileDescription ||
                  intl.get('hzero.common.richTextEditor.fileDescription').d('文件说明')
                }
              >
                {getFieldDecorator(`fileName`)(<Input style={{ width: '80%' }} />)}
              </FormItem>
            </Form>
          </TabPane>
        </Tabs>
      </Modal>
    );
  }
}
