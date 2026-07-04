<template>
  <div class="register">
    <div class="card">
      <h2>用户注册</h2>
      <form @submit.prevent="register">
        <div class="form-item">
          <label for="username">用户名：</label>
          <input type="text" id="username" v-model="username" placeholder="请输入用户名" autocomplete="name">
        </div>
        <div class="form-item">
          <label for="phone">手机号：</label>
          <input type="text" id="phone" v-model="phone" placeholder="请输入手机号" autocomplete="tel">
        </div>
        <div class="form-item">
          <label for="password">密码：</label>
          <input type="password" id="password" v-model="password" placeholder="请输入密码" autocomplete="new-password">
        </div>
        <div class="form-item">
          <label for="confirmPassword">确认密码：</label>
          <input type="password" id="confirmPassword" v-model="confirmPassword" placeholder="请再次输入密码" autocomplete="new-password">
        </div>
        <div class="form-item">
          <label for="address">地址：</label>
          <input type="text" id="address" v-model="address" placeholder="请输入地址" autocomplete="street-address">
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '注册中...' : '立即注册' }}
        </button>
      </form>
      <div :class="['result', result.type]" v-if="result.message">
        {{ result.message }}
      </div>
      <div class="back-link">
        <router-link to="/login">已有账号？立即登录</router-link>
      </div>
      <div class="back-link">
        <router-link to="/">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      username: '',
      phone: '',
      password: '',
      confirmPassword: '',
      address: '',
      loading: false,
      result: { type: '', message: '' }
    }
  },
  methods: {
    async register() {
      if (!this.username || !this.phone || !this.password) {
        this.result = { type: 'error', message: '请填写所有字段' }
        return
      }
      if (this.password !== this.confirmPassword) {
        this.result = { type: 'error', message: '两次密码输入不一致' }
        return
      }

      this.loading = true
      this.result = { type: '', message: '' }

      try {
        const res = await this.$axios.post('/user/register', {
          username: this.username,
          phone: this.phone,
          password: this.password,
          address: this.address
        })

        if (res.data.code === 200) {
          this.result = {
            type: 'success',
            message: '✅ 注册成功！即将跳转到登录页面...'
          }
          setTimeout(() => {
            this.$router.push('/login')
          }, 1500)
        } else {
          this.result = { type: 'error', message: `❌ 注册失败：${res.data.msg}` }
        }
      } catch (err) {
        this.result = { type: 'error', message: `❌ 注册异常：${err.message}` }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.register {
  max-width: 400px;
  margin: 0 auto;
}

.back-link {
  text-align: center;
  margin-top: 15px;
}

.back-link a {
  color: #409eff;
  text-decoration: none;
}
</style>