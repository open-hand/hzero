/**
 * AvatarUploadModal.js - 头像上传模态框
 * @description
 * @author ZhuYan Luo
 * @email zhuyan.luo@hand-china.com
 * @date 2018/11/15
 */

import React from 'react';
import { Button, Icon, Modal, Upload } from 'hzero-ui';
import Cropper from 'react-cropper';
import 'cropperjs/dist/cropper.css';
import { Bind } from 'lodash-decorators';
import { indexOf, isEmpty, isObject, isString } from 'lodash';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { HZERO_FILE, BKT_PUBLIC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

import styles from '../index.less';

const { Dragger } = Upload;
const STORAGE_UNIT = { KB: 1024, MB: 1024 * 1024, GB: 1024 * 2014 * 1024 };

function isJSON(str) {
  let result;
  try {
    result = JSON.parse(str);
  } catch (e) {
    return false;
  }
  return isObject(result) && !isString(result);
}

export default class AvatarUploadModal extends React.PureComponent {
  state = {
    fileList: [],
    file: null,
    avatarPath: '',
    limitData: {},
    currentTenantId: getCurrentOrganizationId(),
  };

  cropper;

  componentDidMount() {
    this.handleSearch();
  }

  @Bind()
  handleSearch() {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'userInfo/fetchEnabledFile',
      payload: {
        tenantId: organizationId,
        bucketName: BKT_PUBLIC,
        directory: 'hiam02',
      },
    }).then(res => {
      if (res) {
        this.setState({
          limitData: res,
        });
      }
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
    this.setState({ fileList: [], file: null });
  }

  /**
   * 图片裁剪，获取图片的blob格式
   */
  handleAvatarCrop() {
    const { dispatch } = this.props;
    const imgCanvas = this.cropper.getCroppedCanvas({
      width: 256,
      height: 256,
      imageSmoothingQuality: 'high',
    });
    // IE toBlob兼容方案，手动实现toBlob，兼容IE10及以上
    if (!HTMLCanvasElement.prototype.toBlob) {
      Object.defineProperty(HTMLCanvasElement.prototype, 'toBlob', {
        value(callback, type, quality) {
          // atob将编码为base64后的图片数据解码为二进制数据
          const binStr = atob(this.toDataURL(type, quality).split(',')[1]);
          const len = binStr.length;
          // 使用二进制数组存放数据
          const arr = new Uint8Array(len);
          for (let i = 0; i < len; i++) {
            // 将指定位置的数据设置为Unicode编码，存放在二进制数组中
            arr[i] = binStr.charCodeAt(i);
          }
          // 最后返回Blob类型的数据
          callback(new Blob([arr], { type: type || 'image/png' }));
        },
      });
    }
    imgCanvas.toBlob(blob => {
      dispatch({ type: 'userInfo/updateState', payload: { imgFormData: blob } });
    });
  }

  /**
   *beforeUpload 上传之前进行类型检测
   * @param {Object} file 文件对象
   */
  beforeUpload(file) {
    this.setState(({ fileList }) => ({
      fileList: [...fileList, file],
      file,
    }));
    this.props.dispatch({ type: 'userInfo/updateState', payload: { imgFormData: file } });
    return false;
  }

  /**
   * 选择图片后，将图片转换为base64格式预览
   * @param {object} info 文件数据对象，包含file，fileList
   */
  handleImgChange(info) {
    const { dispatch } = this.props;
    const reader = new FileReader();
    reader.readAsDataURL(info.file);
    reader.onload = () => {
      dispatch({
        type: 'userInfo/updateState',
        payload: {
          uploadImgPreviewUrl: reader.result,
          uploadImgName: info.file.name,
        },
      });
    };
  }

  /**
   * 校验上传参数
   * 使用时: 如果Boolean(返回的数据), 则不做限制
   */
  validateUploadParams() {
    const { file, limitData } = this.state;
    const { fileFormat, storageSize, storageUnit } = limitData;
    if (!fileFormat && !storageSize) {
      // 如果没有查询到桶配置, 那么就不做限制;
      return;
    }

    let flag = true;
    let message;
    if (fileFormat) {
      const fileType = fileFormat.split(',').map(item => `image/${item}`);
      if (fileType.indexOf('image/jpg') > -1) {
        fileType.push('image/jpeg');
      }
      flag = indexOf(fileType, file.type) > -1;
      message = flag
        ? ''
        : intl.get('hiam.userInfo.view.message.fileTypeError').d('文件类型不支持');
    }

    if (storageSize && flag) {
      // 表示文件大小上限，单位：KB
      const fileSize = STORAGE_UNIT[storageUnit] * storageSize;
      flag = fileSize > file.size;
      message = flag
        ? ''
        : intl
            .get('hzero.common.upload.error.size.custom', {
              fileSize: `${storageSize}${storageUnit}`,
            })
            .d(`上传文件大小不能超过: ${storageSize}${storageUnit}`);
    }

    return message;
  }

  /**
   * 保存裁剪的图片
   */
  handleCutAvatar() {
    const { fileList } = this.state;
    const { dispatch, imgFormData, uploadImgName, organizationId, onCancel } = this.props;
    dispatch({
      type: 'userInfo/updateState',
      payload: { imgUploadStatus: 'uploading' },
    });
    if (fileList.length > 1) {
      fileList.pop();
    }

    // 判断是否有上传配置，有的话对上传的文件类型和大小作限制，否则都不限制
    const message = this.validateUploadParams();
    if (message) {
      notification.error({ message });
      dispatch({
        type: 'userInfo/updateState',
        payload: { imgUploadStatus: 'waiting' },
      });
      return;
    }

    dispatch({
      type: 'userInfo/uploadAvatar',
      payload: {
        organizationId,
        image: imgFormData,
        uploadImgName,
        bucketName: BKT_PUBLIC,
        directory: 'hiam02',
      },
    }).then(res => {
      if (isJSON(res) && JSON.parse(res).failed) {
        notification.error({ description: JSON.parse(res).message });
      } else if (res) {
        this.setState({ avatarPath: res }, () => {
          dispatch({
            type: 'userInfo/saveAvatar',
            payload: res,
          }).then(result => {
            if (result) {
              notification.success({
                message: intl.get('hiam.userInfo.view.message.updateSuccess').d('头像保存成功'),
              });
              this.setState({
                ...this.state,
                fileList: [],
                file: null,
              });
              onCancel();
              // eslint-disable-next-line no-underscore-dangle
              const state = window.dvaApp._store.getState();
              const { userInfo = {} } = state;
              dispatch({
                type: 'userInfo/updateState',
                payload: { userInfo: { ...userInfo.userInfo, imageUrl: res } },
              });
              dispatch({
                type: 'user/updateCurrentUser',
                payload: { imageUrl: res },
              });
            }
          });
        });
      }
    });
  }

  render() {
    const {
      avatarVisible,
      imgUploadStatus,
      loading,
      uploadImgPreviewUrl,
      userInfo,
      onOk,
    } = this.props;
    const { fileList, limitData, currentTenantId } = this.state;
    const { fileFormat, storageSize, storageUnit } = limitData;
    let displayFlag = false;
    let typeFlag = false;
    if (!isEmpty(limitData)) {
      displayFlag = true;
      if (limitData.fileFormat !== '') {
        typeFlag = true;
      }
    }
    return (
      <Modal
        visible={avatarVisible}
        maskClosable={false}
        title={intl.get('hiam.userInfo.view.message.changeAvatar').d('更换头像')}
        width={485}
        onOk={onOk}
        onCancel={this.handleCancel}
        footer={[
          <Button key="back" onClick={this.handleCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button
            key="submit"
            type="primary"
            onClick={() => this.handleCutAvatar()}
            disabled={fileList.length === 0}
            loading={imgUploadStatus === 'uploading' || loading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>,
        ]}
      >
        {fileList.length === 0 ? (
          <Dragger
            className={styles['avatar-uploader']}
            showUploadList={false}
            beforeUpload={file => this.beforeUpload(file)}
            onChange={info => this.handleImgChange(info)}
            name="image"
            action={`${HZERO_FILE}/v1/${
              isTenantRoleLevel() ? `${currentTenantId}/` : ''
            }files/byte`}
            data={{ imagePath: userInfo.imageUrl }}
            accept="image/jpeg,image/jpg,image/png,image/gif,image/bmp"
          >
            <p className="ant-upload-drag-icon">
              <Icon type="inbox" />
            </p>
            <p className="ant-upload-text">
              {intl
                .get('hiam.userInfo.view.message.uploadTips')
                .d('点击或将图片拖到此区域上传图片')}
            </p>
            {displayFlag && (
              <p className="ant-upload-hint">
                {typeFlag &&
                  intl
                    .get('hiam.userInfo.view.message.uploadType', {
                      uploadType: fileFormat,
                    })
                    .d(`支持${fileFormat}格式，`)}
                {intl
                  .get('hzero.common.upload.error.size.custom', {
                    fileSize: `${storageSize}${storageUnit}`,
                  })
                  .d(`上传文件大小不能超过: ${storageSize}${storageUnit}`)}
              </p>
            )}
          </Dragger>
        ) : (
          <div className={styles.uploader}>
            <div className={styles['avatar-upload']}>
              <div className={styles['avatar-crop-wrap']}>
                <Cropper
                  viewMode={1}
                  preview=".img-preview"
                  className={styles['avatar-crop']}
                  ref={ele => {
                    this.cropper = ele;
                  }}
                  src={uploadImgPreviewUrl}
                  aspectRatio={1 / 1}
                  zoomable={false}
                  movable={false}
                  guides={false}
                  crop={() => this.handleAvatarCrop()}
                />
              </div>
              <div className={styles['avatar-preview-wrap']}>
                <div>{intl.get('hiam.userInfo.view.message.preview').d('预览')}</div>
                <div className={styles.preview}>
                  <div className="img-preview" style={{ width: 100, height: 100 }} />
                  <div>100 * 100</div>
                </div>
                <div className={styles.preview}>
                  <div className="img-preview" style={{ width: 64, height: 64 }} />
                  <div>64 * 64</div>
                </div>
                <div className={styles.preview}>
                  <div className="img-preview" style={{ width: 24, height: 24 }} />
                  <div>24 * 24</div>
                </div>
              </div>
            </div>
            <Upload
              className={styles['avatar-uploader']}
              showUploadList={false}
              beforeUpload={file => this.beforeUpload(file)}
              onChange={info => this.handleImgChange(info)}
              name="image"
              action={`${HZERO_FILE}/v1/${
                isTenantRoleLevel() ? `${currentTenantId}/` : ''
              }files/byte`}
              data={{ imagePath: userInfo.imageUrl }}
              accept="image/jpeg,image/jpg,image/png,image/gif,image/bmp"
            >
              <Button style={{ marginTop: 10 }}>
                <Icon type="upload" />
                {intl.get('hiam.userInfo.view.option.selectPic').d('重新上传')}
              </Button>
            </Upload>
          </div>
        )}
        {/* <Button
          type="primary"
          onClick={() => this.handleCutAvatar()}
          disabled={this.state.fileList.length === 0}
          loading={imgUploadStatus === 'uploading'}
          style={{ marginTop: 10 }}
        >
          {intl.get('hiam.userInfo.view.option.cut').d('确定裁剪')}
        </Button> */}
      </Modal>
    );
  }
}
