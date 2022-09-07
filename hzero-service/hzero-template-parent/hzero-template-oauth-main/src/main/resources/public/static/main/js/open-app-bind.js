$(document).ready(function () {
  var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="; // 由于 这边只会有一个组件的实例, 且 babel 转化的 class 有问题, 所以state放在这边

  if (typeof Object.assign != 'function') {
    Object.assign = function (target) {
      'use strict';
      if (target == null) {
        throw new TypeError('Cannot convert undefined or null to object');
      }

      target = Object(target);
      for (var index = 1; index < arguments.length; index++) {
        var source = arguments[index];
        if (source != null) {
          for (var key in source) {
            if (Object.prototype.hasOwnProperty.call(source, key)) {
              target[key] = source[key];
            }
          }
        }
      }
      return target;
    };
  }
  
  var storeData = {
    initData: {},
    // 初始值
    rootEl: null,
    // 挂载的节点
    loginAccount: {} // 账号登录的状态

  };

  function initValidate() {
    $('#bindFormAccount').validate({
      errorPlacement: function errorPlacement(error, element) {
        error.addClass("has-error has-error-msg");
        error.appendTo(element.parent());
      },
      onkeyup: false,
      onclick: false,
      onfocusout: false,
      rules: {
        username: "required",
        password: "required",
        captcha: "required"
      },
      messages: {
        username: storeData.initData.userNameMessage,
        password: {
          required: storeData.initData.passwordMessage
        },
        captcha: storeData.initData.captchaMessage
      }
    });
  }

  function validate(selector, form) {
    var flag = $(form).validate().element($(selector));

    if (flag) {
      $(selector).parents(".bind-input-content").removeClass("has-error");
    } else {
      $(selector).parents(".bind-input-content").addClass("has-error");
    }

    return flag;
  }
  /**
   * 根据传递进来的数据 初始化 store
   */


  function initInitialData() {
    // 获取验证码的地址
    storeData.loginAccount.captchaGetUrl = storeData.initData.captchaGetUrl; // 是否需要验证码

    storeData.loginAccount.isNeedCaptcha = storeData.initData.isNeedCaptcha || false; // logoSrc

    storeData.logoSrc = storeData.initData.logoSrc; // 忘记密码的跳转链接

    storeData.loginAccount.linkToFindPwd = storeData.initData.linkToFindPwd; // 登录失败的 后端返回的信息

    storeData.initData.loginErrorMsg = storeData.initData.loginErrorMsg;
  }

  function encryptMd5(password) {
    var output = "";
    var chr1,
        chr2,
        chr3 = "";
    var enc1,
        enc2,
        enc3,
        enc4 = "";
    var i = 0;

    do {
      chr1 = password.charCodeAt(i++);
      chr2 = password.charCodeAt(i++);
      chr3 = password.charCodeAt(i++);
      enc1 = chr1 >> 2;
      enc2 = (chr1 & 3) << 4 | chr2 >> 4;
      enc3 = (chr2 & 15) << 2 | chr3 >> 6;
      enc4 = chr3 & 63;

      if (isNaN(chr2)) {
        enc3 = enc4 = 64;
      } else if (isNaN(chr3)) {
        enc4 = 64;
      }

      output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2) + keyStr.charAt(enc3) + keyStr.charAt(enc4);
      chr1 = chr2 = chr3 = "";
      enc1 = enc2 = enc3 = enc4 = "";
    } while (i < password.length);

    return output;
  }
  /**
   * 加密的密码
   * @param {String} password - 需要加密的密码
   */


  function encryptPwd(password) {
    /* 有公钥 使用 rsa 加密, 否则使用 md5 加密 */
    if (storeData.initData.publicKey) {
      // 初始化加密器
      var encrypt = new JSEncrypt(); // 设置公钥

      encrypt.setPublicKey(storeData.initData.publicKey); // 加密

      return encrypt.encrypt(password);
    } else {
      return encryptMd5(password);
    }
  }

  function Throttle() {
    var time = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 200;
    var calling = false;
    var context;
    var args;
    var fn;

    function invokeSuccess() {
      calling = false;
    }

    return function (_fn) {
      fn = _fn;
      return function () {
        if (calling) {
          return;
        }

        calling = true;
        context = this;
        args = Array.prototype.slice.call(arguments);
        fn.apply(context, args);
        setTimeout(invokeSuccess, time);
      };
    };
  }

  $(".look").mousedown(function () {
    $('#password').attr("type", "text");
    $(".look").toggleClass("glyphicon-eye-open");
    $(".look").toggleClass("glyphicon-eye-close");
  });
  $(".look").mouseup(function () {
    $('#password').attr("type", "password");
    $(".look").toggleClass("glyphicon-eye-open");
    $(".look").toggleClass("glyphicon-eye-close");
  });
  $(".look").mouseout(function () {
    if ($(".look").hasClass("glyphicon-eye-open")) {
      $('#password').attr("type", "password");
      $(".look").toggleClass("glyphicon-eye-open");
      $(".look").toggleClass("glyphicon-eye-close");
    }
  });
  $('.btn-raised').click(function (e) {
    var _this = $(this);

    var px = e.offsetX;
    var py = e.offsetY - 37;
    var id = parseInt(Math.random() * 1000);

    _this.append('<div class="water-btn-style" style="top:' + py + 'px;left:' + px + 'px;background:rgba(0, 0, 0, 0.14)' + '" id="wb_' + id + '"></div>');

    setTimeout(function () {
      try {
        _this.find('#wb_' + id).remove();
      } catch (err) {
        // 兼容ie	
        _this.find('#wb_' + id).removeNode(true);
      }
    }, 1000);
  });
  $('#username').keyup(function () {
    validate('#username', '#bindFormAccount');
  });
  $('#password').keyup(function () {
    var flag = validate('#password', '#bindFormAccount');

    if (flag) {
      $(".look").css('display', 'block');
    } else {
      $(".look").css('display', 'none');
    }
  });
  $('#captcha').keyup(function () {
    validate('#captcha', '#bindFormAccount');
  });
  $(".bind-account-captcha").click(function () {
    updateCaptchaGetUrl();
  });
  $("#bindFormAccount").submit(function () {
    var flag = $("#bindFormAccount").valid();
    validate('#username', '#bindFormAccount');
    validate('#password', '#bindFormAccount');

    if (storeData.loginAccount.isNeedCaptcha) {
      validate('#captcha', '#bindFormAccount');
    }

    if (flag) {
      handleAccountLogin();
    }

    return false;
  });
  /** login-account */

  /**
   * 更新账号登录的state
   * @param {Object} patchState
   */

  function updateLoginAccount(patchState) {
    var loginAccount = storeData.loginAccount;
    storeData.loginAccount = Object.assign({}, loginAccount, patchState);
  }
  /**
   * 更新验证码
   */


  function _updateCaptchaGetUrl() {
    $(".bind-account-captcha-image").attr("src", storeData.initData.captchaGetUrl + '?t=' + new Date().getTime());
    updateLoginAccount({
      captchaGetUrl: storeData.initData.captchaGetUrl + '?t=' + new Date().getTime()
    });
  }

  var updateCaptchaGetUrl = Throttle()(_updateCaptchaGetUrl);
  /**
   * @param {Object[]} params - 提交参数
   * @param {string} params.type - 输入框类型
   * @param {string} params.name - 表单名
   * @param {string} params.value - 表单值
   * @param {Object} formProps - 表单属性
   * 创建表单及 提交
   */

  function createFormAndSubmit() {
    var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : [];
    var formProps = arguments.length > 1 ? arguments[1] : undefined;
    var callback = arguments.length > 2 ? arguments[2] : undefined;
    var temp = document.createElement("form");
    Object.keys(formProps).forEach(function (key) {
      temp.setAttribute(key, formProps[key]);
    });
    temp.style.display = "none";

    if (params.length > 0) {
      for (var i = 0; i < params.length; i++) {
        var opt = document.createElement("input");
        opt.name = params[i].name;
        opt.type = params[i].type; //防止IE浏览器将null 自动转换为"null" 导致错误

        if (params[i].value !== null) {
          opt.value = params[i].value || '';
        }

        temp.appendChild(opt);
      }
    }

    document.body.appendChild(temp);

    function submitAndCallback() {
      temp.submit();
      callback();
    }
    /**
     * 销毁创建表单
     */


    function destroy() {
      document.removeChild(temp);
    }

    submitAndCallback();
    return {
      destroy: destroy
    };
  }
  /**
   * 登录行为触发
   * @param {Event} e - 表单提交事件
   * @param {Object} form - 查询表单
   */


  function _handleAccountLogin() {
    var t = $("#bindFormAccount").serializeArray();
    var d = {};
    $.each(t, function () {
      d[this.name] = this.value;
    });
    var username = d.username,
        password = d.password,
        captcha = d.captcha;
        if(storeData.initData.accountEncrypt) {
          var newUsername = encryptPwd($.trim(username));
        } else {
          var newUsername = $.trim(username);
        }
        if(storeData.initData.passwordEncrypt) {
          var newPassword = encryptPwd($.trim(password));
        } else {
          var newPassword = $.trim(password);
        }
    updateLoginAccount({
      submitting: true
    });

    var _createFormAndSubmit = createFormAndSubmit([{
      name: 'username',
      value: newUsername,
      type: 'text'
    }, {
      name: 'password',
      value: newPassword,
      type: 'password'
    }, {
      name: 'captcha',
      value: captcha,
      type: 'text'
    }], {
      action: '/oauth/login',
      method: 'post',
      autocomplete: 'off'
    }, function () {
      updateLoginAccount({
        submitting: false
      });

      if (destroy) {
        destroy();
      }
    }),
        destroy = _createFormAndSubmit.destroy;
  }

  var handleAccountLogin = Throttle()(_handleAccountLogin);
  $(function () {
    var data = {};
    var $templateData = $('#templateData'); // 是否显示验证码

    data.isNeedCaptcha = $templateData.data('isneedcaptcha'); // logo图标

    data.logoSrc = $templateData.data('logosrc') || '/oauth/static/main/img/logo.png';
    data.copyright = $templateData.data('copyright') || 'Copyright © The HZERO Author®.All rights reserved.'; // 忘记密码跳转链接

    data.linkToFindPwd = $templateData.data('linktofindpwd'); // 登录错误信息

    data.loginErrorMsg = $templateData.data('loginerrormsg'); // 获取验证码图片

    data.captchaGetUrl = $templateData.data('captchageturl'); // 公钥

    data.publicKey = $templateData.data('publickey'); // need access after load

    data.redirectUserName = $('#login-hidden-defaultUserName').val();
    data.userNameMessage = $templateData.data('usernamemessage');
    data.passwordMessage = $templateData.data('passwordmessage');
    data.captchaMessage = $templateData.data('captchamessage');
    data.accountEncrypt = $templateData.data('accountencrypt') || false;
    data.passwordEncrypt = $templateData.data('passwordencrypt')|| false;
    storeData.initData = data;
    storeData.rootEl = $('#app').get(0);
    initInitialData();
    initValidate();
  });
});