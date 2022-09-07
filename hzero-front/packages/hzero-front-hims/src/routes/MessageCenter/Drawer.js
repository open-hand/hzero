import React from 'react';

export default class Drawer extends React.Component {
  render() {
    const { editData = {} } = this.props;
    return <pre>{editData.content}</pre>;
  }
}
