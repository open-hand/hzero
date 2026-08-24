import React, { PureComponent } from 'react';
import { Form, Input, Modal, Select, Spin } from 'hzero-ui';
import { filter, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const FormItem = Form.Item;
const { Option } = Select;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

/**
 * 新建或编辑模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAdd - 添加确定的回调函数
 * @reactProps {Function} onEdit - 编辑确定的回调函数
 * @reactProps {Object} fileDetail - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  @Bind()
  onOk() {
    const { form, onAdd, isCreate, fileDetail, onEdit } = this.props;
    const { uploadConfigId, objectVersionNumber, _token } = fileDetail;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        const { fileFormat, contentType, ...others } = values;
        if (isCreate) {
          onAdd({
            fileFormat: fileFormat.join(','),
            contentType: contentType.join(','),
            ...others,
          });
        } else {
          onEdit({
            _token,
            uploadConfigId,
            objectVersionNumber,
            fileFormat: fileFormat.join(','),
            contentType: contentType.join(','),
            ...others,
          });
        }
      }
    });
  }

  /**
   *  改变文件分类
   */
  @Bind()
  changeContentType(value) {
    const { fileFormatsList = [], onChangeFileFormats } = this.props;
    const newFileFormat = filter(fileFormatsList, (item) => value.indexOf(item.parentValue) >= 0);
    onChangeFileFormats(newFileFormat);
  }

  render() {
    const {
      visible,
      onCancel,
      saving,
      anchor,
      fileDetail,
      isCreate,
      fileTypeList = [],
      fileUnitList = [],
      newFileFormat = [],
      detailLoading = false,
    } = this.props;
    const { bucketName } = fileDetail;
    const { getFieldDecorator } = this.props.form;
    const fileMaxUnitSelector = getFieldDecorator('storageUnit', {
      initialValue: fileDetail.storageUnit ? fileDetail.storageUnit : 'MB',
    })(
      <Select style={{ width: 65 }}>
        {fileUnitList &&
          fileUnitList.map((item) => (
            <Option value={item.value} key={item.value}>
              {item.meaning}
            </Option>
          ))}
      </Select>
    );
    return (
      <Modal
        destroyOnClose
        width={520}
        title={
          isCreate
            ? intl.get('hfile.fileUpload.view.message.create').d('新建文件上传详细配置')
            : intl.get('hfile.fileUpload.view.message.edit').d('编辑文件上传详细配置')
        }
        visible={visible}
        onCancel={onCancel}
        onOk={this.onOk}
        confirmLoading={saving}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
      >
        <Spin spinning={detailLoading}>
          <Form>
            <FormItem
              label={intl.get('hfile.fileUpload.model.fileUpload.bucketName').d('分组')}
              {...formLayout}
            >
              {getFieldDecorator('bucketName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hfile.fileUpload.model.fileUpload.bucketName').d('分组'),
                    }),
                  },
                  {
                    pattern: /^[a-z0-9-]*$/,
                    message: intl
                      .get('hfile.fileUpload.view.validation.bucketName')
                      .d('只能由小写字母、数字，"-"组成'),
                  },
                ],
                initialValue: bucketName,
              })(<Input disabled={!isCreate} trim typeCase="lower" inputChinese={false} />)}
            </FormItem>
            <FormItem
              label={intl.get('hfile.fileUpload.model.fileUpload.directory').d('上传目录')}
              {...formLayout}
            >
              {getFieldDecorator('directory', {
                initialValue: fileDetail ? fileDetail.directory : '',
              })(<Input disabled={!isCreate} />)}
            </FormItem>
            <FormItem
              label={intl.get('hfile.fileUpload.model.fileUpload.contentType').d('文件分类')}
              {...formLayout}
            >
              {getFieldDecorator('contentType', {
                rules: [
                  {
                    required: false,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hfile.fileUpload.model.fileUpload.contentType').d('文件分类'),
                      type: 'array',
                    }),
                  },
                ],
                initialValue: isEmpty(fileDetail.contentType)
                  ? []
                  : fileDetail.contentType.split(','),
              })(
                <Select allowClear onChange={this.changeContentType} mode="multiple">
                  {fileTypeList &&
                    fileTypeList.map((item) => (
                      <Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
            <FormItem
              label={intl.get('hfile.fileUpload.model.fileUpload.fileFormat').d('文件格式')}
              {...formLayout}
            >
              {getFieldDecorator('fileFormat', {
                rules: [
                  {
                    required: false,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hfile.fileUpload.model.fileUpload.fileFormat').d('文件格式'),
                      type: 'array',
                    }),
                  },
                ],
                initialValue: isEmpty(fileDetail.fileFormat)
                  ? []
                  : fileDetail.fileFormat.split(','),
              })(
                <Select allowClear mode="multiple">
                  {newFileFormat &&
                    newFileFormat.map((item) => (
                      <Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
            <FormItem
              label={intl.get('hfile.fileUpload.model.fileUpload.storageSize').d('文件大小限制')}
              {...formLayout}
            >
              {getFieldDecorator('storageSize', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hfile.fileUpload.model.fileUpload.storageSize')
                        .d('文件大小限制'),
                    }),
                  },
                  {
                    min: 0,
                    pattern: /^\d+$/,
                    message: intl
                      .get('hfile.fileAggregate.view.message.patternValidate')
                      .d('请输入大于等于0的整数'),
                  },
                ],
                initialValue: fileDetail ? fileDetail.storageSize : '',
              })(<Input type="number" addonAfter={fileMaxUnitSelector} />)}
            </FormItem>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
