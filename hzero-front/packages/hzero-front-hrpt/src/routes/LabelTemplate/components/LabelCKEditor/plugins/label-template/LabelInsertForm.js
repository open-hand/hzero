/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/5
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { DataSet, Form, NumberField, Select, TextField, ColorPicker } from 'choerodon-ui/pro';
import uuid from 'uuid/v4';

import UploadButton from 'components/Upload/UploadButton';

import { BKT_PUBLIC } from 'utils/config';

import { labelTemplateLabelInsertConfig } from '@/stores/labelTemplateDS';

import styles from './styles.less';

function buildNormalOptions(options = []) {
  return options.map(({ value, meaning }) => (
    <Select.Option value={value} key={value}>
      {meaning}
    </Select.Option>
  ));
}

const LabelInsertForm = ({ isCreate = true, initialData, interactedRef }) => {
  // eslint-disable-next-line no-unused-vars
  const [_, forceUpdate] = React.useState();
  const { ds, options, constants, srcFileList } = React.useMemo(() => {
    const config = labelTemplateLabelInsertConfig();
    const labelDS = new DataSet({
      ...config.ds,
    });
    labelDS.create(
      isCreate
        ? {
            shape: config.constants.shape.rectangle,
            type: config.constants.type.text,
            sourceType: config.constants.sourceType.text,
            /* circle use width/height to represent ellipse */ width: 100,
            height: 100,
            /* 文本 */ text: 'label text',
            /* 参数编码 */ param: '',
            /* 上传图片 */ src: '',
            borderWidth: 1,
            borderColor: '#000000',
          }
        : {
            ...initialData,
            borderWidth: initialData.borderWidth || 0,
            borderColor: initialData.borderColor || '',
          }
    );
    return {
      ds: labelDS,
      constants: config.constants,
      options: config.options,
      srcFileList: isCreate ? [] : [{ url: initialData.src, uid: '-1', status: 'done' }],
    };
  }, []);
  const handleImgUploadSuccess = React.useCallback(
    file => {
      ds.current.set('src', file.response || file.url || file.thumbUrl);
    },
    [ds]
  );
  React.useEffect(() => {
    const listenUpdate = ({ name }) => {
      if (['shape', 'type', 'sourceType'].includes(name)) {
        forceUpdate(uuid());
      }
    };
    ds.addEventListener('update', listenUpdate);
    return () => {
      ds.removeEventListener('update', listenUpdate);
    };
  }, [ds]);
  React.useImperativeHandle(interactedRef, () => ({
    async getValidateData() {
      // 获取数据, ds will validate all fields
      const data = ds.toData()[0];
      let valid = false;

      function validateWidthHeight() {
        return data.width > 0 && data.height > 0;
      }

      /**
       * 同时校验 sourceType 对应的值;
       * 同时检验 code
       */
      function validateSourceType() {
        if (!data.code) {
          return false;
        }
        switch (data.sourceType) {
          case constants.sourceType.text:
            return !!data.text;
          case constants.sourceType.param:
            return !!data.param;
          case constants.sourceType.upload:
            return !!data.src;
          default:
            return false;
        }
      }

      switch (data.shape) {
        case constants.shape.horizontalLine:
          if (validateWidthHeight()) {
            valid = true;
          }
          break;
        case constants.shape.verticalLine:
          if (validateWidthHeight()) {
            valid = true;
          }
          break;
        case constants.shape.rectangle:
          if (validateWidthHeight() && validateSourceType()) {
            valid = true;
          }
          break;
        case constants.shape.circle:
          if (validateWidthHeight() && validateSourceType()) {
            valid = true;
          }
          break;
        default:
          valid = false;
      }
      if (valid) {
        return data;
      }
    },
  }));
  const data = ds.current.toData();
  const otherFields = [];
  if ([constants.shape.circle, constants.shape.rectangle].includes(data.shape)) {
    otherFields.push(
      <Select key="type" name="type">
        {buildNormalOptions(options.type)}
      </Select>,
      <TextField key="code" name="code" />,
      <Select key="sourceType" name="sourceType">
        {buildNormalOptions(
          options.sourceType.filter(
            ({ value }) =>
              // 如果为图片就 可以使用所有的输入方式, 否则 只能选择 表达式 手动输入
              data.type === constants.type.img || value !== constants.sourceType.upload
          )
        )}
      </Select>,
      <NumberField key="borderWidth" name="borderWidth" />,
      <ColorPicker key="borderColor" name="borderColor" />
    );
    if (constants.sourceType.text === data.sourceType) {
      otherFields.push(<TextField key="text" name="text" />);
    }
    if (constants.sourceType.param === data.sourceType) {
      otherFields.push(<TextField key="param" name="param" />);
    }
    if (constants.sourceType.upload === data.sourceType) {
      otherFields.push(
        <UploadButton
          single
          key="src"
          name="src"
          fileType="image/jpeg,image/png"
          onUploadSuccess={handleImgUploadSuccess}
          fileList={srcFileList}
          bucketName={BKT_PUBLIC}
        />
      );
    }
  }
  return (
    <Form columns={1} dataSet={ds} className={styles['label-editor']}>
      <Select key="shape" name="shape">
        {buildNormalOptions(options.shape)}
      </Select>
      <NumberField key="width" name="width" min={1} />
      <NumberField key="height" name="height" min={1} />
      {otherFields}
    </Form>
  );
};

export default LabelInsertForm;
