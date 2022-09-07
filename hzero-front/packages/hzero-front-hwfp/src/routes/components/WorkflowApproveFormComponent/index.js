import React from 'react';
import { withRouter } from 'dva/router';
import { Spin } from 'hzero-ui';
import intl from 'utils/intl';
import { resolveRequire } from 'utils/utils';
import { loadWorkflowApproveFormAsync } from 'hzero-front/lib/customize/workflowApproveForm';

@withRouter
export default class workflowApproveFormComponent extends React.Component {
  constructor(props) {
    super(props);
    const { code, formKey } = props;
    this.state = {};
    this.workflowApproveFormComponent = React.lazy(async () => {
      // lazy must be () => import();
      // import is __esModule, so there return __esModule
      const ctor = await loadWorkflowApproveFormAsync(formKey);
      const ctorTemp = await loadWorkflowApproveFormAsync(code);
      return Promise.resolve({
        __esModule: true,
        default:
          resolveRequire(ctor) ||
          resolveRequire(ctorTemp) ||
          (() => (
            <div style={{ color: 'grey' }}>
              {intl
                .get('hwfp.approveForm.view.message.title.notFound', {
                  code,
                })
                .d(`未找到对应编码为“${code}”的审批表单`)}
            </div>
          )),
      });
    });
  }

  parseUrlParams(originUrl, realUrl) {
    const params = {};
    if (originUrl && realUrl && originUrl.indexOf('${') !== -1) {
      const originPathParts = originUrl.split('?')[0].split('/');
      const realPathParts = realUrl.split('?')[0].split('/');
      originPathParts.forEach((item, index) => {
        if (/\$\{.*\}/.test(item)) {
          const key = item.replace('${', '').replace('}', '');
          params[key] = realPathParts[index];
        }
      });
    }
    return params;
  }

  render() {
    const { match = {}, location = {}, history = {}, originFormKey, formKey } = this.props;
    const newLocation = {
      pathname: formKey.split('?')[0],
      search: formKey.indexOf('?') === -1 ? '' : formKey.substr(formKey.indexOf('?')),
    };
    const newMatch = {
      params: this.parseUrlParams(originFormKey, formKey),
      path: originFormKey,
      url: formKey,
    };
    const newProps = {
      ...this.props,
      history: {
        ...history,
        location: {
          ...history.loation,
          ...newLocation,
        },
      },
      location: {
        ...location,
        ...newLocation,
      },
      match: {
        ...match,
        ...newMatch,
      },
    };
    const InCustomizeComponent = this.workflowApproveFormComponent;
    if (InCustomizeComponent) {
      return (
        <React.Suspense fallback={<Spin spinning />}>
          <InCustomizeComponent {...newProps} />
        </React.Suspense>
      );
    }
    // maybe loading
    return null;
  }
}
