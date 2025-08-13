const {Input, Button, FormItem,Icon} = window['choerodon-ui'];
const formItemLayout = {
  labelCol: {
    xs: {span: 24},
    sm: {span: 100},
  },
  wrapperCol: {
    xs: {span: 24},
    sm: {span: 9},
  },
};

class LoginButton extends window.React.Component {
  state = {
    loading: false,
  };

  constructor(props) {
    super(props);
  }

  handleButtonClickTest = (e) => {
    this.setState({loading: true})

    $("#usernameIsNullMsg").css('display', 'none');
    $("#passwordIsNullMsg").css('display', 'none');
    $("#usernameOrPasswordNotFoundMsg").html("");
    var username = $.trim($("#username").val());
    var password = $.trim($("#password").val());
    if (username == '') {
      $("#usernameIsNullMsg").css('display', 'block');
      this.setState({loading: false})
      return;
    }
    if (password == '') {
      $("#passwordIsNullMsg").css('display', 'block');
      this.setState({loading: false})
      return;
    }
    $("#md5_password").val(this.encode(password));
    $('.login-form').submit();
  };

  encode = (password) => {
    var output = "";
    var chr1, chr2, chr3 = "";
    var enc1, enc2, enc3, enc4 = "";
    var i = 0;
    do {
      chr1 = password.charCodeAt(i++);
      chr2 = password.charCodeAt(i++);
      chr3 = password.charCodeAt(i++);
      enc1 = chr1 >> 2;
      enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
      enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
      enc4 = chr3 & 63;
      if (isNaN(chr2)) {
        enc3 = enc4 = 64;
      } else if (isNaN(chr3)) {
        enc4 = 64;
      }
      output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2)
        + keyStr.charAt(enc3) + keyStr.charAt(enc4);
      chr1 = chr2 = chr3 = "";
      enc1 = enc2 = enc3 = enc4 = "";
    } while (i < password.length);
    return output;
  }


  render() {
    return (
      <Button className="btn" onClick={this.handleButtonClickTest} funcType="raised" loading={this.state.loading}
              htmlStyle="padding-top:4px"><span>{this.state.loading ? '登录中' : '登录'}</span></Button>
    )
  }
}

class UsernameInupt extends window.React.Component {
  constructor(props) {
    super(props);
  }

  state = {
    currentUsername: '',
  };

  componentDidMount() {
    this.setState({
      currentUsername: this.getUrlParams('username'),
    })

  }

  getUrlParams = (name) => {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); //定义正则表达式
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
  }
  onValueChange = (e) => {
    this.setState({
      currentUsername: e.target.value,
    })
  }

  render() {
    return (
          <Input prefix={<Icon type="person" style={{ color: '#9f9f9f' }} />} autoFocus autoComplete="off" name="username" id="username"
             onChange={e => this.onValueChange(e)} label=" " placeholder="登录名/邮箱" defaultValue=" "
             value={this.state.currentUsername}/>
    )
  }
}

class PasswordInput extends window.React.Component {
  constructor(props) {
    super(props);
  }

  onValueChange = (e) => {
    this.setState({
      currentPassword: e.target.value,
    })
  }
  state = {
    currentPassword: '',
  };

  componentDidMount() {
    this.setState({
      currentPassword: ''
    })

  }

  render() {
    return (
      <div>
      <Input prefix={<Icon type="lock" style={{ color: '#9f9f9f' }} />} type="password" onChange={e => this.onValueChange(e)} autoComplete="off" label=" " id="password"
             placeholder="密码" defaultValue="" showPasswordEye/>
      </div>
    )
  }
}

/**
 * 渲染账号和密码的输入框
 */
ReactDOM.render(
<UsernameInupt/>,
    document.getElementById('usernameInupt'));
ReactDOM.render(
<PasswordInput/>,
    document.getElementById('passwordInupt'));
ReactDOM.render(
<LoginButton/>,
    document.getElementById('loginButton'));
if (document.getElementById('captchaInupt'))
  ReactDOM.render(
    <Input
      label="验证码"
      prefix={<Icon type="mode_edit" style={{ color: '#9f9f9f', margin: '0 10px 0 7px' }} />}
      type="text"
      style={{width: '200px'}}
      name="captcha"
      placeholder="验证码"
    />,
  document.getElementById('captchaInupt'));
