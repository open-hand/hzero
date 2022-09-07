/* eslint-disable react/no-array-index-key */
import React from 'react';
import Sortable from 'sortablejs';
import Columns from './Columns';

class TargetData extends React.Component {
  boxEle;

  constructor(props) {
    super(props);
    this.state = {
      activeKey: null,
      sorting: false,
    };
  }

  // 由于sortablejs直接操作dom，不符合受控组件逻辑，现在每次改变排序一次，render触发4次：
  // 1、sortjs改变dom；
  // 2、改变受控组件原始数据排序；
  // 3、由于受控组件直接改变了原始数据的排序，所以sortablejs改变的sort需要还原
  // 4、sort还原后 需要重新触发render，改变currentActive位置
  // 后续优化
  componentDidMount() {
    const { isSort } = this.props;
    const ele = this.boxEle.querySelector('.column-content');
    let order = [];
    if (isSort) {
      const sortable = new Sortable(ele, {
        onStart: () => {
          this.setState({
            sorting: true,
          });
        },
        onEnd: (evt) => {
          sortable.sort(order); // sortablejs排序还原
          this.props.changeData(evt.oldIndex, evt.newIndex);
          this.setState({
            sorting: false,
          });
        },
      });
      order = sortable.toArray();
    }
  }

  show(data, relation, iconStatus) {
    return data.map((item) => {
      const temp = item;
      let iconShow = iconStatus ? 'inherit' : 'hidden';
      relation.forEach((n) => {
        if ((n.target && n.target.key) === temp.key) {
          iconShow = 'inherit';
        }
      });
      temp.iconShow = iconShow;
      if (temp.children) {
        this.show(temp.children, relation, iconStatus);
      }
      return temp;
    });
  }

  isActive(key) {
    const { currentRelation } = this.props;
    if (this.state.activeKey === key) {
      return 'active';
    } else if (currentRelation.target && currentRelation.target.key === key) {
      return 'active';
    }
    return '';
  }

  eventHandle(item, type, activeKey) {
    if (!this.state.sorting) {
      this.setState(
        {
          activeKey,
        },
        () => {
          this.props.overActive(item, 'target', type);
        }
      );
    }
  }

  treeRender(list = [], columns = [], columnOpt = () => {}, edit, sorting, temps = []) {
    const treeList = temps;
    list.forEach((item) => {
      const temp = item;
      treeList.push(
        <Columns
          columns={columns}
          key={`target_${temp.key}`}
          columnOpt={columnOpt}
          sorting={sorting}
          edit={edit}
          item={temp}
          index={temp.index}
          type="target"
        />
      );
      if (temp.children) {
        this.treeRender(item.children, columns, columnOpt, edit, sorting, treeList);
      }
    });
    return treeList;
  }

  render() {
    const { columns, data, iconStatus, relation, edit } = this.props;
    const columnOpt = (item, index) => {
      return {
        'data-id': index,
        key: `target_${index}`,
        'data-key': item.key,
        className: this.isActive(item.key),
        onMouseEnter: this.eventHandle.bind(this, item, 'enter', item.key),
        onMouseLeave: this.eventHandle.bind(this, item, 'leave', null),
      };
    };
    const renderContent = this.show(data, relation, iconStatus);
    return (
      <div
        className="target-data"
        ref={(me) => {
          this.boxEle = me;
        }}
      >
        <ul className="column-title">
          <li>
            {columns.map((column, idx) => {
              return (
                <span
                  key={idx}
                  className="column-item"
                  title={column.title}
                  style={{
                    width: column.width,
                    textAlign: column.align,
                  }}
                >
                  {column.title}
                </span>
              );
            })}
          </li>
        </ul>
        <ul className="column-content">
          {/* {renderContent.map((item, index) => {
            return (
              <Columns
                columns={columns}
                key={`target${index}`}
                columnOpt={columnOpt}
                sorting={this.state.sorting}
                edit={edit}
                item={item}
                index={index}
                type="target"
              />
            );
          })} */}
          {this.treeRender(renderContent, columns, columnOpt, edit, this.state.sorting)}
        </ul>
      </div>
    );
  }
}

export default TargetData;
