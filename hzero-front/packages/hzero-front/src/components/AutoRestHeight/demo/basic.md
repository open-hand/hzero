
Basic use

#### c7n pro组件
```jsx
import AutoRestHeight from 'components/AutoRestHeight';

ReactDOM.render(
  <AutoRestHeight topSelector=".c7n-pro-table">
    <Table
      dataSet={this.props.tableDS}
      columns={this.columns}
      buttons={this.buttons}
    />
  </AutoRestHeight>,
  mountNode,
);
```

#### hzero-ui组件
```jsx
import AutoRestHeight from 'components/AutoRestHeight';

ReactDOM.render(
  <AutoRestHeight topSelector=".ant-spin-container" type="hzero-ui">
    <Table
      bordered
      rowKey="promptId"
      loading={fetchPromptLoading}
      dataSource={promptList}
      columns={promptColumns}
      pagination={pagination}
      onChange={this.handleStandardTableChange}
    />
  </AutoRestHeight>,
  mountNode,
);
```
