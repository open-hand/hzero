function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance"); }

function _iterableToArrayLimit(arr, i) { if (!(Symbol.iterator in Object(arr) || Object.prototype.toString.call(arr) === "[object Arguments]")) { return; } var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

$(document).ready(function () {
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
    var keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
    var globalData = {};
    var $templateData = $('#templateData');
    globalData.loginUrl = $templateData.data('loginurl') || '/oauth/static/slide/img/logo.png';
    globalData.optionSucceeded = $templateData.data('optionsucceeded');
    globalData.optionFailed = $templateData.data('optionfailed');
    globalData.resetPassword = $templateData.data('resetpassword');
    globalData.complete = $templateData.data('complete');
    globalData.validateAccountErr = $templateData.data('validateaccounterr');
    globalData.validateAccountFailed = $templateData.data('validateaccountfailed');
    globalData.userNameNotNull = $templateData.data('usernamenotnull');
    globalData.findAccount = $templateData.data('findaccount');
    globalData.captchaPlaceholder = $templateData.data('captchaplaceholder');
    globalData.captchaMessage = $templateData.data('captchamessage');
    globalData.cancel = $templateData.data('cancel');
    globalData.nextStep = $templateData.data('nextstep');
    globalData.preStep = $templateData.data('prestep');
    globalData.validatePwdRepeat = $templateData.data('validatepwdrepeat');
    globalData.captchaLoadErrgMsg = $templateData.data('captchaloaderrgmsg');
    globalData.getCaptcha = $templateData.data('getcaptcha');
    globalData.newPwdCanNotNul = $templateData.data('newpwdcannotnul');
    globalData.resetPwdErr = $templateData.data('resetpwderr');
    globalData.resetPwd = $templateData.data('resetpwd');
    globalData.pwdRepeatMsg = $templateData.data('pwdrepeatmsg');
    globalData.resetPwdSucceeded = $templateData.data('resetpwdsucceeded');
    globalData.loadingNow = $templateData.data('loadingnow');
    globalData.pwdRepeat = $templateData.data('pwdrepeat');
    globalData.copyright = $templateData.data('copyright') || 'Copyright © The HZERO Author®.All rights reserved.';
    globalData.publicKey = $templateData.data('publickey'); //  验证邮箱正则
    globalData.organizationId = $templateData.data('orgid'); //  验证邮箱正则



    var message = {
        success: function success(config) {
            notification(Object.assign({
                placement: 'bottomRight',
                duration: 2,
                message: globalData.optionSucceeded,
                type: 'success',
            }, config));
        },
        error: function error(config) {
            notification(Object.assign({
                placement: 'bottomRight',
                duration: 2,
                message: globalData.optionFailed,
                type: 'error',
            }, config));
        }
    };

    function notification(params) {
        var message = params.message,
            duration = params.duration,
            type = params.type;
        var node = document.createElement("div");
        node.innerHTML = "<div class=\"password-alert\" role=\"alert\">\n    <span class=\"glyphicon ".concat(type == "success" ? 'glyphicon-ok' : 'glyphicon-exclamation-sign', "  password-notification-").concat(type, "\"></span>\n    <div class=\"password-notification-content\">").concat(message, "</div>\n    <span class=\"glyphicon glyphicon-remove\"></span>\n  </div>");
        $(".password-notification").append(node);
        setTimeout(function () {
            try {
                node.remove(node)
            } catch (err) {
                // 兼容ie									               
                node.removeNode(true);
            }
        }, duration * 1000);
    }

    $(document).on("click", ".password-alert .glyphicon-remove", function (e) {
        try {
            e.target.parentNode.remove();
        } catch (err) {
            // 兼容ie	
            e.target.parentNode.removeNode(true);
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

    function validate(selector, form) {
        var flag = $(form).validate().element($(selector));

        if (flag) {
            $(selector).parents(".password-input-content").removeClass("has-error");
        } else {
            $(selector).parents(".password-input-content").addClass("has-error");
        }

        return flag;
    }

    function setLocalStorage(key, value) {
        localStorage.setItem('oauth-password_modify' + key, JSON.stringify(value));
    }

    function getLocalStorage(key) {
        var value = localStorage.getItem('oauth-password_modify' + key);
        return value ? JSON.parse(value) : undefined;
    }

    function passwordValidate() {
        var _ref = getLocalStorage('passwordTipMsg') || {},
            digitsCount = _ref.digitsCount,
            lowercaseCount = _ref.lowercaseCount,
            maxLength = _ref.maxLength,
            minLength = _ref.minLength,
            notUsername = _ref.notUsername,
            specialCharCount = _ref.specialCharCount,
            uppercaseCount = _ref.uppercaseCount;

        var msg = '';
        var allFlag = false;
        var value = $("#password").val();
        var loginName = getLocalStorage('loginName');
        var node = document.createElement("div"); // node.className = "password-validate"

        if (minLength && maxLength) {
            var flag = value.length < minLength || value.length > maxLength;
            allFlag = !flag;

            if (!flag) {
                msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(minLength, "-").concat(maxLength, " ").concat(storeData.initData.validateLength, "</span>\n      </div>");
            } else {
                msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(minLength, "-").concat(maxLength, " ").concat(storeData.initData.validateLength, "</span>\n      </div>");
            }
        }

        if (lowercaseCount) {
            var regStr = [];

            for (var i = 0; i < lowercaseCount; i++) {
                regStr.push('([a-z].*)');
            }

            var lower = new RegExp("".concat(regStr.join('')));

            var _flag = lower.test(value);

            allFlag = _flag && allFlag;

            if (_flag) {
                msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(lowercaseCount, " ").concat(storeData.initData.validateLower, "</span>\n      </div>");
            } else {
                msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(lowercaseCount, " ").concat(storeData.initData.validateLower, "</span>\n      </div>");
            }
        }

        if (uppercaseCount) {
            var _regStr = [];

            for (var _i = 0; _i < uppercaseCount; _i++) {
                _regStr.push('([A-Z].*)');
            }

            var upper = new RegExp("".concat(_regStr.join('')));

            var _flag2 = upper.test(value);

            allFlag = _flag2 && allFlag;

            if (_flag2) {
                msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(uppercaseCount, " ").concat(storeData.initData.validateUpper, "</span>\n      </div>");
            } else {
                msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(uppercaseCount, " ").concat(storeData.initData.validateUpper, "</span>\n      </div>");
            }
        }

        if (notUsername === false) {
            var _flag3 = value === loginName;

            allFlag = !_flag3 && allFlag;

            if (!_flag3) {
                msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.validateUserName, "</span>\n      </div>");
            } else {
                msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.validateUserName, "</span>\n      </div>");
            }
        }

        if (digitsCount) {
            var _regStr2 = [];

            for (var _i2 = 0; _i2 < digitsCount; _i2++) {
                _regStr2.push('([0-9].*)');
            }

            var digits = new RegExp("".concat(_regStr2.join('')));

            var _flag4 = digits.test(value);

            allFlag = _flag4 && allFlag;

            if (_flag4) {
                msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(digitsCount, " ").concat(storeData.initData.validateDigit, "</span>\n      </div>");
            } else {
                msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(digitsCount, " ").concat(storeData.initData.validateDigit, "</span>\n      </div>");
            }
        }

        if (specialCharCount) {
            var _regStr3 = [];

            for (var _i3 = 0; _i3 < specialCharCount; _i3++) {
                _regStr3.push('([~`@#$%^&*/\\-_=+|/()<>,.;:!].*)');
            }

            var special = new RegExp("".concat(_regStr3.join('')));

            var _flag5 = special.test(value);

            allFlag = _flag5 && allFlag;

            if (_flag5) {
                msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(specialCharCount, " ").concat(storeData.initData.validateSpecial, "</span>\n      </div>");
            } else {
                msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(specialCharCount, " ").concat(storeData.initData.validateSpecial, "</span>\n      </div>");
            }
        }

        if (msg.length == 0 && !allFlag) {
            allFlag = true;
        }

        if (msg.length > 0 && !allFlag && value) {
            node.innerHTML = msg;
            $('.password-validate').html(node);
            $('.password-validate').addClass("password-validate-active");
        } else {
            $('.password-validate').html("<div></div>");
            $('.password-validate').removeClass("password-validate-active");
        }

        return allFlag;
    }


    function encryptPwd(password) {
        /* 有公钥 使用 rsa 加密, 否则使用 md5 加密 */
        if (globalData.publicKey) {
            // 初始化加密器
            var encrypt = new JSEncrypt(); // 设置公钥

            encrypt.setPublicKey(globalData.publicKey); // 加密

            return encrypt.encrypt(password);
        } else {
            return password;
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





    function initValidate() {
        jQuery.validator.addMethod("isSame", function (value) {
            return value == $('#password').val();
        }, globalData.validatePwdRepeat);
        $('#formResetPassword').validate({
            errorPlacement: function errorPlacement(error, element) {
                error.addClass("has-error has-error-msg");
                error.appendTo(element.parent());
            },
            onkeyup: false,
            onclick: false,
            onfocusout: false,
            onsubmit: false,
            rules: {
                password: "required",
                passwordRepeat: {
                    required: true,
                    isSame: true
                },
        phoneCaptcha: "required",
        phone: "required"
    },
            messages: {
                passwordRepeat: {
                    required: globalData.pwdRepeatMsg
                },
                password: globalData.newPwdCanNotNul,
        phoneCaptcha: storeData.initData.captchaMessage,
        phone: storeData.initData.phoneMsg,
    }
        });

    } // 由于 这边只会有一个组件的实例, 且 babel 转化的 class 有问题, 所以state放在这边


    var storeData = {
        initData: {},
        // 初始值
        rootEl: null,
    loginPhone: {},
        // 挂载的节点
        password: {
            steps: [{
                title: globalData.resetPassword
            }, {
                title: globalData.complete
            }],
            currentStep: 0
        } // 整体数据

    };




    $('#phone').keyup(function () {
        validate('#phone', '#formResetPassword');

    });
    $('#phoneCaptcha').keyup(function () {
        validate('#phoneCaptcha', '#formResetPassword');

    });

    $('#password').keyup(function () {
        if ($('#passwordRepeat').val()) {
            validate('#passwordRepeat', '#formResetPassword');
        }

        passwordValidate();
        var flag = validate('#password', '#formResetPassword');

        if (flag) {
            $(".look:first").css('display', 'block');
        } else {
            $(".look:first").css('display', 'none');
        }
    });
    $('#passwordRepeat').keyup(function () {
        validate('#passwordRepeat', '#formResetPassword');
        var flag = $('#passwordRepeat').val();

        if (flag) {
            $(".look:last").css('display', 'block');
        } else {
            $(".look:last").css('display', 'none');
        }
    });

    function toggleLook(selector) {
        $(selector).toggleClass("glyphicon-eye-open");
        $(selector).toggleClass("glyphicon-eye-close");
    }

    $(".look:first").mousedown(function () {
        $('#password').attr("type", "text");
        toggleLook(".look:first");
    });
    $(".look:first").mouseup(function () {
        $('#password').attr("type", "password");
        toggleLook(".look:first");
    });
    $(".look:first").mouseout(function () {
        if ($(".look:first").hasClass("glyphicon-eye-open")) {
            $('#password').attr("type", "password");
            toggleLook(".look:first");
        }
    });
    $(".look:last").mousedown(function () {
        $('#passwordRepeat').attr("type", "text");
        $(".look:last").toggleClass("glyphicon-eye-open");
    });
    $(".look:last").mouseup(function () {
        $('#passwordRepeat').attr("type", "password");
        $(".look:last").toggleClass("glyphicon-eye-open");
    });
    $(".look:last").mouseout(function () {
        if ($(".look:last").hasClass("glyphicon-eye-open")) {
            $('#passwordRepeat').attr("type", "password");
            $(".look:last").toggleClass("glyphicon-eye-open");
        }
    });

    $("#formResetPassword").submit(function (e) {
        return false;
    });
    $("#resetPasswordSuccess").submit(function (e) {
        return false;
    });

    $(".password-btn-confirm-account").click(function (e) {
        var flag = $("#formResetPassword").valid();
        var allFlag = passwordValidate();
        validate('#password', '#formResetPassword');
        validate('#passwordRepeat', '#formResetPassword');
        var forceCodeVerify = storeData.initData.forceCodeVerify;
        if(forceCodeVerify){
            validate('#phoneCaptcha', '#formResetPassword');
        }
        if (flag && allFlag) {
            resetPassword();
        }
    });

    $(".button-login").click(function () {
        backLoginPage();
    });
    /**
     * 初始化 App 数据
     */

    function initInitialData() {
        storeData.initData.successImgSrc = '/oauth/static/slide/img/illustrate_success.png'; // 成功图片地址
        var nowDate = new Date().getTime();
        var captchaDate = getLocalStorage('captchaTime-date', new Date().getTime()) || nowDate;
        var captchaTime = getLocalStorage('captchaTime') || 0;
        storeData.loginPhone.captchaTime = Math.floor(captchaTime - (nowDate - captchaDate) / 1000);
        startLoginPhoneCaptchaTime();
        storeData.loginPhone.captchaKey = getLocalStorage('captchaKey');
        var params = location.search.substr(1).split('&').reduce(function (prev, cur) {
            var _cur$split = cur.split('='),
                _cur$split2 = _slicedToArray(_cur$split, 2),
                name = _cur$split2[0],
                value = _cur$split2[1];

            prev[decodeURIComponent(name)] = decodeURIComponent(value);
            return prev;
        }, {});
        Object.assign(storeData.password, params); // 处理一些类型问题
        storeData.password.currentStep = +storeData.password.currentStep || 0;

        

    }

    function initQuery() {
        if (storeData.password.currentStep == 0) {
            if (!globalData.organizationId && globalData.organizationId != 0) {
                backLoginPage()
            } else {
                $.ajax({
                    url: "/iam/v1/".concat(globalData.organizationId, "/password-policies/query"),
                    type: 'get',
                    success: function success(data) {
                        if (data.failed == true) {
                            message.error({
                                message: data.message
                            });

                        } else {
                            setLocalStorage('passwordTipMsg', data);
                        }
                    },
                    error: function error() { }
                });
            }
        }
    }



    /**
     * 回退到登录页面
     */

    function backLoginPage() {
        window.location.href = storeData.initData.loginUrl + location.search.replace('?', "&");
    }




    /**
     * 重置密码
     * @param {Object} form
     * @param {String} captchaKey
     */


    function resetPassword() {
        var t = $("#formResetPassword").serializeArray();
        var d = {};
        $.each(t, function () {
            d[this.name] = this.value;
        });
        var password = d.password,
            phoneCaptcha = d.phoneCaptcha;
        var captchaKey = storeData.loginPhone.captchaKey;
        var phone = storeData.initData.phone;
        var forceCodeVerify = storeData.initData.forceCodeVerify;
        var newPassword = encryptPwd($.trim(password));
        $.ajax({
            url: '/oauth/password/force-modify',
            type: 'POST',
            dataType: 'json',
            data: forceCodeVerify?{
                password: newPassword,
                captcha: phoneCaptcha,
                captchaKey,
                phone,
                businessScope: 'MODIFY_PASSWORD',
            }:{
                password: newPassword,
                captcha: phoneCaptcha,
                captchaKey,
                businessScope: 'MODIFY_PASSWORD',
            },
            success: function success(data) {
                if (data.success) {
                    window.location.href = '/oauth/public/slide/password_modify.html?currentStep=1';
                } else {
                    switch (data.code) {
                        default:
                            message.error({
                                message: data.msg || data.message
                            });
                            break;
                    }
                }
            },
            error: function error(err) {
                message.error({
                    message: globalData.optionFailed
                });
            }
        });
    }

    $(".password-form-button-captcha").click(function () {
      var flag =  validate('#phone', '#formResetPassword');
      

      if(flag){
        sendCaptcha(storeData.loginPhone.captchaKey);
      }
    
        return false;
      });

        /**
   * 更新手机号/邮箱登录的state
   * @param {Object} patchState
   */

  function updateLoginPhone(patchState) {
    var loginPhone = storeData.loginPhone;
    storeData.loginPhone = Object.assign({}, loginPhone, patchState);
  }

  /**
   *  发送验证吗
   */

  function _sendCaptcha() {
    updateLoginPhone({
      captchaLoading: true
    });
    $(".password-form-button-captcha").attr("disabled", "disabled");
    $(".password-form-button-captcha").text("..." + storeData.initData.captchaLoadingMsg);

    var t = $("#formResetPassword").serializeArray();
    var d = {};
    $.each(t, function () {
      d[this.name] = this.value;
    });
    var phone = storeData.initData.phone || d.phone;
    $.ajax('/oauth/public/force-modify-pwd/send-captcha', {
      false: 'false',
      method: 'GET',
      data: phone.includes('*')?{
        businessScope: 'MODIFY_PASSWORD',
      }:{
        phone: phone,
        businessScope: 'MODIFY_PASSWORD',
      },
      success: function success(data) {
        if (data.success) {
          message.success({
            message: data.msg || data.message
          });
          updateLoginPhone({
            captchaKey: data.data,
            captchaLoading: false,
            captchaTime: data.interval || 60
          });
          $(".password-form-button-captcha").removeAttr("disabled");
          $(".password-form-button-captcha").text(storeData.initData.captchaLoadingMsg);
          setLocalStorage('captchaKey', data.data);
          setLocalStorage('captchaTime', data.interval || 60);
          setLocalStorage('captchaTime-date', new Date().getTime());
          startLoginPhoneCaptchaTime();
        } else {
          message.error({
            message: data.msg || data.message
          }); // 没有办法知道具体的 计时, 所以直接使用 60

          switch (data.code) {
            // case 'captcha.send.interval':
            default:
              var nextCaptchaTime = data.interval > 0 ? data.interval : 0;

              if ((getLocalStorage('captchaTime') || 0) == 0) {
                updateLoginPhone({
                  captchaLoading: false,
                  captchaTime: nextCaptchaTime
                });
                $(".password-form-button-captcha").removeAttr("disabled");
                $(".password-form-button-captcha").text(storeData.initData.captchaLoadingMsg);
                setLocalStorage('captchaTime', nextCaptchaTime);
                setLocalStorage('captchaTime-date', new Date().getTime());
                clearInterval(loginPhoneCaptchaTimer);
                startLoginPhoneCaptchaTime();
              }

              break;
          }
        }
      },
      error: function error() {
        message.error({
          message: storeData.initData.captchaLoadErrgMsg
        });
        $(".password-form-button-captcha").removeAttr("disabled");
        $(".password-form-button-captcha").text(storeData.initData.captchaLoadingMsg);
      }
    });
  }

  var sendCaptcha = Throttle()(_sendCaptcha);
  /**
   * 手机号发送验证码的计时器
   */

  var loginPhoneCaptchaTimer;
  /**
   * 定时更新 loginPhone 的state
   */

  function updateLoginPhoneCaptchaTime() {
    var loginPhone = storeData.loginPhone;
    var _loginPhone$captchaTi = loginPhone.captchaTime,
      captchaTime = _loginPhone$captchaTi === void 0 ? 0 : _loginPhone$captchaTi;

    if (captchaTime <= 0) {
      stopLoginPhoneCaptchaTime();
    } else {
      var newCaptchaTime = (captchaTime || 0) - 1;
      setLocalStorage('captchaTime', newCaptchaTime);
      setLocalStorage('captchaTime-date', new Date().getTime());
      updateLoginPhone({
        captchaTime: newCaptchaTime
      });
      $(".password-form-button-captcha").attr("disabled", "disabled");
      $(".password-form-button-captcha").text(captchaTime - 1 + 's');
    }
  }


      /**
   * 开始倒计时, 需要先更新 captchaTime
   */


  function startLoginPhoneCaptchaTime() {
    loginPhoneCaptchaTimer = setInterval(updateLoginPhoneCaptchaTime, 1000);
  }
  /**
   * 结束倒计时
   */


  function stopLoginPhoneCaptchaTime() {
    $(".password-form-button-captcha").removeAttr("disabled");
    $(".password-form-button-captcha").text(storeData.initData.captchaLoadingMsg);
    clearInterval(loginPhoneCaptchaTimer);
  }


    /**
     * 页面加载完成 执行 render 渲染页面
     */


    $(function () {
        var data = {};
        var $templateData = $('#templateData');
        data.loginUrl = $templateData.data('loginurl');
        data.optionSucceeded = $templateData.data('optionsucceeded');
        data.optionFailed = $templateData.data('optionfailed');
        data.resetPassword = $templateData.data('resetpassword');
        data.complete = $templateData.data('complete');
        data.validateAccountErr = $templateData.data('validateaccounterr');
        data.validateAccountFailed = $templateData.data('validateaccountfailed');
        data.userNameNotNull = $templateData.data('usernamenotnull');
        data.findAccount = $templateData.data('findaccount');
        data.captchaPlaceholder = $templateData.data('captchaplaceholder');
        data.captchaMessage = $templateData.data('captchamessage');
        data.cancel = $templateData.data('cancel');
        data.nextStep = $templateData.data('nextstep');
        data.preStep = $templateData.data('prestep');
        data.validatePwdRepeat = $templateData.data('validatepwdrepeat');
        data.captchaLoadErrgMsg = $templateData.data('captchaloaderrgmsg');
        data.getCaptcha = $templateData.data('getcaptcha');
        data.newPwdCanNotNul = $templateData.data('newpwdcannotnul');
        data.resetPwdErr = $templateData.data('resetpwderr');
        data.resetPwd = $templateData.data('resetpwd');
        data.pwdRepeatMsg = $templateData.data('pwdrepeatmsg');
        data.resetPwdSucceeded = $templateData.data('resetpwdsucceeded');
        data.loadingNow = $templateData.data('loadingnow');
        data.pwdRepeat = $templateData.data('pwdrepeat');
        data.logoSrc = $templateData.data('logosrc') || '/oauth/static/slide/img/logo.png';
        data.validateLength = $templateData.data('validatelength');
        data.validateLower = $templateData.data('validatelower');
        data.validateUpper = $templateData.data('validateupper');
        data.validateDigit = $templateData.data('validatedigit');
        data.validateSpecial = $templateData.data('validatespecial');
        data.atLeast = $templateData.data('atleast');
        data.validateUserName = $templateData.data('validateusername'); // // copyright
        data.organizationId = $templateData.data('orgId');
        data.forceCodeVerify = $templateData.data('forceCodeVerify');
        data.phone = $templateData.data('phone') || '';
        data.captchaLoadErrgMsg = $templateData.data('captchaloaderrgmsg');
    data.phoneMsg = $templateData.data('phonemsg');
    data.captchaLoadingMsg = $templateData.data('captchaloadingmsg');
        // data.copyright = $templateData.data('copyright') || 'Copyright © The HZERO Author®.All rights reserved.';
        
        storeData.initData = data;
        storeData.rootEl = $('#app').get(0);
        initInitialData();
        initQuery();
        initValidate();
    });
});