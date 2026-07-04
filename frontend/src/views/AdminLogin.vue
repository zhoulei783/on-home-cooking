<template>
  <div class="admin-login">
    <div class="card">
      <h2>管理员登录</h2>
      <form @submit.prevent="adminLogin">
        <div class="form-item">
          <label for="phone">管理员账号：</label>
          <input type="text" id="phone" name="phone" v-model="phone" placeholder="请输入管理员手机号" autocomplete="tel">
        </div>
        <div class="form-item">
          <label for="password">密码：</label>
          <input type="password" id="password" name="password" v-model="password" placeholder="请输入密码" autocomplete="current-password">
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '管理员登录' }}
        </button>
      </form>
      <div :class="['result', result.type]" v-if="result.message">
        {{ result.message }}
      </div>
      <div class="back-link">
        <router-link to="/">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AdminLogin',
  data() {
    return {
      phone: '',
      password: '',
      loading: false,
      result: { type: '', message: '' }
    }
  },
  methods: {
    async adminLogin() {
      if (!this.phone || !this.password) {
        this.result = {type: 'error', message: '请输入账号和密码'}
        return
      }

      this.loading = true
      this.result = {type: '', message: ''}

      try {
          const res = await this.$axios.post('user/adminAuth/login', {
          phone: this.phone,
          password: this.password
        })

        if (res.data.code === 200) {
          this.result = {
            type: 'success',
            message: `✅ 管理员登录成功！`
          }
          localStorage.setItem('admin', JSON.stringify(res.data.data))
          setTimeout(() => {
            this.$router.push('/admin-dashboard')
          }, 1500)
        } else {
          this.result = {type: 'error', message: `❌ 登录失败：${res.data.msg}`}
        }
      } catch (err) {
        this.result = {type: 'error', message: `❌ 登录异常：${err.message}`}
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.admin-login {
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