import React from 'react';
import { Bind } from 'lodash-decorators';

import { loadIMAsync, watchIMSetComponent } from '../../../customize/IM';

export default class IM extends React.Component {
  state = { InstantMessaging: { component: null } };

  componentDidMount() {
    this.unListener = watchIMSetComponent(() => {
      this.importCard().then(res => {
        const item = res;
        if (item) {
          if (item.__esModule) {
            const InstantMessaging = item.default;
            this.setState({
              InstantMessaging: {
                component: <InstantMessaging />,
              },
            });
          } else {
            const InstantMessaging = item;

            this.setState({
              InstantMessaging: {
                component: <InstantMessaging />,
              },
            });
          }
        } else {
          this.setState({
            InstantMessaging: {
              component: null,
            },
          });
        }
      });
    }, true);
  }

  componentWillUnmount() {
    if (this.unListener) {
      this.unListener();
    }
  }

  @Bind()
  async importCard() {
    let loadCard = null;
    try {
      loadCard = await loadIMAsync('HzeroIM');
    } catch (e) {
      loadCard = null;
    }
    return loadCard;
  }

  @Bind()
  renderCard() {
    const { InstantMessaging } = this.state;
    if (InstantMessaging && InstantMessaging.component) {
      return <div>{InstantMessaging.component}</div>;
    } else {
      return <div />;
    }
  }

  render() {
    return <div>{this.renderCard()}</div>;
  }
}
