/* eslint-disable react/require-default-props */
/**
 * 1. 在组件卸载
 * 2. 使用 如果 配置变化 请重新赋予一个新的key
 * <OnlyOfficeEditor key={JSON.stringify(config)} {...config} />
 * 如果 不基于 hzero 的服务, 则需要自己定义自己的 OnlyOffice 组件
 * 基于 hzero 文件 的 onlyOffice 的编辑器
 * OnlyOfficeEditor
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-02
 * @copyright 2019-07-02 © HAND
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Spin } from 'hzero-ui';
import { isNil } from 'lodash';
import { Bind, Debounce } from 'lodash-decorators';

import request from 'utils/request';
import intl from 'utils/intl';
import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId } from 'utils/utils';

export default class OnlyOfficeEditor extends React.PureComponent {
  static propTypes = {
    // 必须由外面设置宽高
    height: PropTypes.oneOfType([PropTypes.string.isRequired, PropTypes.number.isRequired])
      .isRequired,
    width: PropTypes.oneOfType([PropTypes.string.isRequired, PropTypes.number.isRequired])
      .isRequired,
    // 租户id 默认值是当前租户
    organizationId: PropTypes.number,
    // 没有默认桶名, 桶名 是必须的
    bucketName: PropTypes.string.isRequired,
    // 文件的url
    url: PropTypes.string.isRequired,
    // 不是必输的
    storageCode: PropTypes.string,
    // 权限字段
    permission: PropTypes.shape({
      changeReview: PropTypes.bool,
      comment: PropTypes.bool,
      dealWithReview: PropTypes.bool,
      dealWithReviewOnly: PropTypes.bool,
      download: PropTypes.bool,
      edit: PropTypes.bool,
      print: PropTypes.bool,
      review: PropTypes.bool,
    }).isRequired,
    // 暂时不起作用
    // 编辑器 是否自动 focus
    // autoFocus: PropTypes.bool,
  };

  constructor(props) {
    super(props);
    this.state = {
      loaded: false,
      // reloading: false, // 是否 重新加载
      configErrorMessages: null, // 如果 configError 不为空, 则显示出错
      saving: false,
    };
    this.iframeRef = React.createRef();
    this.config = getEnvConfig();
  }

  componentDidMount() {
    this.loadOnlyOfficeData();
  }

  /**
   * 在编辑器卸载之前 需要触发编辑器的 onbeforeunload
   * 通过将 srcdoc 置为空字符串 触发
   */
  componentWillUnmount() {
    this.triggerDocumentSave();
  }

  /**
   * 通过修改 srcdoc 触发文档的文件保存
   * 500 ms 的延时 让 编辑器有足够的时间来保存内容到内存
   * @param {Function} callback - 保存完毕
   */
  @Debounce(500)
  @Bind()
  async triggerDocumentSave(callback) {
    const { saving } = this.state;
    if (saving) {
      return;
    }
    if (this.iframeRef.current) {
      this.iframeRef.current.contentDocument.write('');
      this.setState({
        saving: true,
      });
      setTimeout(() => {
        if (this.iframeRef.current) {
          const { srcdoc } = this.state;
          this.iframeRef.current.contentDocument.write(srcdoc);
          this.setState(
            {
              saving: false,
            },
            () => {
              callback();
            }
          );
        }
      }, 100);
    }
  }

  // // 不要支持 url 改变
  // componentDidUpdate(prevProps, prevState, snapshot) {
  //   // 比较 影响 编辑的数据是否发生变化
  //   const { organizationId: prevOrganizationId, bucketName: prevBucketName, storageCode: prevStorageCode, permission: prevPermission, url: prevUrl } = prevProps;
  //   const { organizationId, bucketName, storageCode, permission, url } = this.props;
  //   const configProps = { organizationId, bucketName, storageCode, permission, url };
  //   const prevConfigProps = {
  //     organizationId: prevOrganizationId,
  //     bucketName: prevBucketName,
  //     storageCode: prevStorageCode,
  //     permission: prevPermission,
  //     url: prevUrl,
  //   };
  //   if (JSON.stringify(configProps) !== JSON.stringify(prevConfigProps)) {
  //     this.setState({ reloading: true }, () => {
  //       this.loadOnlyOfficeData();
  //     });
  //   }
  // }

  loadOnlyOfficeData() {
    if (!this.checkConfigError()) {
      return;
    }
    const {
      organizationId = getCurrentOrganizationId(),
      bucketName,
      storageCode,
      permission,
      url,
    } = this.props;
    const { HZERO_FILE } = this.config;
    request(`${HZERO_FILE}/v1/${organizationId}/only-office/url`, {
      method: 'POST',
      body: {
        bucketName,
        storageCode,
        permission,
        url,
      },
      // 期望返回类型是 文本
      responseType: 'text',
    }).then((res) => {
      if (typeof res === 'string') {
        // 是文本
        this.setState({
          loaded: true,
          // reloading: false,
          srcdoc: res,
        });
        // this.iframeRef.current.srcdoc=res;
        this.iframeRef.current.contentDocument.write(res);
      }
    });
  }

  checkConfigError() {
    const { bucketName, permission, url } = this.props;
    const errors = [];
    if (isNil(bucketName)) {
      errors.push(intl.get('hzero.common.validation.notNull', { name: 'bucketName' }));
    }
    if (isNil(permission)) {
      errors.push(intl.get('hzero.common.validation.notNull', { name: 'permission' }));
    }
    if (isNil(url)) {
      errors.push(intl.get('hzero.common.validation.notNull', { name: 'url' }));
    }
    if (errors.length > 0) {
      this.setState({
        configErrorMessages: errors,
      });
      return false;
    }
    const { configErrors } = this.state;
    if (configErrors) {
      this.setState({
        configErrorMessages: null,
      });
    }
    return true;
  }

  render() {
    const { width, height, url } = this.props;
    const {
      loaded,
      // srcdoc,
      // reloading, 不要支持 reload
      configErrorMessages,
    } = this.state;
    let content;
    if (configErrorMessages !== null) {
      content = (
        <div
          style={{
            width,
            height,
          }}
        >
          <ul>
            {configErrorMessages.map((errorMessage) => (
              <li key={errorMessage}>{errorMessage}</li>
            ))}
          </ul>
        </div>
      );
    } else if (loaded) {
      content = <iframe ref={this.iframeRef} title={url} width={width} height={height} />;
    } else {
      content = <Spin width={width} height={height} spinning />;
    }
    return content;
  }
}
