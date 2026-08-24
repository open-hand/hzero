/**
 * index
 * @date 2018/9/28
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import PropTypes from 'prop-types';
import { omit } from 'lodash';
import ComposeTableEditModal from './ComposeTableEditModal';

export default class ComposeTable extends React.PureComponent {
  static propTypes = {
    // onRemove: PropTypes.func.isRequired,
    rowKey: PropTypes.string.isRequired,
    fields: PropTypes.arrayOf(PropTypes.object).isRequired,
    organizationId: PropTypes.number.isRequired,
  };

  render() {
    const composeTableProps = omit(this.props, ['editMode']);
    return <ComposeTableEditModal {...composeTableProps} />;
  }
}
