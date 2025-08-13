import React, { Component } from 'react';
import { Provider } from 'mobx-react';

import { getCurrentLanguage, getCurrentRole, getCurrentTenant } from 'utils/utils';

import { LowCodeSharedComponent } from '../../customize/lowCode';

export default class LowCodeProvider extends Component {
  render() {
    const { level = 'site' } = getCurrentRole();
    const { tenantId = 0, tenantName } = getCurrentTenant();
    const language = getCurrentLanguage();
    const currentLanguage = language.replace('-', '_');
    const AppState = {
      currentLanguage,
      currentMenuType: {
        type: level,
        organizationId: tenantId,
        id: tenantId,
        name: tenantName,
      },
    };

    return (
      <Provider AppState={AppState}>
        <LowCodeSharedComponent componentCode="ModelProvider" componentProps={this.props} />
      </Provider>
    );
  }
}
