import React from 'react';
import {
  Form,
  TextField,
  Switch,
  Lov,
  TextArea,
  NumberField,
  Select,
  ColorPicker,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { BKT_HFILE } from 'utils/config';
import Upload from 'components/Upload/UploadButton';

const { Option } = Select;
export default class Drawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isImg: false, // 水印类型是否为图片
      fileList: [], // 上传文件列表
      fontFileList: [], // 上传字体文件列表
    };
  }

  componentDidMount() {
    const { watermarkId } = this.props;
    if (watermarkId) {
      this.handleQuery(watermarkId);
    }
  }

  /**
   * 查询详情
   * @param {string} value
   */
  @Bind()
  async handleQuery(value) {
    const { ds } = this.props;
    ds.setQueryParameter('watermarkId', value);
    await ds.query();
    const watermarkType = ds.current.get('watermarkType');
    const isImg = watermarkType === 'IMAGE' || watermarkType === 'TILE_IMAGE';
    this.setState({
      isImg,
    });
    if (isImg) {
      const url = ds.current.get('detail');
      this.setState({
        fileList: url
          ? [
              {
                uid: '-1',
                name: ds.current.get('filename'),
                status: 'done',
                url,
              },
            ]
          : [],
      });
    } else {
      const url = ds.current.get('fontUrl');
      this.setState({
        fontFileList: url
          ? [
              {
                uid: '-1',
                name: ds.current.get('filename'),
                status: 'done',
                url,
              },
            ]
          : [],
      });
    }
  }

  /**
   * 选择器值改变时触发
   * @param {string} value
   */
  @Bind()
  onSelectChange(value) {
    const { isImg } = this.state;
    const { ds } = this.props;
    const flag = value === 'IMAGE' || value === 'TILE_IMAGE';
    this.setState({
      isImg: flag,
    });
    if (isImg !== flag) {
      ds.current.set('detail', '');
    }
    if (flag) {
      const url = ds.current.get('detail');
      const urlFlag = url ? url.indexOf('http://') === 0 || url.indexOf('https://') === 0 : false;
      this.setState({
        fileList: urlFlag
          ? [
              {
                uid: '-1',
                name: '',
                status: 'done',
                url,
              },
            ]
          : [],
      });
    }
  }

  render() {
    const { ds, isTenant, isCreate, onUploadSuccess, onCancelSuccess } = this.props;
    const { isImg, fileList, fontFileList } = this.state;
    return (
      <Form dataSet={ds}>
        {!isTenant && <Lov name="tenantLov" disabled={!isCreate} />}
        <TextField name="watermarkCode" disabled={!isCreate} />
        <TextField name="description" />
        <Select name="watermarkType" onChange={this.onSelectChange} />
        <NumberField name="fillOpacity" min={0} step={0.1} max={1} />
        {!isImg && <ColorPicker name="color" />}
        {!isImg && <NumberField name="fontSize" min={1} step={1} />}
        {isImg && <NumberField name="weight" min={1} step={1} />}
        {isImg && <NumberField name="height" min={1} step={1} />}
        <NumberField name="xAxis" min={0} step={1} />
        <NumberField name="yAxis" min={0} step={1} />
        <Select name="align">
          <Option value={0}>
            {intl.get('hfile.waterMark.view.waterMark.leftAlign').d('左对齐')}
          </Option>
          <Option value={1}>{intl.get('hfile.waterMark.view.waterMark.center').d('居中')}</Option>
          <Option value={2}>
            {intl.get('hfile.waterMark.view.waterMark.rightAlign').d('右对齐')}
          </Option>
        </Select>
        <NumberField name="rotation" min={0} step={1} />
        {!isImg && <TextArea name="detail" />}
        {isImg && (
          <Upload
            name="detail"
            accept="image/jpeg,image/png"
            single
            fileList={fileList}
            bucketName={BKT_HFILE}
            bucketDirectory="hfle01"
            onUploadSuccess={(file) => onUploadSuccess(file, isImg)}
            onRemove={(file) => onCancelSuccess(file, isImg)}
          />
        )}
        {!isImg && (
          <Upload
            name="fontUrl"
            single
            fileList={fontFileList}
            bucketName={BKT_HFILE}
            bucketDirectory="hfle01"
            onUploadSuccess={(file) => onUploadSuccess(file, isImg)}
            onRemove={(file) => onCancelSuccess(file, isImg)}
          />
        )}
        <Switch name="enabledFlag" />
      </Form>
    );
  }
}
