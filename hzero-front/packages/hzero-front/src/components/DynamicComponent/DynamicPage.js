/**
 * DynamicPage.js
 * @date 2018/10/22
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import PropTypes from 'prop-types';
import { map, isFunction } from 'lodash';
import { withRouter } from 'dva/router';

import { getResponse, getCurrentOrganizationId } from 'utils/utils';
import request from 'utils/request';

// import { queryConfigsByPageCode } from 'hzero-front-hpfm/lib/services/uiPageService';

import DynamicComponent from './index';

function queryConfigsByPageCode(organizationId, pageCode) {
  return request(`hpfm/v1/${organizationId}/ui-pages/common/${pageCode}`, {
    method: 'GET',
  });
}

@withRouter
export default class DynamicPage extends React.Component {
  request = request;

  getResponse = getResponse;

  ref = {}; // 存储 DynamicComponent 对应组件的ref;

  static defaultProps = {
    pageCode: undefined,
    pageConfig: undefined,
    organizationId: undefined,
  };

  static propTypes = {
    pageCode: PropTypes.string,
    pageConfig: PropTypes.object,
    organizationId: PropTypes.number, // 租户
  };

  state = {
    isStaticConfig: false,
    templates: [], // 模板
  };

  constructor(props) {
    super(props);
    this.onRef = this.onRef.bind(this);

    const { pageConfig, script } = props;

    const initTemplates = (pageConfig && pageConfig.fields) || [];
    this.state = {
      isStaticConfig: !!pageConfig,
      templates: initTemplates,
    };
    if (script) {
      // eslint-disable-next-line
      eval(script)(this);
    }
  }

  render() {
    const { params } = this.props;
    const { isStaticConfig } = this.state;
    let { templates } = this.state;
    if (isStaticConfig) {
      templates = (this.props.pageConfig && this.props.pageConfig.fields) || [];
    }
    return (
      <>
        {map(templates, template => (
          <DynamicComponent
            params={params}
            template={template}
            onRef={this.onRef(template)}
            key={template.templateCode}
            context={this}
          />
        ))}
      </>
    );
  }

  componentDidMount() {
    const { isStaticConfig } = this.state;
    const { onRef } = this.props;
    if (!isStaticConfig) {
      this.fetchPageConfig();
    }
    if (onRef && isFunction(onRef)) {
      onRef(this);
    }
  }

  /**
   * @param {Object} template - 模板的配置
   * @returns {Function} 将 DynamicComponent 对应组件的this设置到 this.ref 中
   */
  onRef(template) {
    return tplRef => {
      this.ref[template.templateCode] = tplRef;
    };
  }

  /**
   * 获取 page 对应的 配置
   */
  fetchPageConfig() {
    const { pageCode, organizationId = getCurrentOrganizationId() } = this.props;
    if (pageCode) {
      queryConfigsByPageCode(organizationId, pageCode).then(res => {
        const page = getResponse(res) || {};
        // todo 在这里需要设置 script
        if (page.script) {
          // eslint-disable-next-line
          eval(page.script)(this);
        }
        this.setState({
          templates: page.fields || [],
        });
      });
    }
  }
}
