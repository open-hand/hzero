import React, { useState, forwardRef, useEffect } from 'react';
import { Upload, Icon, Button, notification } from 'hzero-ui';
// import { API_HOST } from '@/constants/config';
// import { getOrgId } from '@/utils/global';
import { getAccessToken } from 'utils/utils/token';
import { API_HOST, HZERO_ADM } from 'utils/config';
import { isArray, isValidArray } from '../../utils/base';

function BaseUpload(
  {
    multiple = false,
    value = [],
    size = 30 * 1024 * 1024, // 默认最大为30M
    onChange,
    text,
    extra,
    beforeComponent,
    buttonProps = {},
    num = 3, // 多个上传是，上传个数限制
    ...rest
  },
  ref
) {
  const [files, setFiles] = useState(value || []);

  // 清除缓存
  useEffect(() => {
    if (value && value.length === 0) {
      setFiles([]);
    }
  }, [value ? value[0] : null]); // eslint-disable-line

  // eslint-disable-next-line no-unused-vars,@typescript-eslint/no-unused-vars
  const _onChange = ({ file, _, fileList }) => {
    const { uid, name, response, status } = file;

    if (status === 'done' && response.failed) {
      notification.error({ message: response.message });
    }

    if (multiple) {
      // 多文件上传
      if (file.size < size) {
        // 过滤上传失败的文件
        const successArr = fileList.filter((one) => !one?.response?.failed);
        setFiles(successArr.slice(-num));
      } else {
        notification.error({ message: '文件大小超过最大限制！' });
      }

      if (status === 'done' && !response.failed) {
        const newFile = {
          ...file,
          uid,
          fileKey: response.fileKey,
          fileName: name,
          status: 'done',
          response,
        };
        const arr =
          isValidArray(value) && value.length < num
            ? [...value, newFile]
            : [...value.slice(-num + 1), newFile];
        onChange(arr);
      } else if (status === 'removed') {
        const arr = isArray(value) && value.filter((one) => one.uid !== file.uid);
        onChange(arr);
      }
    } else {
      // 单文件上传
      if (file.size < size) {
        setFiles(fileList.slice(fileList.length - 1));
      } else {
        notification.error({ message: '文件大小超过最大限制！' });
      }

      if (status === 'done' && !response.failed) {
        onChange([response.fileKey, name, response]);
      } else if (status === 'removed') {
        onChange(null);
      }
    }
  };

  return (
    <Upload
      multiple={false}
      action={`${API_HOST}${HZERO_ADM}/v1/market/work-order/secret-multipart`}
      data={() => ({
        bucketName: 'doc',
        directory: 'doc_classify',
      })}
      fileList={files}
      onChange={_onChange}
      showUploadList={{
        showPreviewIcon: true,
        showDownloadIcon: false,
        showRemoveIcon: true,
      }}
      headers={{
        Authorization: `bearer ${getAccessToken()}`,
      }}
      {...rest}
      ref={ref}
    >
      {beforeComponent}
      <Button {...buttonProps} disabled={files.length >= num}>
        <Icon type="upload" />
        {text || '上传文件'}
      </Button>
      {extra}
    </Upload>
  );
}
export default forwardRef(BaseUpload);
