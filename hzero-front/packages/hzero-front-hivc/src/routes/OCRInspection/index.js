/**
 * OCRInspectionService OCR识别发票查验
 * @date: 2019-8-25
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { connect } from 'dva';
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Button, Card, Form, Spin } from 'hzero-ui';
import Viewer from 'react-viewer';

import { Content, Header } from 'components/Page';
import Upload from 'components/Upload/UploadButton';
import { closeTab } from 'utils/menuTab';
import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME, EDIT_FORM_CLASSNAME } from 'utils/constants';
import { BKT_INVOICE } from 'utils/config';

import style from './index.less';
import 'react-viewer/dist/index.css';

/**
 * 手工发票查验
 * @extends {Component} - React.Component
 * @return React.element
 */
@connect(({ ocrInspection, loading }) => ({
  ocrInspection,
  tenantId: getCurrentOrganizationId(),
  createLoading: loading.effects['ocrInspection/create'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hivc.ocrInspection'] })
export default class ocrInspection extends Component {
  state = {
    imageURL: '',
    previewImages: [],
    previewVisible: false,
  };

  /**
   * 识别并查验发票
   */
  @Bind()
  handleCreate() {
    const { history, dispatch } = this.props;
    const { imageURL } = this.state;
    if (imageURL) {
      dispatch({
        type: 'ocrInspection/create',
        payload: { imageURL },
      }).then((res) => {
        if (res && res[0] && res[0].code === '0001') {
          notification.success({
            message: res[0].message,
          });
          if (res[0].data) {
            history.push({
              pathname: `/hivc/inspection-history/create-detail`,
              payload: {
                ...res[0].data,
                type: 'ocr',
              },
            });
            closeTab(`/hivc/ocr-inspection/create`);
          }
        } else {
          notification.error({
            message: res[0].message,
          });
        }
      });
    }
  }

  @Bind()
  onCancelSuccess(file) {
    if (file) {
      this.setState({ imageURL: '' });
    }
  }

  /**
   * @function onUploadSuccess - 图片上传成功的回调函数
   * @param {object} file - 上传的文件对象
   */
  @Bind()
  onUploadSuccess(file) {
    if (file.response) {
      this.setState({ imageURL: file.response });
    }
  }

  /**
   * 图片预览
   * @param {*} file
   */
  @Bind()
  handlePreview(file) {
    this.setState({
      previewImages: [
        {
          src: file.url || file.thumbUrl,
          alt: '', // 由于下方会显示 alt 所以这里给空字符串 file.name,
        },
      ],
      previewVisible: true,
    });
  }

  /**
   * 图片预览取消
   */
  @Bind()
  handlePreviewCancel() {
    this.setState({
      previewImages: [],
      previewVisible: false,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { match, createLoading = false } = this.props;
    const { id } = match.params;
    const headerTitle = intl.get('hivc.ocrInspection.view.message.title.edit').d('OCR发票查验');
    const { previewImages, previewVisible } = this.state;
    return (
      <>
        <Spin spinning={false} wrapperClassName={style['spin-container']}>
          <Header title={headerTitle}>
            {!id && (
              <Button type="primary" icon="eye" onClick={this.handleCreate} loading={createLoading}>
                {intl.get('hivc.ocrInspection.model.ocrInspection.OCR').d('OCR识别并查验')}
              </Button>
            )}
          </Header>
          <Content>
            <Card
              key="invoice-header"
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{intl.get('hivc.ocrInspection.view.message.title').d('OCR发票查验')}</h3>}
            >
              <Upload
                className={style.uploader}
                accept=".jpeg,.png,.jpg"
                listType="picture-card"
                single
                bucketName={BKT_INVOICE}
                bucketDirectory="hivc01"
                onRemove={this.onCancelSuccess}
                onUploadSuccess={this.onUploadSuccess}
                onPreview={this.handlePreview}
                showUploadList={{
                  removePopConfirmTitle: intl
                    .get('hzero.common.message.confirm.delete')
                    .d('是否删除此条记录？'),
                }}
              />
              <Form className={EDIT_FORM_CLASSNAME} />
            </Card>
          </Content>
          <Viewer
            noImgDetails
            noNavbar
            scalable={false}
            changeable={false}
            visible={previewVisible}
            onClose={this.handlePreviewCancel}
            images={previewImages}
          />
        </Spin>
      </>
    );
  }
}
