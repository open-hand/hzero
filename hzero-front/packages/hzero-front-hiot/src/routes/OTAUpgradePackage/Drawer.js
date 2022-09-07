import React from 'react';
import { Form, Spin, Switch, TextArea, TextField, Select } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { BKT_HIOT } from 'utils/config';
import Upload from 'components/Upload/UploadButton';
import { fields } from '@/stores/otaUpgradePackageDS';

export default class Drawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fileList: [], // 上传文件列表
    };
  }

  componentDidMount() {
    const { packageId } = this.props;
    if (packageId) {
      this.handleQuery(packageId);
    }
  }

  /**
   * 查询详情
   * @param {string} value
   */
  @Bind()
  async handleQuery(value) {
    const { ds } = this.props;
    ds.setQueryParameter('packageId', value);
    await ds.query();
    const url = ds.current.get('attachmentUrl');
    this.setState({
      fileList: url
        ? [
            {
              uid: '-1',
              name: ds.current.get('fileName'),
              status: 'done',
              url,
            },
          ]
        : [],
    });
  }

  // 上传图片成功
  @Bind()
  onUploadSuccess(fileInfo) {
    const { ds } = this.props;
    const { response } = fileInfo;
    ds.current.set(fields.chooseUpgradePackage().name, response);
  }

  @Bind()
  onFileRemove() {
    const { ds } = this.props;
    ds.current.set(fields.chooseUpgradePackage().name, undefined);
  }

  render() {
    const { ds } = this.props;
    const { fileList } = this.state;
    return (
      <Spin dataSet={ds}>
        <Form dataSet={ds}>
          <TextField name="packageName" disabled />
          <TextField name="categoryCodeMeaning" disabled />
          <TextField name="thingModelName" disabled />
          <TextField name="thingModelCode" disabled />
          <TextField name="protocolService" disabled />
          <TextField name="currentVersion" />
          <Upload
            name="attachmentUrl"
            single
            fileSize={30 * 1024 * 1024}
            bucketName={BKT_HIOT}
            fileList={fileList}
            accept="application/zip"
            bucketDirectory="hiot01/"
            onRemove={this.onFileRemove}
            onUploadSuccess={this.onUploadSuccess}
            help={intl.get(`hiot.otaPackage.view.file.size.limit.50m`).d('文件大小不能超过30M')}
          />
          <Select name={fields.signatureAlgorithm().name} />
          <Switch name="enabledFlag" />
          <TextArea name="updateLogs" />
        </Form>
      </Spin>
    );
  }
}
