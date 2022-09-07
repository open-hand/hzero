import React from 'react';
import { connect } from 'dva';
import { Spin } from 'hzero-ui';
import qs from 'query-string';

import { cleanMenuTabs } from 'utils/menuTab';

@connect(({ tenantRedirect = {}, loading = {} }) => ({
  tenantRedirect,
  redirectLoading: loading.effects['tenantRedirect/redirect'],
}))
export default class TenantRedirect extends React.Component {
  componentDidMount() {
    const { dispatch, location = {}, history } = this.props;
    const { search = {} } = location;
    const searchParams = qs.parse(search) || {};
    dispatch({
      type: 'tenantRedirect/redirect',
      payload: searchParams,
    }).then(res => {
      if (res) {
        cleanMenuTabs();
        history.push(res.redirect);
        // window.location.reload();
        dispatch({
          type: 'user/fetchCurrent',
        });
      }
    });
  }

  render() {
    const { redirectLoading = false } = this.props;
    return <Spin spinning={redirectLoading} style={{ marginTop: 20 }} />;
  }
}
