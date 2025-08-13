/**
 * 分片上传
 * @email CJ <jing.chen04@hand-china.com>
 * @creationDate 2020/02/19
 * @copyright HAND ® 2019
 */

import * as React from 'react';

import { Modal as HzeroModal, Button } from 'hzero-ui';

import intl from 'utils/intl';
import { API_HOST } from 'utils/config';
import { getAccessToken } from 'utils/utils';

const FileChunkUpload = props => {
  const [hzeroModalVisible, setHzeroModalVisible] = React.useState(false);
  const { title, disabled = false, style, organizationId, modalProps, prefixPatch } = props;
  const accessToken = getAccessToken();
  const iframeUrl = React.useMemo(() => {
    const configure = {
      href: '',
      search: '',
    };
    configure.href = `${API_HOST}${prefixPatch}/v1/${organizationId}/upload`;
    const searchObject = {
      access_token: accessToken,
      templateCode: props.templateCode,
    };
    configure.search = Object.keys(searchObject)
      .filter(searchKey => searchObject[searchKey] !== undefined)
      .map(searchKey => `${searchKey}=${searchObject[searchKey]}`)
      .join('&');
    return `${configure.href}?${configure.search}`;
  }, [organizationId, accessToken, JSON.stringify(props)]);
  const wrapStyle = React.useMemo(() => ({ height: '100%', width: '100%', border: 0, ...style }), [
    style,
  ]);
  const handleFileChunkUploadClick = React.useCallback(() => {
    if (disabled) {
      return;
    }
    setHzeroModalVisible(true);
  }, [iframeUrl, disabled, setHzeroModalVisible, wrapStyle]);
  const closeHzeroModal = React.useCallback(() => {
    setHzeroModalVisible(false);
  }, [setHzeroModalVisible]);

  return (
    <>
      <Button icon="upload" onClick={handleFileChunkUploadClick}>
        {intl.get(`himp.comment.view.button.pageUpload`).d('分片上传')}
      </Button>
      {hzeroModalVisible && (
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
    </>
  );
};

export default FileChunkUpload;
