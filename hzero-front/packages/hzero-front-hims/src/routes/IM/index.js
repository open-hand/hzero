import React from 'react';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';
import { message, Avatar } from 'hzero-ui';

import { getAccessToken } from 'utils/utils';
import { API_HOST, HZERO_IM } from 'utils/config';

@connect(({ loading }) => ({
  loading,
  accessToken: getAccessToken(),
  IMLoading: loading.effects['global/baseLazyInit'],
}))
export default class IM extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      style: { width: 1, height: 1 },
    };
  }

  componentDidMount() {
    window.addEventListener('message', this.receiveMessage, false);
  }

  @Bind()
  receiveMessage(event) {
    if (event.origin === API_HOST) {
      let msgData = {};
      try {
        msgData = JSON.parse(event.data);
      } catch (evt) {
        message.error(event.data);
        return;
      }
      if (msgData.flag) {
        this.setState({
          style: { width: '100%', height: '100%', position: 'fixed', top: '0px', left: '0px' },
        });
      } else if (msgData.smFlag) {
        this.setState({
          style: {
            width: '500px',
            height: '518px',
            position: 'fixed',
            bottom: '20px',
            right: '10px',
          },
        });
      } else {
        this.setState({
          style: {
            width: '40px',
            height: '40px',
            position: 'fixed',
            bottom: '20px',
            right: '10px',
          },
        });
      }
    }
  }

  render() {
    const { IMLoading = true } = this.props;
    const { style } = this.state;
    return (
      <div>
        {!IMLoading && (
          <iframe
            title="im"
            style={{ ...style, zIndex: '1000' }}
            src={`${API_HOST}${HZERO_IM}/im?access_token=${getAccessToken()}`}
            allowTransparency="true"
            frameBorder="0"
            onLoad={() => {
              // if (isEmpty(style)) {
              //   this.setState({ style: null });
              // }
            }}
          />
        )}
        {!style.width && (
          <div
            style={{
              width: '40px',
              height: '40px',
              position: 'fixed',
              bottom: '20px',
              right: '10px',
              zIndex: '1000',
            }}
          >
            <Avatar
              size="large"
              icon="message"
              style={{
                background: '#ccc',
                lineHeight: '36px',
              }}
            />
          </div>
        )}
      </div>
    );
  }
}
