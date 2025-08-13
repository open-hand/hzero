/* eslint-disable-next-line */
import React from 'react';
import { Route } from 'dva/router';

import { omit, isEqual } from 'lodash';
import shallowEqual from 'shallowequal';

export default class WrapperRoute extends Route {
  shouldComponentUpdate(nextProps, nextState) {
    if (!nextState || !nextState.match) {
      return false;
    }
    return (
      !shallowEqual(omit(this.props, ['computedMatch']), omit(nextProps, ['computedMatch'])) ||
      !isEqual(this.state.match, nextState.match)
    );
  }
}
