/**
 * 文件切片上传
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/26
 * @copyright HAND ® 2019
 */

import * as React from 'react';

import { Modal as HzeroModal } from 'hzero-ui';
import { Modal } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { BKT_PUBLIC, HZERO_FILE } from 'utils/config';
import { getAccessToken } from 'utils/utils';

const modalKey = Modal.key();

const btnDisabledStyle = {
  cursor: 'not-allowed',
};
const btnStyle = {
  cursor: 'pointer',
};

const FileChunkUploader = (props) => {
  const [hzeroModalVisible, setHzeroModalVisible] = React.useState(false);
  const {
    type,
    componentType,
    title,
    disabled = false,
    children,
    style,
    organizationId,
    modalProps,
  } = props;
  const accessToken = getAccessToken();
  const iframeUrl = React.useMemo(() => {
    const configure = {
      isBucket: false,
      href: '',
      search: '',
    };
    if (props.type === 'bucket') {
      configure.isBucket = true;
      configure.href = `${HZERO_FILE}/v1/${organizationId}/upload`;
      const searchObject = {
        access_token: accessToken,
        bucketName: props.bucketName || BKT_PUBLIC,
        directory: props.directory,
        storageCode: props.storageCode,
      };
      configure.search = Object.keys(searchObject)
        .filter((searchKey) => searchObject[searchKey] !== undefined)
        .map((searchKey) => `${searchKey}=${searchObject[searchKey]}`)
        .join('&');
    } else {
      configure.isBucket = false;
      configure.href = `${HZERO_FILE}/v1/${organizationId}/upload`;
      const searchObject = {
        access_token: accessToken,
        path: props.path,
        configCode: props.configCode,
      };
      configure.search = Object.keys(searchObject)
        .filter((searchKey) => searchObject[searchKey] !== undefined)
        .map((searchKey) => `${searchKey}=${searchObject[searchKey]}`)
        .join('&');
    }
    return `${configure.href}?${configure.search}`;
  }, [HZERO_FILE, organizationId, type, accessToken, JSON.stringify(props)]);
  const wrapStyle = React.useMemo(() => ({ height: '100%', width: '100%', border: 0, ...style }), [
    style,
  ]);
  const handleFileChunkUploaderClick = React.useCallback(() => {
    if (disabled) {
      return;
    }
    if (componentType === 'c7n') {
      Modal.open({
        key: modalKey,
        drawer: true,
        ...modalProps,
        children: <iframe style={wrapStyle} src={iframeUrl} title={title} />,
      });
    } else {
      setHzeroModalVisible(true);
    }
  }, [iframeUrl, disabled, componentType, setHzeroModalVisible, wrapStyle]);
  const closeHzeroModal = React.useCallback(() => {
    setHzeroModalVisible(false);
  }, [setHzeroModalVisible]);

  return (
    <div>
      {React.isValidElement(children) ? (
        React.cloneElement(children, { onClick: handleFileChunkUploaderClick })
      ) : (
        <a onClick={handleFileChunkUploaderClick} style={disabled ? btnDisabledStyle : btnStyle}>
          {intl.get('hfile.component.fileChunkUploader.view.upload').d('上传文件')}
        </a>
      )}
      {componentType === 'hzero' && hzeroModalVisible && (
        <HzeroModal
          visible
          destroyOnClose
          title={title}
          wrapClassName="ant-modal-sidebar-right"
          transitionName="move-right"
          {...modalProps}
          onCancel={closeHzeroModal}
          onOk={closeHzeroModal}
        >
          <iframe style={wrapStyle} src={iframeUrl} title={title} />
        </HzeroModal>
      )}
    </div>
  );
};

export default FileChunkUploader;
