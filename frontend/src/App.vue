<template>
  <div id="app">
    <header v-if="isLoggedIn">
      <nav>
        <router-link to="/chefs">厨师列表</router-link>
        <router-link to="/dishes">菜单</router-link>
        <router-link to="/cart">购物车</router-link>
        <router-link to="/order">下单</router-link>
        <router-link to="/orders">我的订单</router-link>
        <router-link to="/wallet" class="balance-link">余额 ¥{{ balance.toFixed(2) }}</router-link>
        <a @click="logout" href="javascript:;">退出登录</a>
      </nav>
      <div class="user-info">欢迎，{{ username }}</div>
    </header>

    <!-- 充值弹窗 -->
    <div v-if="showRechargeModal" class="modal" @click.self="showRechargeModal = false">
      <div class="modal-content">
        <h3>充值</h3>
        <div class="contact-info">
          <p>充值后请拨打：<strong>18370598382</strong></p>
          <p>通知管理员您充值了多少钱</p>
        </div>
        <div class="qr-codes">
          <div class="qr-code">
            <img src="/src/assets/wechat-qrcode.png" alt="微信支付">
            <p>微信支付</p>
          </div>
          <div class="qr-code">
            <img src="/src/assets/alipay-qrcode.png" alt="支付宝支付">
            <p>支付宝支付</p>
          </div>
        </div>
        <div class="form-item">
          <label>充值金额：</label>
          <input type="number" v-model="rechargeAmount" placeholder="请输入实际充值金额">
        </div>
        <button @click="confirmRecharge">确认充值</button>
        <button @click="showRechargeModal = false" style="background: #999;">关闭</button>
        <div :class="['result', rechargeResult.type]" v-if="rechargeResult.message">
          {{ rechargeResult.message }}
        </div>
      </div>
    </div>

    <main>
      <router-view @login-success="handleLoginSuccess" />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      isLoggedIn: false,
      username: '',
      balance: 0,
      showRechargeModal: false,
      rechargeAmount: '',
      rechargeResult: { type: '', message: '' }
    }
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user.id) {
      this.isLoggedIn = true
      this.username = user.username || '用户'
      this.balance = user.balance || 0
    }
    window.addEventListener('open-recharge', this.showRecharge)
    window.addEventListener('balance-updated', this.handleBalanceUpdated)
  },
  beforeUnmount() {
    window.removeEventListener('open-recharge', this.showRecharge)
    window.removeEventListener('balance-updated', this.handleBalanceUpdated)
  },
  methods: {
    handleLoginSuccess(user) {
      this.isLoggedIn = true
      this.username = user.username
      this.balance = user.balance || 0
    },
    handleBalanceUpdated(event) {
      this.balance = Number(event.detail || 0)
    },
    logout() {
      localStorage.removeItem('user')
      this.isLoggedIn = false
      this.username = ''
      this.balance = 0
      this.$router.push('/')
    },
    showRecharge() {
      this.showRechargeModal = true
      this.rechargeAmount = ''
      this.rechargeResult = { type: '', message: '' }
    },
    async confirmRecharge() {
      // 验证金额是否大于0
      if (!this.rechargeAmount || parseFloat(this.rechargeAmount) <= 0) {
        this.rechargeResult = { type: 'error', message: '充值金额必须大于0' }
        return
      }
      
      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        const res = await this.$axios.post(`/user/${user.id}/confirmRecharge`, {
          amount: this.rechargeAmount
        })
        if (res.data.code === 200) {
          this.rechargeResult = { type: 'success', message: '充值申请已提交，请等待管理员确认' }

          setTimeout(() => {
            this.showRechargeModal = false
          }, 1500)
        } else {
          this.rechargeResult = { type: 'error', message: res.data.msg }
        }
      } catch (err) {
        this.rechargeResult = { type: 'error', message: '充值失败' }
      }
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Microsoft Yahei", sans-serif;
}
body {
  background: #f5f5f5;
  min-height: 100vh;
}
#app {
  max-width: 1040px;
  margin: 0 auto;
  padding: 20px;
}
header {
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
nav {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 10px;
}
nav a {
  color: #409eff;
  text-decoration: none;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}
nav a:hover,
nav a.router-link-active {
  background: #409eff;
  color: #fff;
}
.user-info {
  color: #666;
  font-size: 14px;
}
.balance-link {
  color: #67c23a;
  font-weight: bold;
}
.balance-link:hover {
  background: #67c23a;
  color: #fff;
}
.card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}
h2 {
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}
h3 {
  color: #409eff;
  margin: 15px 0;
  font-size: 24px;
}
.form-item {
  margin: 15px 0;
  display: flex;
  align-items: center;
}
label {
  width: 80px;
  font-size: 14px;
  color: #666;
}
input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}
input:focus {
  outline: none;
  border-color: #409eff;
}
button {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-top: 10px;
  transition: background 0.3s;
}
button:hover {
  background: #66b1ff;
}
button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.result {
  margin-top: 15px;
  padding: 12px;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.5;
}
.success {
  background: #f0f9eb;
  color: #67c23a;
}
.error {
  background: #fef0f0;
  color: #f56c6c;
}
.log {
  margin-top: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}
.list-item {
  padding: 12px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.3s;
}
.list-item:hover {
  background: #f5f7fa;
}
.list-item:last-child {
  border-bottom: none;
}
.list-item.active {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
}
.empty {
  text-align: center;
  color: #999;
  padding: 20px;
}
.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.status-pending {
  background: #e6a23c;
  color: #fff;
}
.status-cooking {
  background: #409eff;
  color: #fff;
}
.status-completed {
  background: #67c23a;
  color: #fff;
}
.status-cancelled {
  background: #f56c6c;
  color: #fff;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 40px;
  border-radius: 16px;
  width: 95%;
  max-width: 600px;
}

.qr-codes {
  display: flex;
  justify-content: center;
  gap: 50px;
  margin: 30px 0;
}

.qr-code {
  text-align: center;
}

.qr-code img {
  width: 220px;
  height: 220px;
}

.qr-code p {
  margin-top: 15px;
  color: #666;
  font-size: 18px;
}

.contact-info {
  text-align: center;
  padding: 25px;
  background: #fff3e0;
  border-radius: 12px;
  margin-top: 25px;
}

.contact-info p {
  margin: 10px 0;
  color: #e6a23c;
  font-size: 18px;
}

.contact-info strong {
  color: #f56c6c;
  font-size: 22px;
}
</style>
