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
        {/* {expandedKeys.includes(item.key) ? (
          <svg
            t="1593310531955"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="6219"
            width="16"
            height="16"
            className={item.children ? "column-switcher-open" : "column-switcher-close"}
          >
            <path
              d="M507.58399999 736.064a32.896 32.896 0 0 1-23.35999999-9.664L110.912 353.088a33.024 33.024 0 0 1 46.72-46.656l373.312 373.248a33.024 33.024 0 0 1-23.36000001 56.384"
              fill="#333333"
              p-id="6220"
            />
            <path
              d="M507.58399999 736.064a32.896 32.896 0 0 1-23.35999999-56.32l382.144-382.08a33.024 33.024 0 0 1 46.656 46.656l-382.016 382.08a33.152 33.152 0 0 1-23.42400001 9.664"
              fill="#333333"
              p-id="6221"
            />
          </svg>
        )
          : (
            <svg
              t="1593311349926"
              viewBox="0 0 1024 1024"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              p-id="7762"
              width="16"
              height="16"
              className={item.children ? "column-switcher-open" : "column-switcher-close"}
            >
              <path
                d="M736.064 516.41600001a32.896 32.896 0 0 1-9.664 23.35999999L353.088 913.088a33.024 33.024 0 0 1-46.656-46.72l373.248-373.312a33.024 33.024 0 0 1 56.384 23.36000001"
                fill="#333333"
                p-id="7763"
              />
              <path
                d="M736.064 516.41600001a32.896 32.896 0 0 1-56.32 23.35999999l-382.08-382.144a33.024 33.024 0 0 1 46.656-46.656l382.08 382.016a33.152 33.152 0 0 1 9.664 23.42400001"
                fill="#333333"
                p-id="7764"
              />
            </svg>
          )
        } */}
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
              {this.customRender(column, item, idx) || `${item[column.key]} : ${item.type}`}
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
