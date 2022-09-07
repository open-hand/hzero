/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/26
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 创建OTA升级包弹框组件
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { DataSet, Form, Lov, Select, TextField, Output, Switch, IntlField } from 'choerodon-ui/pro';
import Upload from 'components/Upload/UploadButton';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { BKT_HIOT } from 'utils/config';
import { fields, createOTAUpgradePackageDS } from '@/stores/otaUpgradePackageDS';
import Spin from '@/routes/components/loading/Spin';

const prefix = 'hiot.otaPackage';

@formatterCollections({
  code: [prefix, 'hiot.common'],
})
export default class CreateOTAUpgradePackage extends Component {
  constructor(props) {
    super(props);
    this.createOTAUpgradePackageDS = new DataSet(createOTAUpgradePackageDS());
    this.state = {
      isDeviceTemp: true, // 是否是设备模板
    };
    this.uploadProps = {
      headers: {
        'Access-Control-Allow-Origin': '*',
      },
      action: '//localhost:3000/upload',
      multiple: true,
      accept: ['.deb', '.txt', '.pdf', 'image/*'],
      uploadImmediately: false,
      showUploadBtn: false,
      showPreviewImage: true,
    };
  }

  componentDidMount() {
    this.createOTAUpgradePackageDS.create({}, 0);
    const { onRef } = this.props;
    if (onRef) {
      onRef(this);
    }
  }

  // 上传图片成功
  @Bind()
  onUploadSuccess(fileInfo) {
    const { response } = fileInfo;
    this.createOTAUpgradePackageDS.get(0).set(fields.chooseUpgradePackage().name, response);
  }

  @Bind()
  onFileRemove() {
    this.createOTAUpgradePackageDS.get(0).set(fields.chooseUpgradePackage().name, undefined);
  }

  @Bind()
  handleTempName(lovRecord) {
    const dsRecord = this.createOTAUpgradePackageDS.get(0);
    dsRecord.set(fields.templateName().name, lovRecord);
    dsRecord.set(fields.templateNameGateWay().name, lovRecord);
  }

  @Bind()
  handleSubmit() {
    const createCompDS = this.createOTAUpgradePackageDS;
    return createCompDS.submit();
  }

  render() {
    const { isDeviceTemp } = this.state;
    return (
      <Spin dataSet={this.createOTAUpgradePackageDS}>
        <Form dataSet={this.createOTAUpgradePackageDS}>
          <IntlField name="packageName" />
          <Select
            name={fields.upgradeCategory().name}
            onChange={(value) => {
              this.createOTAUpgradePackageDS.get(0).set(fields.templateName().name, {});
              this.createOTAUpgradePackageDS.get(0).set(fields.templateNameGateWay().name, {});
              this.setState({ isDeviceTemp: value === 'THING' });
            }}
          />
          {isDeviceTemp ? (
            <Lov name={fields.templateName().name} onChange={this.handleTempName} />
          ) : (
            <Lov name={fields.templateNameGateWay().name} onChange={this.handleTempName} />
          )}
          <TextField name={fields.templateCode().name} disabled />
          <TextField name="protocolService" />
          <TextField name={fields.versionNum().name} />
          <Output
            required
            label={intl.get('hiot.ota.choose.upgrade.package').d('选择升级包')}
            renderer={() => (
              <Upload
                single
                fileSize={30 * 1024 * 1024}
                bucketName={BKT_HIOT}
                accept="application/zip"
                bucketDirectory="hiot01/"
                onRemove={this.onFileRemove}
                onUploadSuccess={this.onUploadSuccess}
              />
            )}
            help={intl.get(`${prefix}.view.file.size.limit.30m`).d('文件大小不能超过30M')}
          />
          <Select name={fields.signatureAlgorithm().name} />
          <Switch name="enabledFlag" />
          <TextField name={fields.versionDesc().name} />
        </Form>
      </Spin>
    );
  }
}
