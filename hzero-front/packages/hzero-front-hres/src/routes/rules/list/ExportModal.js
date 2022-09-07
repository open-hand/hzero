/**
 * @ 结算规则 - 导入配置模态框
 * @Author: NJQ <jiqangqi.nan@hand-china.com>
 * @Date: 2019-11-06
 * @Copyright: Copyright (c) 2018, Hand
 */

import React from 'react';
import { Upload, Button, Form, Output, Modal, TextArea } from 'choerodon-ui/pro';

import { getAccessToken, getCurrentOrganizationId } from 'utils/utils';
import request from 'utils/request';
import intl from 'utils/intl';
import { API_HOST, HZERO_HRES } from 'utils/config';
import notification from 'utils/notification';

import styles from './index.less';

const modalKey = Modal.key();
const modalKey1 = Modal.key();

/**
 * 导入接口配置
 * @param {string} params.fileToken
 * @param {string} params.enable
 */
export async function importIfConfig(params) {
  return request(`${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/config/cover/import`, {
    method: 'POST',
    query: { ...params },
  });
}

class ModalContent extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      importConfigResult: '',
      uploading: false,
    };
  }

  saveUpload = node => {
    this.upload = node;
  };

  render() {
    const props = {
      headers: {
        'Access-Control-Allow-Origin': '*',
        Authorization: `bearer ${getAccessToken()}`,
      },
      name: 'configFile',
      action: `${API_HOST}${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/config/import`,
      multiple: false,
      accept: ['.json'],
      extra: (
        <p style={{ margin: '8px' }}>
          {intl.get('hres.rule.view.title.please.json').d('请上传JSON配置文件(.json)')}
        </p>
      ),
      uploadImmediately: false,
      showUploadBtn: false,
      showPreviewImage: true,
      // eslint-disable-next-line consistent-return
      onUploadSuccess: res => {
        this.setState({
          uploading: false,
        });
        try {
          const response = typeof res === 'string' ? JSON.parse(res) : res;
          let responseObject = {};
          try {
            // eslint-disable-next-line prefer-destructuring
            responseObject = response[0];
          } catch (error) {
            responseObject = {};
          }
          if (!response.failed) {
            if (responseObject.status === 'END') {
              this.setState({
                importConfigResult: responseObject.message,
              });
              notification.success({ message: responseObject.message });
              return false;
              // eslint-disable-next-line no-else-return
            } else if (responseObject.status === 'ERROR') {
              Modal.open({
                key: modalKey1,
                title: intl.get('hres.rule.view.title.prompt').d('提示'),
                closable: true,
                footer: null,
                children: (
                  <div>
                    <br />
                    <p>{responseObject.message}</p>
                  </div>
                ),
                onOk: () =>
                  new Promise(resolve => {
                    const fileToken = responseObject.token;
                    const enable = 'Y';

                    importIfConfig({
                      fileToken,
                      enable,
                    })
                      .then(resp => {
                        try {
                          if (!resp.failed) {
                            this.setState({
                              importConfigResult: resp[0].message,
                            });

                            notification.success({
                              message: resp[0].message,
                            });
                          } else {
                            this.setState({
                              importConfigResult: resp.message,
                            });
                            notification.error({ message: resp.message });
                          }
                          // 延时400ms 执行
                          setTimeout(() => {
                            resolve();
                          }, 400);
                        } catch (error) {
                          setTimeout(() => {
                            resolve();
                          }, 400);
                        }
                      })
                      .catch(error => {
                        setTimeout(() => {
                          resolve();
                        }, 400);
                        notification.error({ message: error });
                      });
                  }),
                onCancel: () =>
                  new Promise(resolve => {
                    this.setState({
                      importConfigResult: '',
                    });
                    resolve();
                  }),
              });
              return true;
            } else {
              this.setState({
                importConfigResult: responseObject.message,
              });
              notification.success({ message: responseObject.message });
              return false;
            }
          } else {
            this.setState({
              importConfigResult: response.message,
            });
            notification.error({ message: response.message });
          }
        } catch (error) {
          notification.error({ message: error });
        }
      },
      onUploadError: (error, res) => {
        this.setState({
          uploading: false,
        });
        notification.error({ message: res });
      },
    };

    const handleBtnClick = () => {
      this.setState({
        importConfigResult: '',
      });
      /* this.setState({
        uploading: false,
      }); */
      return this.upload.startUpload();
    };

    return (
      <>
        <Form className={styles['output-label']}>
          <Output
            label={intl.get('hres.rule.view.title.upload.files').d('上传文件')}
            renderer={() => <Upload ref={this.saveUpload} {...props} />}
          />
          <Output
            label={intl.get('hres.rule.view.title.import.information').d('导入信息')}
            renderer={() => (
              <TextArea
                style={{ width: '100%' }}
                placeholder={intl
                  .get('hres.rule.view.title.import.configuration.result')
                  .d('导入配置输出结果')}
                readOnly
                value={this.state.importConfigResult}
              />
            )}
          />
        </Form>
        <Button
          loading={this.state.uploading}
          style={{ marginBottom: 10, width: 88, float: 'right' }}
          color="primary"
          onClick={handleBtnClick}
        >
          {intl.get('hres.rule.view.title.submission').d('提交')}
        </Button>
      </>
    );
  }
}

export default function ExportModal() {
  Modal.open({
    key: modalKey,
    title: intl.get('hres.rule.view.title.import.configuration').d('导入配置'),
    children: <ModalContent />,
    maskClosable: true,
    destroyOnClose: true,
    footer: null,
    closable: true,
  });
}
