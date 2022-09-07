import { Tooltip } from 'choerodon-ui';
import React from 'react';
import { isElement } from 'react-dom/test-utils';

class Columns extends React.Component {
  customRender(opts, data, idx) {
    const { key, render } = opts;
    let result = false;
    if (isElement(render) || typeof render === 'string') {
      result = render;
    } else if (typeof render === 'function') {
      result = render(data[key], data, idx);
    }
    return result;
  }

  render() {
    const { item, index, columnOpt, sorting, columns, type, edit } = this.props;
    return (
      <li {...columnOpt(item, index)}>
        <span style={{ width: (item.level - 1) * 20 }} />
        {columns.map((column, idx) => {
          return (
            <span
              key={column.key}
              className="column-item"
              style={{
                width: column.width,
                textAlign: column.align,
              }}
              title={item[column.key] || ''}
            >
              {this.customRender(column, item, idx) || (
                <Tooltip title={`${item[column.key]} : ${item.type}`}>
                  {`${item[column.key]} : ${item.type}`}
                </Tooltip>
              )}
            </span>
          );
        })}
        {index !== 0 && (
          <div
            style={{ visibility: edit && item.iconShow }}
            className={`column-icon ${type}-column-icon ${sorting ? 'sorting' : ''} ${
              edit ? '' : 'disabled'
            }`}
          />
        )}
      </li>
    );
  }
}

export default Columns;
