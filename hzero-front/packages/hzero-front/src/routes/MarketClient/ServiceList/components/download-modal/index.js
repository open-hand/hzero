import React, { useState } from 'react';
import { CLIENT_JUMP_URL } from 'utils/market-client';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { downloadFile } from 'hzero-front/lib/services/api';
import { Modal, Row, Col } from 'hzero-ui';
import { HZERO_ADM } from 'utils/config';

function DownloadModal({ downloadRecord, handleCancel = () => {} }) {
  const [downloading, setDownloading] = useState(false);

  if (!downloadRecord) return null;
  const { artifactId, groupId, version, serviceId } = downloadRecord;

  // 处理点击确定
  const handleOk = () => {
    setDownloading(true);

    const queryParams = [
      { name: 'version', value: version },
      { name: 'groupId', value: groupId },
      { name: 'artifactId', value: artifactId },
    ];
    downloadFile({
      requestUrl: `${HZERO_ADM}/v1/market/server/download/${serviceId}`,
      queryParams,
    }).then(() => {
      setTimeout(() => {
        setDownloading(false);
        handleCancel();
      }, 800);
    });
  };

  return (
    <Modal
      width={520}
      title={intl.get('hadm.marketclient.view.service.download.modal.title').d('下载本地服务数据')}
      closable={false}
      confirmLoading={downloading}
      onCancel={() => {
        document.body.onclick = null;
        if (handleCancel) handleCancel();
      }}
      visible={!!(downloadRecord && Object.keys(downloadRecord).length)}
      onOk={handleOk}
    >
      <Row>
        <Col
          offset={1}
          span={4}
          style={{ textAlign: 'right', boxSizing: 'border-box', paddingRight: '8px' }}
        >
          {intl.get('hadm.marketclient.view.service.download.modal.service').d('对比服务')}：
        </Col>
        <Col span={17} style={{ background: '#FAFAFA', boxSizing: 'border-box', padding: '8px' }}>
          <p>GroupId：{groupId}</p>
          <p>ArtifactId: {artifactId}</p>
          <p style={{ margin: 0 }}>Version：{version}</p>
        </Col>
      </Row>
      <Row style={{ marginTop: '24px' }}>
        <Col
          offset={1}
          span={4}
          style={{ textAlign: 'right', boxSizing: 'border-box', paddingRight: '8px' }}
        >
          {intl.get('hadm.marketclient.view.service.download.modal.address').d('对比地址')}：
        </Col>
        <Col span={17} style={{ background: '#FAFAFA', boxSizing: 'border-box', padding: '8px' }}>
          <div style={{ fontSize: '14px', color: '#4F7DE7', marginBottom: '12px' }}>
            <a
              href={`${CLIENT_JUMP_URL}/my-product/my-authorize/version-compare?compareType=OFFLINE`}
              rel="noopener noreferrer"
              target="_blank"
            >
              {`${CLIENT_JUMP_URL}/my-product/my-authorize/version-compare?compareType=OFFLINE`}
            </a>
          </div>
          <div
            style={{
              fontSize: '12px',
              color: '#9B9B9B',
              textAlign: 'left',
              lineHeight: '18px',
            }}
          >
            {intl
              .get('hadm.marketclient.view.service.download.modal.tips')
              .d(
                '注：下载本地服务数据文件后，前往该地址上传该服务数据，即可将本地服务版本与新版本进行对比'
              )}
          </div>
        </Col>
      </Row>
    </Modal>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient', 'hzero.c7nProUI'],
})(DownloadModal);
